package com.subspaceparasite.common.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;

import java.util.EnumSet;

/**
 * Kirin teleport attack. Charge 60 ticks, teleport near target, steal 50% health.
 */
public class ParasiteKirinBlinkGoal extends Goal {

    protected final EntityParasiteBase parasite;

    protected int chargeTicks;
    protected boolean isCharging;
    protected boolean hasBlinked;

    private static final int CHARGE_DURATION = 60;
    private static final float HEALTH_STEAL_PERCENT = 0.5F;
    private static final double BLINK_DISTANCE = 3.0D;

    public ParasiteKirinBlinkGoal(EntityParasiteBase parasite) {
        this.parasite = parasite;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.parasite.getTarget();
        if (target == null || !target.isAlive()) return false;
        if (this.parasite.isPassive()) return false;

        double dist = this.parasite.distanceToSqr(target);
        return dist > BLINK_DISTANCE * BLINK_DISTANCE && dist < 256.0D; // within 16 blocks
    }

    @Override
    public boolean canContinueToUse() {
        return this.isCharging || this.hasBlinked;
    }

    @Override
    public void start() {
        this.chargeTicks = 0;
        this.isCharging = true;
        this.hasBlinked = false;
    }

    @Override
    public void stop() {
        this.isCharging = false;
        this.hasBlinked = false;
        this.chargeTicks = 0;
    }

    @Override
    public void tick() {
        LivingEntity target = this.parasite.getTarget();
        if (target == null) {
            this.stop();
            return;
        }

        if (this.isCharging) {
            this.chargeTicks++;

            // Look at target during charge
            this.parasite.getLookControl().setLookAt(target, 30.0F, 30.0F);

            // Visual/sound cues during charging
            if (this.chargeTicks % 10 == 0) {
                this.parasite.onSkillCharging((byte) 99, this.chargeTicks, CHARGE_DURATION);
            }

            if (this.chargeTicks >= CHARGE_DURATION) {
                // Execute blink
                performBlink(target);
                this.isCharging = false;
                this.hasBlinked = true;
            }
        } else if (this.hasBlinked) {
            // Post-blink: attack and steal health
            stealHealth(target);
            this.parasite.doHurtTarget(target);
            this.hasBlinked = false;
        }
    }

    protected void performBlink(LivingEntity target) {
        // Teleport near the target (not inside them)
        Vec3 targetPos = target.position();
        Vec3 lookVec = target.getLookAngle();

        // Teleport behind the target
        Vec3 behindPos = targetPos.subtract(lookVec.scale(BLINK_DISTANCE));

        // Ensure safe landing position
        if (this.parasite.level().getBlockState(net.minecraft.core.BlockPos.containing(behindPos)).isAir()) {
            this.parasite.teleportTo(behindPos.x, behindPos.y, behindPos.z);
        } else {
            // Fallback: teleport to the side
            Vec3 sidePos = targetPos.add(lookVec.z * BLINK_DISTANCE, 0, -lookVec.x * BLINK_DISTANCE);
            this.parasite.teleportTo(sidePos.x, sidePos.y, sidePos.z);
        }

        // Play blink effect
        this.parasite.onSkillExecute((byte) 99);
    }

    protected void stealHealth(LivingEntity target) {
        float targetHealth = target.getHealth();
        float stolenHealth = targetHealth * HEALTH_STEAL_PERCENT;

        // Damage target using the damage system
        target.hurt(this.parasite.damageSources().generic(), stolenHealth);

        // Heal self
        float currentHealth = this.parasite.getHealth();
        float maxHealth = this.parasite.getMaxHealth();
        this.parasite.setHealth(Math.min(currentHealth + stolenHealth, maxHealth));
    }
}
