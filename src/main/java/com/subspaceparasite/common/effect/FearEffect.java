package com.subspaceparasite.common.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Fear Effect - Forces entity to flee and causes trembling.
 * Represents the terror induced by parasite presence.
 * 
 * <h2>Effects by Amplifier:</h2>
 * <ul>
 *   <li>Level 0: Slowness, trembling, reduced attack damage</li>
 *   <li>Level 1: Stronger slowness, weakness, prevents targeting</li>
 *   <li>Level 2+: Paralysis-like effects, extreme debuffs</li>
 * </ul>
 * 
 * @author SRP Port Team
 */
public class FearEffect extends BaseSRPEffect {
    
    private static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("f4a5b6c7-d8e9-0f1a-2b3c-4d5e6f7a8b9c");
    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("a5b6c7d8-e9f0-1a2b-3c4d-5e6f7a8b9c0d");
    private static final double SPEED_PENALTY = 0.20;
    private static final double DAMAGE_PENALTY = 0.30;
    
    public FearEffect() {
        super(MobEffectCategory.HARMFUL, 0x363636, 3, true, 20);
    }
    
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnFearParticles(entity);
            return;
        }
        
        // Apply trembling effect (random movement disruption)
        if (entity.getRandom().nextFloat() < 0.3f) {
            entity.push(
                (entity.getRandom().nextDouble() - 0.5) * 0.3 * (amplifier + 1),
                0,
                (entity.getRandom().nextDouble() - 0.5) * 0.3 * (amplifier + 1)
            );
        }
        
        // Force entity to look around nervously
        entity.yHeadRot += (entity.getRandom().nextFloat() - 0.5f) * 20.0f * (amplifier + 1);
        
        // Apply slowness and weakness effects directly
        int slownessLevel = Math.min(4, amplifier + 1);
        MobEffectInstance currentSlowness = entity.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
        if (currentSlowness == null || currentSlowness.getAmplifier() < slownessLevel) {
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, slownessLevel, false, false, true));
        }
        
        // Level 1+: Apply weakness
        if (amplifier >= 1) {
            int weaknessLevel = Math.min(3, amplifier);
            MobEffectInstance currentWeakness = entity.getEffect(MobEffects.WEAKNESS);
            if (currentWeakness == null || currentWeakness.getAmplifier() < weaknessLevel) {
                entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, weaknessLevel, false, false, true));
            }
        }
        
        // Level 2+: Prevent targeting (clear attack target)
        if (amplifier >= 2 && entity instanceof net.minecraft.world.entity.Mob mob) {
            mob.setTarget(null);
            mob.setAggressive(false);
        }
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
    
    @Override
    public void addAttributeModifiers(LivingEntity entity,
                                     AttributeMap attributeMap,
                                     int amplifier) {
        AttributeInstance speedAttr = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            double penalty = -SPEED_PENALTY * (amplifier + 1);
            AttributeModifier speedModifier = new AttributeModifier(
                MOVEMENT_SPEED_UUID,
                "Fear Speed Penalty",
                penalty,
                AttributeModifier.Operation.MULTIPLY_TOTAL
            );
            if (!speedAttr.hasModifier(speedModifier)) {
                speedAttr.addTransientModifier(speedModifier);
            }
        }
        AttributeInstance damageAttr = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damageAttr != null) {
            double penalty = -DAMAGE_PENALTY * (amplifier + 1);
            AttributeModifier damageModifier = new AttributeModifier(
                ATTACK_DAMAGE_UUID,
                "Fear Damage Penalty",
                penalty,
                AttributeModifier.Operation.MULTIPLY_TOTAL
            );
            if (!damageAttr.hasModifier(damageModifier)) {
                damageAttr.addTransientModifier(damageModifier);
            }
        }
    }
    
    @Override
    public void removeAttributeModifiers(LivingEntity entity,
                                        AttributeMap attributeMap,
                                        int amplifier) {
        AttributeInstance speedAttr = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.removeModifier(MOVEMENT_SPEED_UUID);
        }
        AttributeInstance damageAttr = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damageAttr != null) {
            damageAttr.removeModifier(ATTACK_DAMAGE_UUID);
        }
    }
    
    private void spawnFearParticles(LivingEntity entity) {
        for (int i = 0; i < 3; i++) {
            double offsetX = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            double offsetY = entity.getRandom().nextDouble() * entity.getBbHeight();
            double offsetZ = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.SCULK_SOUL,
                entity.getX() + offsetX,
                entity.getY() + offsetY,
                entity.getZ() + offsetZ,
                0, 0.05, 0
            );
        }
    }
}
