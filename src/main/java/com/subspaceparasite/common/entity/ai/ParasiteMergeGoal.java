package com.subspaceparasite.common.entity.ai;

import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.common.entity.base.MergeSystem;
import com.subspaceparasite.config.ModConfigSystems;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;
import java.util.List;

/**
 * AI goal that causes compatible parasites to seek out and merge with
 * nearby merge partners. This implements the entity-level merge mechanic
 * from the original Scape and Run: Parasites mod.
 * <p>
 * Behavior flow:
 * <ol>
 *   <li>Periodically checks for compatible merge partners within search range</li>
 *   <li>If a partner is found and merge chance roll succeeds, moves toward the partner</li>
 *   <li>When close enough (within proximity range), triggers the merge</li>
 *   <li>The merge is handled by {@link MergeSystem#performMerge}</li>
 * </ol>
 * <p>
 * Merge is not automatic — it has a probability per check based on the
 * evolution phase, controlled by configuration values.
 *
 * @author SubspaceParasite Team
 * @since 1.0.0
 */
public class ParasiteMergeGoal extends Goal {

    protected final EntityParasiteBase parasite;
    protected final double moveSpeed;
    protected final double searchRange;
    protected final double proximityRange;

    /** The current merge partner being approached. */
    protected EntityParasiteBase mergePartner;

    /** Timer for periodic partner search (avoids scanning every tick). */
    protected int searchCooldown;

    /** Timer for merge chance re-rolling. */
    protected int mergeCheckCooldown;

    /** Whether a merge attempt has been approved for this partner. */
    protected boolean mergeApproved;

    /** Ticks spent approaching current partner (timeout to avoid getting stuck). */
    protected int approachTicks;

    /** Maximum ticks to spend approaching before giving up. */
    protected static final int MAX_APPROACH_TICKS = 200;

    public ParasiteMergeGoal(EntityParasiteBase parasite) {
        this(parasite, 1.0D);
    }

    public ParasiteMergeGoal(EntityParasiteBase parasite, double moveSpeed) {
        this.parasite = parasite;
        this.moveSpeed = moveSpeed;
        this.searchRange = ModConfigSystems.getMergeSearchRange();
        this.proximityRange = ModConfigSystems.getMergeProximityRange();
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    // ========== Goal Lifecycle ==========

    @Override
    public boolean canUse() {
        if (!ModConfigSystems.isMergeEnabled()) return false;
        if (!this.parasite.isAlive()) return false;
        if (this.parasite.getMergeCooldown() > 0) return false;
        if (this.parasite.getParasiteType() == null) return false;
        if (this.parasite.getTarget() != null) return false; // Don't merge while fighting

        // Only check periodically
        if (this.searchCooldown > 0) {
            this.searchCooldown--;
            return false;
        }

        // Reset search cooldown
        this.searchCooldown = ModConfigSystems.getMergeCheckInterval()
                + this.parasite.getRandom().nextInt(40);

        // Look for a compatible merge partner
        this.mergePartner = findMergePartner();
        return this.mergePartner != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (!ModConfigSystems.isMergeEnabled()) return false;
        if (this.mergePartner == null || !this.mergePartner.isAlive()) return false;
        if (this.parasite.getMergeCooldown() > 0) return false;
        if (this.parasite.getTarget() != null) return false;
        if (this.approachTicks > MAX_APPROACH_TICKS) return false;

        // Check if partner is still within reasonable range
        double distSq = this.parasite.distanceToSqr(this.mergePartner);
        double maxRange = this.searchRange * 3.0; // Allow some leeway
        return distSq < maxRange * maxRange;
    }

    @Override
    public void start() {
        this.approachTicks = 0;
        this.mergeApproved = false;
        this.mergeCheckCooldown = 0;

        // Roll merge chance immediately on start
        float chance = MergeSystem.getInstance().calculateMergeChance(this.parasite);
        this.mergeApproved = this.parasite.getRandom().nextFloat() < chance;
    }

    @Override
    public void stop() {
        this.mergePartner = null;
        this.mergeApproved = false;
        this.approachTicks = 0;
        this.parasite.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.mergePartner == null) return;

        this.approachTicks++;

        // Move toward the merge partner
        this.parasite.getNavigation().moveTo(this.mergePartner, this.moveSpeed);
        this.parasite.getLookControl().setLookAt(this.mergePartner, 30.0F, 30.0F);

        // Check proximity for merge trigger
        double distSq = this.parasite.distanceToSqr(this.mergePartner);
        double proximitySq = this.proximityRange * this.proximityRange;

        if (distSq <= proximitySq) {
            // Close enough — attempt the merge
            if (this.mergeApproved) {
                MergeSystem.getInstance().performMerge(this.parasite, this.mergePartner);
                // Both entities are consumed by the merge, so we're done
            } else {
                // Not approved — re-roll merge chance occasionally
                this.mergeCheckCooldown--;
                if (this.mergeCheckCooldown <= 0) {
                    this.mergeCheckCooldown = 20;
                    float chance = MergeSystem.getInstance().calculateMergeChance(this.parasite);
                    this.mergeApproved = this.parasite.getRandom().nextFloat() < chance;
                }
            }
        }
    }

    // ========== Partner Search ==========

    /**
     * Finds the closest compatible merge partner within search range.
     * Prioritizes same-type partners for standard tier merges,
     * then checks for specific recipe matches.
     *
     * @return the closest compatible merge partner, or null if none found
     */
    protected EntityParasiteBase findMergePartner() {
        if (!(this.parasite.level() instanceof net.minecraft.server.level.ServerLevel)) {
            return null;
        }

        double searchRangeSq = this.searchRange * this.searchRange;
        AABB searchArea = this.parasite.getBoundingBox().inflate(this.searchRange);

        List<EntityParasiteBase> nearby = this.parasite.level().getEntitiesOfClass(
                EntityParasiteBase.class, searchArea,
                p -> p != this.parasite
                        && p.isAlive()
                        && p.getMergeCooldown() <= 0
                        && p.getParasiteType() != null
                        && p.getTarget() == null
                        && p.distanceToSqr(this.parasite) <= searchRangeSq);

        if (nearby.isEmpty()) return null;

        EntityParasiteBase bestPartner = null;
        double bestDist = Double.MAX_VALUE;

        MergeSystem mergeSystem = MergeSystem.getInstance();

        for (EntityParasiteBase candidate : nearby) {
            if (mergeSystem.canMergeWith(this.parasite, candidate)) {
                double dist = this.parasite.distanceToSqr(candidate);
                if (dist < bestDist) {
                    bestDist = dist;
                    bestPartner = candidate;
                }
            }
        }

        return bestPartner;
    }
}
