package com.subspaceparasite.common.entity.ai;

import java.util.EnumSet;
import java.util.List;

import com.subspaceparasite.api.ICanAbility;
import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;

/**
 * Unified special skill execution goal for parasite entities.
 * <p>
 * This goal evaluates which abilities a parasite has available and decides
 * which one to execute based on the current combat situation, health level,
 * target distance, and phase. It acts as the central dispatcher for all
 * special abilities defined in {@link ICanAbility.AbilityType}.
 * <p>
 * Priority order for skill execution:
 * <ol>
 *   <li><b>SELF_DESTRUCT</b> — when health &lt; 25% and target is close</li>
 *   <li><b>TELEPORT</b> — when target is distant or when recently hurt</li>
 *   <li><b>SUMMON</b> — when health &lt; 50% or multiple enemies nearby</li>
 *   <li><b>AOE_SLAM</b> — when surrounded by multiple enemies</li>
 *   <li><b>BLOCK_BREAK</b> — when path is blocked toward target</li>
 *   <li><b>BLOCK_PLACE</b> — when idle and can build colony structures</li>
 *   <li><b>HEAL_AURA</b> — when injured and allies are nearby</li>
 *   <li><b>INFECT_AURA</b> — when non-parasite entities are in range</li>
 * </ol>
 * <p>
 * Inspired by the original SRP special skill system where each parasite type
 * had unique special moves (Enderman teleport, Bomph self-destruct, Beckon summon, etc.)
 */
public class ParasiteSpecialSkillGoal extends Goal {

    protected final EntityParasiteBase parasite;

    // Skill-specific cooldown trackers
    protected int teleportCooldown;
    protected int summonCooldown;
    protected int selfDestructCooldown;
    protected int aoeSlamCooldown;
    protected int blockBreakCooldown;
    protected int blockPlaceCooldown;
    protected int healAuraCooldown;
    protected int infectAuraCooldown;

    // Configurable cooldowns (in ticks) — set based on parasite tier
    protected final int teleportCooldownMax;
    protected final int summonCooldownMax;
    protected final int selfDestructWindup;
    protected final int aoeSlamCooldownMax;
    protected final int blockBreakCooldownMax;
    protected final int blockPlaceCooldownMax;
    protected final int healAuraInterval;
    protected final int infectAuraInterval;

    // Range parameters
    protected final float teleportMinRange;
    protected final float teleportMaxRange;
    protected final float selfDestructTriggerRange;
    protected float summonRange;
    protected float aoeSlamRange;
    protected float healAuraRange;
    protected float infectAuraRange;

    // Self-destruct state
    protected int selfDestructWindupTicks = 0;
    protected boolean isSelfDestructing = false;

