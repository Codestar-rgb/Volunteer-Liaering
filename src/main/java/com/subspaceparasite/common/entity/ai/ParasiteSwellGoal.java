package com.subspaceparasite.common.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;

import java.util.EnumSet;

/**
 * Creeper-style swell/explode. Sets selfDestructState.
 */
public class ParasiteSwellGoal extends Goal {

    protected final EntityParasiteBase parasite;
    protected final double triggerDistance;

    private static final double DEFAULT_TRIGGER = 3.0D;

    public ParasiteSwellGoal(EntityParasiteBase parasite) {
        this(parasite, DEFAULT_TRIGGER);
    }

    public ParasiteSwellGoal(EntityParasiteBase parasite, double triggerDistance) {
        this.parasite = parasite;
        this.triggerDistance = triggerDistance;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (this.parasite.isSelfDestructing()) return true;

        LivingEntity target = this.parasite.getTarget();
        if (target == null) return false;
        if (this.parasite.isPassive()) return false;

        double dist = this.parasite.distanceToSqr(target);
        return dist <= this.triggerDistance * this.triggerDistance;
    }

    @Override
    public void start() {
        this.parasite.getNavigation().stop();
        this.parasite.setSelfDestructState(1); // swelling
    }

    @Override
    public void stop() {
        LivingEntity target = this.parasite.getTarget();
        if (target == null || this.parasite.distanceToSqr(target) > this.triggerDistance * this.triggerDistance * 2.0D) {
            this.parasite.setSelfDestructState(0); // defusing
        }
    }

    @Override
    public void tick() {
        LivingEntity target = this.parasite.getTarget();

        if (target != null && this.parasite.distanceToSqr(target) <= this.triggerDistance * this.triggerDistance) {
            // Continue swelling
            this.parasite.setSelfDestructState(1);
        } else {
            // Out of range - defuse
            this.parasite.setSelfDestructState(0);
        }
    }
}
