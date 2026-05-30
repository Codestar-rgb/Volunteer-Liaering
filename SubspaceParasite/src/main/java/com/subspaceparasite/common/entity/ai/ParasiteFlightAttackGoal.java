package com.subspaceparasite.common.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import net.minecraft.world.phys.Vec3;

import com.subspaceparasite.common.entity.ai.misc.EntityCanFly;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;

import java.util.EnumSet;

/**
 * Flight-based target acquisition. Ignores LOS if xray enabled.
 */
public class ParasiteFlightAttackGoal extends Goal {

    protected final EntityParasiteBase parasite;
    protected final double distance;
    protected final boolean ignoreSee;
    protected final int floorSearch;

    protected int flightTime;
    protected int attackCooldown;

    public ParasiteFlightAttackGoal(EntityParasiteBase parasite, double distance, boolean ignoreSee, int floorSearch) {
        this.parasite = parasite;
        this.distance = distance;
        this.ignoreSee = ignoreSee;
        this.floorSearch = floorSearch;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!(this.parasite instanceof EntityCanFly)) return false;

        LivingEntity target = this.parasite.getTarget();
        if (target == null || !target.isAlive()) return false;
        if (this.parasite.isPassive()) return false;

        double dist = this.parasite.distanceToSqr(target);
        if (dist > this.distance * this.distance) return false;

        // If ignoreSee (xray), skip LOS check
        if (!this.ignoreSee && !this.parasite.getSensing().hasLineOfSight(target)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.parasite.getTarget();
        if (target == null || !target.isAlive()) return false;

        double dist = this.parasite.distanceToSqr(target);
        return dist <= this.distance * this.distance * 1.5D;
    }

    @Override
    public void start() {
        this.flightTime = 0;
    }

    @Override
    public void stop() {
        this.parasite.getNavigation().stop();
    }

    @Override
    public void tick() {
        LivingEntity target = this.parasite.getTarget();
        if (target == null) return;

        this.flightTime++;

        // Fly toward target
        Vec3 targetPos = target.position().add(0.0D, target.getEyeHeight(), 0.0D);
        Vec3 currentPos = this.parasite.position().add(0.0D, this.parasite.getEyeHeight(), 0.0D);
        Vec3 toTarget = targetPos.subtract(currentPos);

        double dist = toTarget.length();
        if (dist > 1.0D) {
            Vec3 flyMotion = toTarget.normalize().scale(0.3D);
            this.parasite.setDeltaMovement(
                    this.parasite.getDeltaMovement().add(flyMotion.x * 0.1D, flyMotion.y * 0.1D, flyMotion.z * 0.1D)
            );
        }

        // Look at target
        this.parasite.getLookControl().setLookAt(target, 30.0F, 30.0F);

        // If close enough, try melee (with cooldown to prevent attack spam)
        if (dist < 2.0D && this.attackCooldown <= 0) {
            this.parasite.doHurtTarget(target);
            this.parasite.onMeleeAttack(target);
            this.attackCooldown = 20;
        }

        if (this.attackCooldown > 0) this.attackCooldown--;

        // Periodically check for ground to land on
        if (this.flightTime % 40 == 0) {
            BlockPos groundPos = findGroundBelow(this.parasite.blockPosition());
            if (groundPos != null && this.parasite.distanceToSqr(Vec3.atCenterOf(groundPos)) < 4.0D) {
                // Close to ground, might want to land
            }
        }
    }

    protected BlockPos findGroundBelow(BlockPos start) {
        for (int i = 0; i < this.floorSearch; i++) {
            BlockPos below = start.below(i);
            if (!this.parasite.level().getBlockState(below).isAir() &&
                this.parasite.level().getBlockState(below.above()).isAir()) {
                return below;
            }
        }
        return null;
    }
}
