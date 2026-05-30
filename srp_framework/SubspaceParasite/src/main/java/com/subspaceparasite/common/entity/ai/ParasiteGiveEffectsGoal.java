package com.subspaceparasite.common.entity.ai;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;

import java.util.EnumSet;
import java.util.List;

/**
 * Periodically buff nearby parasites with configurable effects.
 */
public class ParasiteGiveEffectsGoal extends Goal {

    protected final EntityParasiteBase parasite;
    protected final int cooldown;
    protected final int range;
    protected final String[] effects;

    protected int effectCooldown;
    protected static final TargetingConditions TARGETING = TargetingConditions.forNonCombat();

    public ParasiteGiveEffectsGoal(EntityParasiteBase parasite, int cooldown, int range, String[] effects) {
        this.parasite = parasite;
        this.cooldown = cooldown;
        this.range = range;
        this.effects = effects;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.effectCooldown > 0) {
            this.effectCooldown--;
            return false;
        }
        return !this.parasite.isDeadOrDying();
    }

    @Override
    public boolean canContinueToUse() {
        return false; // one-shot per cooldown
    }

    @Override
    public void start() {
        List<EntityParasiteBase> nearby = this.parasite.level().getNearbyEntities(
                EntityParasiteBase.class,
                TARGETING.range(this.range),
                this.parasite,
                this.parasite.getBoundingBox().inflate(this.range)
        );

        for (EntityParasiteBase other : nearby) {
            if (other.isDeadOrDying()) continue;
            for (String effectName : this.effects) {
                MobEffectInstance effect = parseEffect(effectName);
                if (effect != null) {
                    other.addEffect(new MobEffectInstance(effect));
                }
            }
        }

        this.effectCooldown = this.cooldown;
    }

    @Override
    public void stop() {
        this.effectCooldown = this.cooldown;
    }

    /**
     * Parse an effect from string name. Supports standard vanilla effects.
     */
    protected MobEffectInstance parseEffect(String name) {
        int duration = 200; // 10 seconds default
        int amplifier = 0;

        switch (name.toLowerCase()) {
            case "speed":
                return new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, amplifier);
            case "strength":
                return new MobEffectInstance(MobEffects.DAMAGE_BOOST, duration, amplifier);
            case "resistance":
                return new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, amplifier);
            case "regeneration":
                return new MobEffectInstance(MobEffects.REGENERATION, duration, amplifier);
            case "fire_resistance":
                return new MobEffectInstance(MobEffects.FIRE_RESISTANCE, duration, amplifier);
            case "haste":
                return new MobEffectInstance(MobEffects.DIG_SPEED, duration, amplifier);
            case "jump":
                return new MobEffectInstance(MobEffects.JUMP, duration, amplifier);
            default:
                return null;
        }
    }
}
