package com.subspaceparasite.common.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;

import net.minecraft.world.phys.Vec3;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;

import java.util.EnumSet;

/**
 * Multi-phase dive bomb: IDLE -> ASCEND -> HOVER -> DIVE -> EXPLODE.
 */
public class ParasiteDiveBombGoal extends Goal {

    protected final Mob mob;
    protected final int cooldown;
    protected final int hoverTicks;
    protected final double diveSpeed;
    protected final float explosionPower;

    protected int bombCooldown;
    protected Phase currentPhase;
    protected int phaseTicks;
    protected Vec3 diveTarget;

    public enum Phase {
        IDLE,
        ASCEND,
        HOVER,
        DIVE,
        EXPLODE
    }

    private static final double ASCEND_HEIGHT = 15.0D;
    private static final double ASCEND_SPEED = 0.4D;

    public ParasiteDiveBombGoal(Mob mob, int cooldown, int hoverTicks, double diveSpeed, float explosionPower) {
        this.mob = mob;
        this.cooldown = cooldown;
        this.hoverTicks = hoverTicks;
        this.diveSpeed = diveSpeed;
        this.explosionPower = explosionPower;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.bombCooldown > 0) {
            this.bombCooldown--;
            return false;
        }

        LivingEntity target = this.mob.getTarget();
        if (target == null || !target.isAlive()) return false;

        return this.mob.onGround() || this.currentPhase != null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.currentPhase != null && this.currentPhase != Phase.EXPLODE;
    }

    @Override
    public void start() {
        this.currentPhase = Phase.ASCEND;
        this.phaseTicks = 0;
        this.diveTarget = null;
    }

    @Override
    public void stop() {
        this.currentPhase = null;
        this.phaseTicks = 0;
        this.diveTarget = null;
        this.bombCooldown = this.cooldown;
    }

    @Override
    public void tick() {
        if (this.currentPhase == null) return;

        this.phaseTicks++;

        switch (this.currentPhase) {
            case ASCEND:
                tickAscend();
                break;
            case HOVER:
                tickHover();
                break;
            case DIVE:
                tickDive();
                break;
            case EXPLODE:
                tickExplode();
                break;
            default:
                break;
        }
    }

    protected void tickAscend() {
        LivingEntity target = this.mob.getTarget();
        if (target != null) {
            // Move upward and slightly toward target
            Vec3 toward = target.position().subtract(this.mob.position()).normalize().scale(0.05D);
            this.mob.setDeltaMovement(toward.x, ASCEND_SPEED, toward.z);
        } else {
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0.0D, ASCEND_SPEED, 0.0D));
        }

        // Check if we've ascended enough
        double startHeight = this.mob.blockPosition().getY() - ASCEND_HEIGHT;
        if (this.phaseTicks > 20 && this.mob.getDeltaMovement().y <= 0) {
            this.currentPhase = Phase.HOVER;
            this.phaseTicks = 0;
        }
    }

    protected void tickHover() {
        // Hover in place, tracking target
        LivingEntity target = this.mob.getTarget();
        if (target != null) {
            this.diveTarget = target.position();
            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }

        // Slight hovering motion
        this.mob.setDeltaMovement(0.0D, Math.sin(this.phaseTicks * 0.2D) * 0.02D, 0.0D);

        if (this.phaseTicks >= this.hoverTicks) {
            this.currentPhase = Phase.DIVE;
            this.phaseTicks = 0;
        }
    }

    protected void tickDive() {
        if (this.diveTarget != null) {
            Vec3 toTarget = this.diveTarget.subtract(this.mob.position()).normalize().scale(this.diveSpeed);
            this.mob.setDeltaMovement(toTarget);
        }

        // Check if reached ground or target
        if (this.mob.onGround() || this.phaseTicks > 100) {
            this.currentPhase = Phase.EXPLODE;
            this.phaseTicks = 0;
        }
    }

    protected void tickExplode() {
        // Explode!
        if (!this.mob.level().isClientSide) {
            this.mob.level().explode(this.mob, this.mob.getX(), this.mob.getY(), this.mob.getZ(), this.explosionPower, Level.ExplosionInteraction.MOB);
            this.mob.discard();
        }

        this.currentPhase = null;
    }
}
