package com.subspaceparasite.common.entity.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import com.subspaceparasite.common.entity.ai.misc.EntityCanSwim;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;

import java.util.EnumSet;

/**
 * Swimming with diving capability. Apply downward motion when target is in water below.
 */
public class ParasiteSwimDiveGoal extends Goal {

    protected final Mob mob;
    protected final double yMotion;

    public ParasiteSwimDiveGoal(Mob mob, double yMotion) {
        this.mob = mob;
        this.yMotion = yMotion;
        this.setFlags(EnumSet.of(Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        return this.mob.isInWater();
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.isInWater();
    }

    @Override
    public void tick() {
        if (this.mob.isInWater()) {
            // Check if target is below in water
            if (this.mob.getTarget() != null && this.mob.getTarget().isInWater()) {
                double targetY = this.mob.getTarget().getY();
                if (targetY < this.mob.getY()) {
                    // Dive toward target
                    this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0.0D, this.yMotion, 0.0D));
                    return;
                }
            }

            // Standard swimming - float upward
            if (this.mob.getDeltaMovement().y < 0.0D) {
                this.mob.setDeltaMovement(this.mob.getDeltaMovement().x, Math.max(this.mob.getDeltaMovement().y, 0.0D), this.mob.getDeltaMovement().z);
            }

            // Surface for air if not a swim-capable parasite
            if (!(this.mob instanceof EntityCanSwim)) {
                if (this.mob.getAirSupply() < 40) {
                    this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0.0D, 0.04D, 0.0D));
                }
            }
        }
    }
}
