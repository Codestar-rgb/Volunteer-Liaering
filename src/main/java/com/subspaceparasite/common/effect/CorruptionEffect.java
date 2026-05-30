package com.subspaceparasite.common.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Corruption Effect - Continuous damage and max health reduction
 * Represents the corrupting influence of the parasite on host biology
 * 
 * @author SRP Port Team
 */
public class CorruptionEffect extends BaseSRPEffect {
    
    private static final UUID MAX_HEALTH_UUID = UUID.fromString("c1d2e3f4-a5b6-7c8d-9e0f-1a2b3c4d5e6f");
    private static final float DAMAGE_PER_TICK = 0.25f;
    private static final double MAX_HEALTH_REDUCTION_PER_LEVEL = 0.10;
    
    public CorruptionEffect() {
        super(MobEffectCategory.HARMFUL, 0x4B0082);
    }
    
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            return;
        }
        
        // Apply magic damage over time
        float damage = DAMAGE_PER_TICK * (amplifier + 1);
        if (entity.tickCount % 20 == 0) {
            entity.hurt(entity.damageSources().magic(), damage);
        }
        
        // Spawn corruption particles
        if (entity.tickCount % 40 == 0) {
            spawnCorruptionParticles(entity);
        }
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
    
    @Override
    public void addAttributeModifiers(@NotNull LivingEntity entity, 
                                     @NotNull net.minecraft.world.entity.ai.attributes.AttributeMap attributes, 
                                     int amplifier) {
        double reduction = MAX_HEALTH_REDUCTION_PER_LEVEL * (amplifier + 1);
        AttributeModifier modifier = new AttributeModifier(
            MAX_HEALTH_UUID,
            "Corruption Max Health Reduction",
            -reduction,
            AttributeModifier.Operation.MULTIPLY_TOTAL
        );
        attributes.addAttributeInstance(Attributes.MAX_HEALTH, modifier);
        
        // Force health update
        if (entity.getHealth() > entity.getMaxHealth()) {
            entity.setHealth(entity.getMaxHealth());
        }
    }
    
    @Override
    public void removeAttributeModifiers(@NotNull LivingEntity entity, 
                                        @NotNull net.minecraft.world.entity.ai.attributes.AttributeMap attributes, 
                                        int amplifier) {
        AttributeModifier modifier = new AttributeModifier(
            MAX_HEALTH_UUID,
            "Corruption Max Health Reduction",
            -MAX_HEALTH_REDUCTION_PER_LEVEL * (amplifier + 1),
            AttributeModifier.Operation.MULTIPLY_TOTAL
        );
        attributes.removeAttributeInstance(Attributes.MAX_HEALTH, modifier);
    }
    
    private void spawnCorruptionParticles(LivingEntity entity) {
        for (int i = 0; i < 5; i++) {
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