    /**
     * Creates a ParasiteSpecialSkillGoal with tier-appropriate cooldowns.
     *
     * @param parasite                the parasite entity
     * @param tierLevel               tier level (0=lowest, used to scale cooldowns)
     * @param phase                   the evolution phase at creation (affects ranges and cooldowns)
     */
    public ParasiteSpecialSkillGoal(EntityParasiteBase parasite, int tierLevel, EvoPhase phase) {
        this.parasite = parasite;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));

        int phaseBonus = phase.getPhaseNumber();

        // Scale cooldowns inversely with tier — higher tier = shorter cooldowns
        this.teleportCooldownMax = Math.max(100, 600 - tierLevel * 40 - phaseBonus * 20);
        this.summonCooldownMax = Math.max(300, 1200 - tierLevel * 60 - phaseBonus * 40);
        this.selfDestructWindup = Math.max(20, 60 - phaseBonus * 5);
        this.aoeSlamCooldownMax = Math.max(80, 200 - tierLevel * 10 - phaseBonus * 10);
        this.blockBreakCooldownMax = Math.max(20, 200 - tierLevel * 10 - phaseBonus * 10);
        this.blockPlaceCooldownMax = Math.max(40, 120 - tierLevel * 5);
        this.healAuraInterval = Math.max(20, 60 - phaseBonus * 5);
        this.infectAuraInterval = Math.max(20, 60 - phaseBonus * 5);

        // Range parameters
        this.teleportMinRange = 5.0F + phaseBonus;
        this.teleportMaxRange = 16.0F + phaseBonus * 2.0F;
        this.selfDestructTriggerRange = 3.0F + phaseBonus * 0.5F;
        this.summonRange = 8.0F + phaseBonus;
        this.aoeSlamRange = 4.0F + phaseBonus * 0.5F;
        this.healAuraRange = 8.0F + phaseBonus;
        this.infectAuraRange = 6.0F + phaseBonus;
    }

    /**
     * Simplified constructor using the parasite's phase for defaults.
     */
    public ParasiteSpecialSkillGoal(EntityParasiteBase parasite) {
        this(parasite, parasite.getParasiteTier(), parasite.getPhaseCreated());
    }

    // ========== Cooldown Tick ==========

    @Override
    public boolean canUse() {
        if (this.parasite.level().isClientSide) return false;
        if (!this.parasite.canWorkTask()) return false;

        // Decrement all cooldowns
        if (this.teleportCooldown > 0) this.teleportCooldown--;
        if (this.summonCooldown > 0) this.summonCooldown--;
        if (this.selfDestructCooldown > 0) this.selfDestructCooldown--;
        if (this.aoeSlamCooldown > 0) this.aoeSlamCooldown--;
        if (this.blockBreakCooldown > 0) this.blockBreakCooldown--;
        if (this.blockPlaceCooldown > 0) this.blockPlaceCooldown--;
        if (this.healAuraCooldown > 0) this.healAuraCooldown--;
        if (this.infectAuraCooldown > 0) this.infectAuraCooldown--;

        // If already self-destructing, continue that
        if (this.isSelfDestructing) return true;

        // Priority 1: Self-destruct when health is critical and target close
        if (canSelfDestruct()) return true;

        // Priority 2: Teleport when target is distant or recently hurt
        if (canTeleport()) return true;

        // Priority 3: Summon when health low or multiple enemies nearby
        if (canSummon()) return true;

        // Priority 4: AOE slam when surrounded
        if (canAoeSlam()) return true;

        // Priority 5: Block break when path is blocked
        if (canBlockBreak()) return true;

        // Priority 6: Passive auras (always available, tick-based)
        if (canHealAura()) return true;
        if (canInfectAura()) return true;

        // Priority 7: Block place when idle
        if (canBlockPlace()) return true;

        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return this.isSelfDestructing;
    }

    @Override
    public void start() {
        if (this.isSelfDestructing) return; // already in progress

        // Execute highest-priority available ability
        if (canSelfDestruct()) {
            startSelfDestruct();
        } else if (canTeleport()) {
            executeTeleport();
        } else if (canSummon()) {
            executeSummon();
        } else if (canAoeSlam()) {
            executeAoeSlam();
        } else if (canBlockBreak()) {
            executeBlockBreak();
        } else if (canHealAura()) {
            executeHealAura();
        } else if (canInfectAura()) {
            executeInfectAura();
        } else if (canBlockPlace()) {
            executeBlockPlace();
        }
    }

    @Override
    public void tick() {
        if (this.isSelfDestructing) {
            tickSelfDestruct();
        }
    }

    @Override
    public void stop() {
        // Reset self-destruct if we were interrupted before completion
        if (this.isSelfDestructing && this.selfDestructWindupTicks < this.selfDestructWindup) {
            this.isSelfDestructing = false;
            this.selfDestructWindupTicks = 0;
            this.parasite.setSelfDestructState(0);
        }
    }

    // ========== Availability Checks ==========

    /**
     * Checks if the parasite can and should self-destruct.
     * Triggers when: has SELF_DESTRUCT ability AND (health &lt; 25% with target nearby,
     * or health &lt; 10% regardless of target proximity).
     */
    protected boolean canSelfDestruct() {
        if (!this.parasite.hasAbility(ICanAbility.AbilityType.SELF_DESTRUCT)) return false;
        if (this.selfDestructCooldown > 0) return false;
        if (this.isSelfDestructing) return true;

        float healthPercent = this.parasite.getHealth() / this.parasite.getMaxHealth();

        // Critical health — always trigger
        if (healthPercent < 0.10F) return true;

        // Low health with target close
        if (healthPercent < 0.25F) {
            LivingEntity target = this.parasite.getTarget();
            if (target != null && target.isAlive()) {
                double dist = this.parasite.distanceToSqr(target);
                return dist <= this.selfDestructTriggerRange * this.selfDestructTriggerRange * 4.0;
            }
        }

        return false;
    }

    /**
     * Checks if the parasite can teleport.
     * Triggers when: has TELEPORT ability, target is in teleport range, and cooldown is ready.
     * Also triggers when recently hurt (reactive teleport).
     */
    protected boolean canTeleport() {
        if (!this.parasite.hasAbility(ICanAbility.AbilityType.TELEPORT)) return false;
        if (this.teleportCooldown > 0) return false;

        LivingEntity target = this.parasite.getTarget();
        if (target == null || !target.isAlive()) return false;

        double dist = this.parasite.distanceToSqr(target);
        double minSq = this.teleportMinRange * this.teleportMinRange;
        double maxSq = this.teleportMaxRange * this.teleportMaxRange;

        // Teleport when target is distant (within range)
        if (dist >= minSq && dist <= maxSq) return true;

        // Reactive teleport when recently hurt and target exists
        if (this.parasite.wasRecentlyHurt() && dist <= maxSq) return true;

        return false;
    }

    /**
     * Checks if the parasite can summon reinforcements.
     * Triggers when: has SUMMON ability AND (health &lt; 50% or multiple enemies nearby).
     */
    protected boolean canSummon() {
        if (!this.parasite.hasAbility(ICanAbility.AbilityType.SUMMON)) return false;
        if (this.summonCooldown > 0) return false;

        LivingEntity target = this.parasite.getTarget();
        if (target == null || !target.isAlive()) return false;

        float healthPercent = this.parasite.getHealth() / this.parasite.getMaxHealth();

        // Low health trigger
        if (healthPercent < 0.50F) return true;

        // Multiple enemies nearby trigger
        AABB area = this.parasite.getBoundingBox().inflate(this.summonRange);
        List<LivingEntity> enemies = this.parasite.level().getEntitiesOfClass(
                LivingEntity.class, area,
                e -> e != this.parasite && e.isAlive() && !(e instanceof EntityParasiteBase));
        return enemies.size() >= 2;
    }

    /**
     * Checks if the parasite can perform an AOE slam.
     * Triggers when: has AOE_SLAM ability and multiple enemies in melee range.
     */
    protected boolean canAoeSlam() {
        if (!this.parasite.hasAbility(ICanAbility.AbilityType.AOE_SLAM)) return false;
        if (this.aoeSlamCooldown > 0) return false;
        if (!this.parasite.getGeneBooleans()[com.subspaceparasite.api.parasite.GeneType.SPECIAL_MOVE.getIndex()]) return false;

        LivingEntity target = this.parasite.getTarget();
        if (target == null || !target.isAlive()) return false;

        // Check if at least one enemy is in melee range
        double dist = this.parasite.distanceToSqr(target);
        if (dist > this.aoeSlamRange * this.aoeSlamRange) return false;

        // Multiple enemies nearby makes it worthwhile
        AABB area = this.parasite.getBoundingBox().inflate(this.aoeSlamRange);
        List<LivingEntity> nearby = this.parasite.level().getEntitiesOfClass(
                LivingEntity.class, area,
                e -> e != this.parasite && e.isAlive() && !(e instanceof EntityParasiteBase));
        return nearby.size() >= 1;
    }

    /**
     * Checks if the parasite can break blocks toward its target.
     * Triggers when: has BLOCK_BREAK ability and has a target but no clear path.
     */
    protected boolean canBlockBreak() {
        if (!this.parasite.hasAbility(ICanAbility.AbilityType.BLOCK_BREAK)) return false;
        if (this.blockBreakCooldown > 0) return false;

        LivingEntity target = this.parasite.getTarget();
        if (target == null || !target.isAlive()) return false;

        // Check if the path is blocked — look for solid blocks between parasite and target
        return isPathBlocked();
    }

    /**
     * Checks if the parasite can place colony blocks.
     * Triggers when: has BLOCK_PLACE ability and is idle (no target).
     */
    protected boolean canBlockPlace() {
        if (!this.parasite.hasAbility(ICanAbility.AbilityType.BLOCK_PLACE)) return false;
        if (this.blockPlaceCooldown > 0) return false;

        // Only place blocks when idle
        return this.parasite.getTarget() == null && this.parasite.onGround();
    }

    /**
     * Checks if the parasite can use its healing aura.
     * Triggers when: has HEAL_AURA ability and there are injured parasites nearby.
     */
    protected boolean canHealAura() {
        if (!this.parasite.hasAbility(ICanAbility.AbilityType.HEAL_AURA)) return false;
        if (this.healAuraCooldown > 0) return false;

        // Check for injured parasites nearby (including self)
        if (this.parasite.getHealth() < this.parasite.getMaxHealth()) return true;

        AABB area = this.parasite.getBoundingBox().inflate(this.healAuraRange);
        List<EntityParasiteBase> nearby = this.parasite.level().getEntitiesOfClass(
                EntityParasiteBase.class, area,
                p -> p != this.parasite && p.isAlive() && p.getHealth() < p.getMaxHealth());
        return !nearby.isEmpty();
    }

    /**
     * Checks if the parasite can use its infection aura.
     * Triggers when: has INFECT_AURA ability and non-parasite entities are in range.
     */
    protected boolean canInfectAura() {
        if (!this.parasite.hasAbility(ICanAbility.AbilityType.INFECT_AURA)) return false;
        if (this.infectAuraCooldown > 0) return false;

        AABB area = this.parasite.getBoundingBox().inflate(this.infectAuraRange);
        List<LivingEntity> nearby = this.parasite.level().getEntitiesOfClass(
                LivingEntity.class, area,
                e -> e != this.parasite && e.isAlive() && !(e instanceof EntityParasiteBase));
        return !nearby.isEmpty();
    }

    // ========== Execution Methods ==========

    /** Starts the self-destruct windup sequence. */
    protected void startSelfDestruct() {
        this.isSelfDestructing = true;
        this.selfDestructWindupTicks = 0;
        this.parasite.setSelfDestructState(this.selfDestructWindup);
        this.parasite.getNavigation().stop();
    }

    /** Ticks the self-destruct windup. When complete, calls trySelfDestruct on the parasite. */
    protected void tickSelfDestruct() {
        this.selfDestructWindupTicks++;
        LivingEntity target = this.parasite.getTarget();
        if (target != null) {
            this.parasite.getLookControl().setLookAt(target, 360.0F, 360.0F);
        }

        // Move toward target during windup
        if (target != null) {
            this.parasite.getMoveControl().setWantedPosition(
                    target.getX(), target.getY(), target.getZ(), 1.0);
        }

        if (this.selfDestructWindupTicks >= this.selfDestructWindup) {
            // Execute self-destruct
            this.parasite.trySelfDestruct();
            this.isSelfDestructing = false;
            this.selfDestructWindupTicks = 0;
            this.selfDestructCooldown = this.selfDestructWindup * 3; // Long post-destruct cooldown
        }
    }

    /** Executes a teleport behind the current target. */
    protected void executeTeleport() {
        LivingEntity target = this.parasite.getTarget();
        if (target != null) {
            this.parasite.tryTeleport(target);
        }
        this.teleportCooldown = this.teleportCooldownMax;
    }

    /** Executes a summon of reinforcement parasites. */
    protected void executeSummon() {
        this.parasite.trySummon();
        this.summonCooldown = this.summonCooldownMax;
    }

    /** Executes an AOE slam attack. */
    protected void executeAoeSlam() {
        this.parasite.executeSpecialMove(ICanAbility.AbilityType.AOE_SLAM);
        this.aoeSlamCooldown = this.aoeSlamCooldownMax;
    }

    /** Executes a block break toward the target. */
    protected void executeBlockBreak() {
        LivingEntity target = this.parasite.getTarget();
        if (target != null) {
            // Find the blocking block
            BlockPos blockingPos = findBlockingBlock(target);
            if (blockingPos != null) {
                this.parasite.tryBreakBlock(blockingPos);
            } else {
                // Fallback: break block in front
                BlockPos frontPos = this.parasite.blockPosition().relative(this.parasite.getDirection());
                this.parasite.tryBreakBlock(frontPos);
            }
        }
        this.blockBreakCooldown = this.blockBreakCooldownMax;
    }

    /** Executes a block placement for colony building. */
    protected void executeBlockPlace() {
        // Find a suitable position near the parasite
        BlockPos targetPos = findPlacePosition();
        if (targetPos != null) {
            this.parasite.tryPlaceBlock(targetPos, null);
        }
        this.blockPlaceCooldown = this.blockPlaceCooldownMax;
    }

    /** Executes a healing aura pulse. */
    protected void executeHealAura() {
        this.parasite.executeSpecialMove(ICanAbility.AbilityType.HEAL_AURA);
        this.healAuraCooldown = this.healAuraInterval;
    }

    /** Executes an infection aura pulse. */
    protected void executeInfectAura() {
        this.parasite.executeSpecialMove(ICanAbility.AbilityType.INFECT_AURA);
        this.infectAuraCooldown = this.infectAuraInterval;
    }

    // ========== Utility Methods ==========

    /**
     * Checks if the path toward the current target is blocked by breakable blocks.
     * Samples blocks along the line from parasite to target.
     */
    protected boolean isPathBlocked() {
        LivingEntity target = this.parasite.getTarget();
        if (target == null) return false;

        // Sample a few blocks along the line between parasite and target
        double dx = target.getX() - this.parasite.getX();
        double dy = target.getY() - this.parasite.getY();
        double dz = target.getZ() - this.parasite.getZ();
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dist < 2.0) return false;

        double stepX = dx / dist;
        double stepY = dy / dist;
        double stepZ = dz / dist;

        for (int i = 1; i <= 3; i++) {
            BlockPos checkPos = BlockPos.containing(
                    this.parasite.getX() + stepX * i,
                    this.parasite.getY() + stepY * i + 1.0,
                    this.parasite.getZ() + stepZ * i);
            if (!this.parasite.level().getBlockState(checkPos).isAir()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds the first blocking block between the parasite and its target.
     */
    @Nullable
    protected BlockPos findBlockingBlock(LivingEntity target) {
        double dx = target.getX() - this.parasite.getX();
        double dy = target.getY() - this.parasite.getY();
        double dz = target.getZ() - this.parasite.getZ();
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dist < 1.0) return null;

        double stepX = dx / dist;
        double stepY = dy / dist;
        double stepZ = dz / dist;

        for (int i = 1; i <= 4; i++) {
            BlockPos checkPos = BlockPos.containing(
                    this.parasite.getX() + stepX * i,
                    this.parasite.getY() + stepY * i + 1.0,
                    this.parasite.getZ() + stepZ * i);
            if (!this.parasite.level().getBlockState(checkPos).isAir()) {
                return checkPos;
            }
        }
        return null;
    }

    /**
     * Finds a suitable position to place a parasite block near the entity.
     */
    @Nullable
    protected BlockPos findPlacePosition() {
        BlockPos origin = this.parasite.blockPosition();
        for (int attempts = 0; attempts < 8; attempts++) {
            int x = origin.getX() + this.parasite.getRandom().nextInt(5) - 2;
            int y = origin.getY() + this.parasite.getRandom().nextInt(3) - 1;
            int z = origin.getZ() + this.parasite.getRandom().nextInt(5) - 2;

            BlockPos candidate = new BlockPos(x, y, z);
            if (this.parasite.level().getBlockState(candidate).isAir()
                    && !this.parasite.level().getBlockState(candidate.below()).isAir()) {
                return candidate;
            }
        }
        return null;
    }

    // ========== Cooldown Accessors ==========

    public int getTeleportCooldown() { return teleportCooldown; }
    public void setTeleportCooldown(int cooldown) { this.teleportCooldown = cooldown; }

    public int getSummonCooldown() { return summonCooldown; }
    public void setSummonCooldown(int cooldown) { this.summonCooldown = cooldown; }

    public int getSelfDestructCooldown() { return selfDestructCooldown; }
    public void setSelfDestructCooldown(int cooldown) { this.selfDestructCooldown = cooldown; }

    public int getAoeSlamCooldown() { return aoeSlamCooldown; }
    public void setAoeSlamCooldown(int cooldown) { this.aoeSlamCooldown = cooldown; }

    public int getBlockBreakCooldown() { return blockBreakCooldown; }
    public void setBlockBreakCooldown(int cooldown) { this.blockBreakCooldown = cooldown; }

    public int getBlockPlaceCooldown() { return blockPlaceCooldown; }
    public void setBlockPlaceCooldown(int cooldown) { this.blockPlaceCooldown = cooldown; }
}
