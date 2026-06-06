package com.subspaceparasite.common.entity.ai;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import com.subspaceparasite.common.entity.ai.misc.EntityCanShoot;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;

import java.util.EnumSet;

/**
 * Custom ranged attack (NOT extending RangedAttackGoal).
 * Implements own strafing/timing. Fires EntityCanShoot projectiles.
 */
public class ParasiteRangedAttackGoal extends Goal {

    protected final EntityParasiteBase parasite;
    protected final int cooldown;
    protected final int tickInterval;
    protected final int burstCount;
    protected final boolean homing;

    protected int attackTime;
    protected int burstFired;
    protected int strafeTime;
    protected boolean strafingBackward;
    protected double targetDistance;

    private static final double STRAFE_MIN = 8.0D;
    private static final double STRAFE_MAX = 24.0D;

    public ParasiteRangedAttackGoal(EntityParasiteBase parasite, int cooldown, int tickInterval, int burstCount, boolean homing) {
        this.parasite = parasite;
        this.cooldown = cooldown;
        this.tickInterval = tickInterval;
        this.burstCount = burstCount;
        this.homing = homing;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!(this.parasite instanceof EntityCanShoot)) return false;
        LivingEntity target = this.parasite.getTarget();
        if (target == null || !target.isAlive()) return false;
        if (this.parasite.isPassive()) return false;

        this.targetDistance = this.parasite.distanceToSqr(target);
        return this.targetDistance > STRAFE_MIN * STRAFE_MIN;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse() || this.parasite.getTarget() != null && this.attackTime > -this.cooldown;
    }

    @Override
    public void start() {
        this.attackTime = -this.cooldown;
        this.burstFired = 0;
        this.strafeTime = 0;
        this.strafingBackward = false;
    }

    @Override
    public void stop() {
        this.parasite.getNavigation().stop();
        this.burstFired = 0;
    }

    @Override
    public void tick() {
        LivingEntity target = this.parasite.getTarget();
        if (target == null) return;

        double dist = this.parasite.distanceToSqr(target);
        boolean canSee = this.parasite.getSensing().hasLineOfSight(target);

        // Look at target
        this.parasite.getLookControl().setLookAt(target, 30.0F, 30.0F);

        // Strafing logic
        if (dist < STRAFE_MIN * STRAFE_MIN) {
            // Too close - retreat away from the target
            Vec3 awayDir = this.parasite.position().subtract(target.position()).normalize().scale(4.0D);
            Vec3 retreatTarget = this.parasite.position().add(awayDir);
            this.parasite.getNavigation().moveTo(retreatTarget.x, retreatTarget.y, retreatTarget.z, 1.0D);
        } else if (dist > STRAFE_MAX * STRAFE_MAX) {
            // Too far - move closer
            this.parasite.getNavigation().moveTo(target, 1.0D);
        } else if (canSee) {
            // In range and can see - strafe
            this.strafeTime++;
            if (this.strafeTime > 20 + this.parasite.getRandom().nextInt(30)) {
                this.strafingBackward = !this.strafingBackward;
                this.strafeTime = 0;
            }

            Vec3 strafeDir = this.parasite.getLookAngle().yRot(this.strafingBackward ? 1.5708F : -1.5708F);
            Vec3 moveTarget = this.parasite.position().add(strafeDir.scale(4.0D));
            this.parasite.getNavigation().moveTo(moveTarget.x, moveTarget.y, moveTarget.z, 0.6D);
        }

        // Firing logic
        this.attackTime++;
        if (this.attackTime >= 0 && canSee && this.attackTime % this.tickInterval == 0) {
            if (this.burstFired < this.burstCount) {
                this.fireProjectile(target);
                this.burstFired++;
            } else {
                this.attackTime = -this.cooldown;
                this.burstFired = 0;
            }
        }
    }

    protected void fireProjectile(LivingEntity target) {
        if (this.parasite instanceof EntityCanShoot shooter) {
            Vec3 start = this.parasite.position().add(0.0D, this.parasite.getEyeHeight(), 0.0D);
            Vec3 end = target.position().add(0.0D, target.getEyeHeight() * 0.5D, 0.0D);
            Vec3 delta = end.subtract(start);

            if (this.homing) {
                // Homing projectiles aim directly at the target's current position
                delta = end.subtract(start).normalize().scale(1.5D);
            }

            net.minecraft.world.entity.Entity proj = shooter.getProj(delta.x, delta.y, delta.z);
            if (proj != null) {
                this.parasite.level().addFreshEntity(proj);
                shooter.playProjSound();
            }
        }
    }
}
