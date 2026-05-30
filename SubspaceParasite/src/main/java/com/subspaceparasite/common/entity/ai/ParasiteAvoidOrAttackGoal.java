package com.subspaceparasite.common.entity.ai;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.LightLayer;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;

import java.util.EnumSet;
import java.util.List;

/**
 * Grouping/following behavior. When in light above threshold, find nearby parasites
 * and follow the highest-tier one. In darkness, set work task true (aggressive).
 * RAGE potion = always aggressive.
 */
public class ParasiteAvoidOrAttackGoal extends Goal {

    protected final EntityParasiteBase parasite;
    protected final float lightThreshold;
    protected final int xzRange;
    protected final int yRange;
    protected final TargetingConditions targetingConditions;

    protected EntityParasiteBase leaderCandidate;
    protected int recheckDelay;

    public ParasiteAvoidOrAttackGoal(EntityParasiteBase parasite, float lightThreshold, int xzRange, int yRange) {
        this.parasite = parasite;
        this.lightThreshold = lightThreshold;
        this.xzRange = xzRange;
        this.yRange = yRange;
        this.targetingConditions = TargetingConditions.forNonCombat().range(Math.max(xzRange, yRange)).ignoreLineOfSight();
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.parasite.isVehicle() || this.parasite.getTarget() != null) {
            return false;
        }

        // RAGE potion = always aggressive
        if (this.parasite.hasEffect(MobEffects.DAMAGE_BOOST)) {
            this.parasite.setWorking(true);
            return false;
        }

        int blockLight = this.parasite.level().getBrightness(LightLayer.BLOCK, this.parasite.blockPosition());
        int skyLight = this.parasite.level().getBrightness(LightLayer.SKY, this.parasite.blockPosition());
        float brightness = Math.max(blockLight, skyLight) / 15.0f;

        if (brightness >= this.lightThreshold) {
            // In bright light: find and follow the highest-tier parasite nearby
            List<EntityParasiteBase> nearbyParasites = this.parasite.level().getNearbyEntities(
                    EntityParasiteBase.class,
                    this.targetingConditions,
                    this.parasite,
                    this.parasite.getBoundingBox().inflate(this.xzRange, this.yRange, this.xzRange)
            );

            if (!nearbyParasites.isEmpty()) {
                this.leaderCandidate = null;
                int highestTier = -1;
                for (EntityParasiteBase other : nearbyParasites) {
                    if (other == this.parasite || other.isDeadOrDying()) continue;
                    int tier = other.getParasiteTier();
                    if (tier > highestTier) {
                        highestTier = tier;
                        this.leaderCandidate = other;
                    }
                }
                return this.leaderCandidate != null;
            }
        } else {
            // In darkness: set aggressive
            this.parasite.setWorking(true);
        }

        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.parasite.getTarget() != null) return false;
        if (this.leaderCandidate == null || this.leaderCandidate.isDeadOrDying()) return false;
        if (this.parasite.hasEffect(MobEffects.DAMAGE_BOOST)) return false;

        double dist = this.parasite.distanceToSqr(this.leaderCandidate);
        return dist > 4.0D; // stop following when close enough
    }

    @Override
    public void start() {
        this.recheckDelay = 0;
    }

    @Override
    public void stop() {
        this.leaderCandidate = null;
        this.parasite.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.leaderCandidate == null) return;

        this.recheckDelay--;
        if (this.recheckDelay <= 0) {
            this.recheckDelay = 10 + this.parasite.getRandom().nextInt(5);
            this.parasite.getLookControl().setLookAt(this.leaderCandidate, 30.0F, 30.0F);
            this.parasite.getNavigation().moveTo(this.leaderCandidate, 1.0D);
        }
    }
}
