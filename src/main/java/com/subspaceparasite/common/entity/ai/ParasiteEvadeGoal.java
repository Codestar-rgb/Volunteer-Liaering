package com.subspaceparasite.common.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;

import java.util.EnumSet;

/**
 * Dodge/evasion with sidestep jump. Gene 5 required unless ignoreGene.
 */
public class ParasiteEvadeGoal extends Goal {

    protected final EntityParasiteBase parasite;
    protected final int cooldown;
    protected final int duration;
    protected final double distance;
    protected final boolean ignoreGene;

    protected int evadeCooldown;
    protected int evadeTicks;
    protected Vec3 evadeDirection;

    public ParasiteEvadeGoal(EntityParasiteBase parasite, int cooldown, int duration, double distance) {
        this(parasite, cooldown, duration, distance, false);
    }

    public ParasiteEvadeGoal(EntityParasiteBase parasite, int cooldown, int duration, double distance, boolean ignoreGene) {
        this.parasite = parasite;
        this.cooldown = cooldown;
        this.duration = duration;
        this.distance = distance;
        this.ignoreGene = ignoreGene;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        // Gene 5 required unless ignoreGene
        if (!this.ignoreGene && this.parasite.getGene() < 5) return false;

        if (this.evadeCooldown > 0) {
            this.evadeCooldown--;
            return false;
        }

        LivingEntity target = this.parasite.getTarget();
        if (target == null) return false;

        // Check if target is attacking or close
        double dist = this.parasite.distanceToSqr(target);
        if (dist > this.distance * this.distance * 4.0D) return false;

        // Random chance to evade
        return this.parasite.getRandom().nextFloat() < 0.3F;
    }

    @Override
    public boolean canContinueToUse() {
        return this.evadeTicks > 0 && this.evadeDirection != null;
    }

    @Override
    public void start() {
        LivingEntity target = this.parasite.getTarget();
        if (target == null) return;

        // Calculate sidestep direction (perpendicular to target direction)
        Vec3 toTarget = target.position().subtract(this.parasite.position()).normalize();
        boolean left = this.parasite.getRandom().nextBoolean();
        this.evadeDirection = left
                ? new Vec3(-toTarget.z, 0.0D, toTarget.x)
                : new Vec3(toTarget.z, 0.0D, -toTarget.x);

        this.evadeTicks = this.duration;
        this.evadeCooldown = this.cooldown;

        // Apply jump
        this.parasite.setDeltaMovement(this.parasite.getDeltaMovement().add(
                this.evadeDirection.x * 0.5D,
                0.4D,
                this.evadeDirection.z * 0.5D
        ));
    }

    @Override
    public void stop() {
        this.evadeTicks = 0;
        this.evadeDirection = null;
    }

    @Override
    public void tick() {
        if (this.evadeDirection != null && this.evadeTicks > 0) {
            this.evadeTicks--;
            // Continue sidestep motion
            this.parasite.setDeltaMovement(this.parasite.getDeltaMovement().add(
                    this.evadeDirection.x * 0.15D,
                    0.0D,
                    this.evadeDirection.z * 0.15D
            ));
        }
    }
}
