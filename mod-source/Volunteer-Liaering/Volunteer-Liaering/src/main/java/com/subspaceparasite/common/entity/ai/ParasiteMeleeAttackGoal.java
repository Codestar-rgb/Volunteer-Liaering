package com.subspaceparasite.common.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;

/**
 * Standard melee attack extending MeleeAttackGoal with status integration.
 * Applies parasite-specific effects on hit and respects parasite state.
 */
public class ParasiteMeleeAttackGoal extends MeleeAttackGoal {

    protected final EntityParasiteBase parasite;

    public ParasiteMeleeAttackGoal(EntityParasiteBase parasite, double speed, boolean followingEvenIfNotSeen) {
        super(parasite, speed, followingEvenIfNotSeen);
        this.parasite = parasite;
    }

    @Override
    public boolean canUse() {
        if (this.parasite.isPassive()) return false;
        return super.canUse();
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity target, double distanceToTarget) {
        double attackReach = this.getAttackReachSqr(target);
        if (distanceToTarget <= attackReach && this.getTicksUntilNextAttack() <= 0) {
            this.resetAttackCooldown();
            this.parasite.doHurtTarget(target);

            // Status integration: apply parasite-specific on-hit effects
            this.parasite.onMeleeAttack(target);
        }
    }

    @Override
    protected double getAttackReachSqr(LivingEntity target) {
        // Parasites may have extended reach
        float reachBonus = this.parasite.getAttackReachBonus();
        double baseReach = super.getAttackReachSqr(target);
        return baseReach + (reachBonus * reachBonus);
    }
}
