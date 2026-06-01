package com.subspaceparasite.common.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Corruption Effect - Continuous damage and max health reduction.
 * Represents the corrupting influence of the parasite on host biology.
 * 
 * <h2>Effects by Amplifier:</h2>
 * <ul>
 *   <li>Level 0: Minor magic damage over time, -10% max health</li>
 *   <li>Level 1: Moderate damage, -20% max health</li>
 *   <li>Level 2+: Severe damage, -30% max health + visual corruption</li>
 * </ul>
 * 
 * @author SRP Port Team
 */
public class CorruptionEffect extends BaseSRPEffect {
    
    private static final UUID MAX_HEALTH_UUID = UUID.fromString("c1d2e3f4-a5b6-7c8d-9e0f-1a2b3c4d5e6f");
    private static final float DAMAGE_PER_TICK = 0.25f;
    private static final double MAX_HEALTH_REDUCTION_PER_LEVEL = 0.10;
    
    public CorruptionEffect() {
        super(MobEffectCategory.HARMFUL, 0x4B0082, 3, true, 20);
    }
    
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnCorruptionParticles(entity, amplifier);
            return;
        }
        
        // Apply magic damage over time
        float damage = DAMAGE_PER_TICK * (amplifier + 1);
        entity.hurt(entity.damageSources().magic(), damage);
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
    
    @Override
    public void addAttributeModifiers(LivingEntity entity,
                                     AttributeMap attributeMap,
                                     int amplifier) {
        AttributeInstance attr = entity.getAttribute(Attributes.MAX_HEALTH);
        if (attr != null) {
            double reduction = MAX_HEALTH_REDUCTION_PER_LEVEL * (amplifier + 1);
            AttributeModifier modifier = new AttributeModifier(
                MAX_HEALTH_UUID,
                "Corruption Max Health Reduction",
                -reduction,
                AttributeModifier.Operation.MULTIPLY_TOTAL
            );
            if (!attr.hasModifier(modifier)) {
                attr.addTransientModifier(modifier);
            }
            
            // Force health update
            if (entity.getHealth() > entity.getMaxHealth()) {
                entity.setHealth(entity.getMaxHealth());
            }
        }
    }
    
    @Override
    public void removeAttributeModifiers(LivingEntity entity,
                                        AttributeMap attributeMap,
                                        int amplifier) {
        AttributeInstance attr = entity.getAttribute(Attributes.MAX_HEALTH);
        if (attr != null) {
            attr.removeModifier(MAX_HEALTH_UUID);
        }
    }
    
    private void spawnCorruptionParticles(LivingEntity entity, int amplifier) {
        for (int i = 0; i < 3 + amplifier; i++) {
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
