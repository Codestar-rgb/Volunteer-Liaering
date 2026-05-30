package com.subspaceparasite.common.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Fear Effect - Forces entity to flee and causes trembling
 * Represents the terror induced by parasite presence
 * 
 * @author SRP Port Team
 */
public class FearEffect extends BaseSRPEffect {
    
    private static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("f4a5b6c7-d8e9-0f1a-2b3c-4d5e6f7a8b9c");
    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("a5b6c7d8-e9f0-1a2b-3c4d-5e6f7a8b9c0d");
    private static final double SPEED_PENALTY = 0.20;
    private static final double DAMAGE_PENALTY = 0.30;
    
    public FearEffect() {
        super(MobEffectCategory.HARMFUL, 0x363636);
    }
    
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnFearParticles(entity);
            return;
        }
        
        // Apply trembling effect (random movement disruption)
        if (entity.tickCount % 10 == 0 && entity.getRandom().nextFloat() < 0.3f) {
            entity.push(
                (entity.getRandom().nextDouble() - 0.5) * 0.5,
                0,
                (entity.getRandom().nextDouble() - 0.5) * 0.5
            );
        }
        
        // Force entity to look around nervously
        if (entity.tickCount % 20 == 0) {
            entity.yHeadRot += (entity.getRandom().nextFloat() - 0.5f) * 30.0f;
        }
    }
    
    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
    
    @Override
    public void addAttributeModifiers(@NotNull LivingEntity entity, 
                                     @NotNull net.minecraft.world.entity.ai.attributes.AttributeMap attributes, 
                                     int amplifier) {
        AttributeModifier speedModifier = new AttributeModifier(
            MOVEMENT_SPEED_UUID,
            "Fear Speed Penalty",
            -SPEED_PENALTY,
            AttributeModifier.Operation.MULTIPLY_TOTAL
        );
        attributes.addAttributeInstance(Attributes.MOVEMENT_SPEED, speedModifier);
        
        AttributeModifier damageModifier = new AttributeModifier(
            ATTACK_DAMAGE_UUID,
            "Fear Damage Penalty",
            -DAMAGE_PENALTY,
            AttributeModifier.Operation.MULTIPLY_TOTAL
        );
        attributes.addAttributeInstance(Attributes.ATTACK_DAMAGE, damageModifier);
    }
    
    @Override
    public void removeAttributeModifiers(@NotNull LivingEntity entity, 
                                        @NotNull net.minecraft.world.entity.ai.attributes.AttributeMap attributes, 
                                        int amplifier) {
        AttributeModifier speedModifier = new AttributeModifier(
            MOVEMENT_SPEED_UUID,
            "Fear Speed Penalty",
            -SPEED_PENALTY,
            AttributeModifier.Operation.MULTIPLY_TOTAL
        );
        attributes.removeAttributeInstance(Attributes.MOVEMENT_SPEED, speedModifier);
        
        AttributeModifier damageModifier = new AttributeModifier(
            ATTACK_DAMAGE_UUID,
            "Fear Damage Penalty",
            -DAMAGE_PENALTY,
            AttributeModifier.Operation.MULTIPLY_TOTAL
        );
        attributes.removeAttributeInstance(Attributes.ATTACK_DAMAGE, damageModifier);
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
