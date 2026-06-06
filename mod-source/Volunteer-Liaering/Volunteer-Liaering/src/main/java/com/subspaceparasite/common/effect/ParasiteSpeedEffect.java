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
 * Parasite Speed Effect - Enhanced movement and agility for parasites.
 * <p>
 * This effect dramatically increases a parasite's mobility, providing
 * massive boosts to movement speed and step height.
 * </p>
 * 
 * @author SRP Port Team
 * @see ParasiteBuffEffectBase
 */
public class ParasiteSpeedEffect extends ParasiteBuffEffectBase {
    
    /** Movement speed multiplier per amplifier level */
    private static final double MOVEMENT_SPEED_MULTIPLIER = 0.3;
    
    /** Step height boost per amplifier level */
    private static final float STEP_HEIGHT_PER_LEVEL = 0.5f;
    
    /** Tick interval for speed logic */
    private static final int SPEED_TICK_INTERVAL = 10;
    
    /** UUID for speed modifier */
    private static final UUID SPEED_UUID = UUID.fromString("a2b3c4d5-e6f7-8901-abcd-ef2345678901");
    
    public ParasiteSpeedEffect() {
        super(
            MobEffectCategory.BENEFICIAL,
            0xFF8C00,  // Dark orange (speed/agility color)
            3,         // Max amplifier
            true,      // Can stack
            SPEED_TICK_INTERVAL,
            true,      // Has attribute modifiers
            MOVEMENT_SPEED_MULTIPLIER,
            AttributeModifier.Operation.MULTIPLY_BASE
        );
    }
    
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnSpeedParticles(entity, amplifier);
            return;
        }
        
        // Increase step height for better climbing
        float stepHeight = 0.6f + (amplifier * STEP_HEIGHT_PER_LEVEL);
        entity.setMaxUpStep(stepHeight);
        
        // Fall damage reduction at high speeds
        if (amplifier >= 2 && entity.fallDistance > 3.0f) {
            entity.fallDistance *= 0.3f;
        }
    }
    
    @Override
    public void addAttributeModifiers(LivingEntity entity,
                                     AttributeMap attributeMap,
                                     int amplifier) {
        AttributeInstance attr = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null) {
            double amount = MOVEMENT_SPEED_MULTIPLIER * (amplifier + 1);
            AttributeModifier modifier = new AttributeModifier(
                SPEED_UUID,
                "Parasite Buff: " + getDescriptionId(),
                amount,
                AttributeModifier.Operation.MULTIPLY_BASE
            );
            if (!attr.hasModifier(modifier)) {
                attr.addTransientModifier(modifier);
            }
        }
    }
    
    @Override
    public void removeAttributeModifiers(LivingEntity entity,
                                        AttributeMap attributeMap,
                                        int amplifier) {
        AttributeInstance attr = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null) {
            attr.removeModifier(SPEED_UUID);
        }
        // Reset step height
        entity.setMaxUpStep(0.6f);
    }
    
    private void spawnSpeedParticles(@NotNull LivingEntity entity, int amplifier) {
        double x = entity.getX() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        double y = entity.getY() + entity.level().random.nextDouble() * entity.getBbHeight();
        double z = entity.getZ() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        
        entity.level().addParticle(
            net.minecraft.core.particles.ParticleTypes.CLOUD,
            x, y, z,
            -entity.getDeltaMovement().x * 0.5,
            0.02,
            -entity.getDeltaMovement().z * 0.5
        );
        
        if (amplifier >= 1) {
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.CLOUD,
                x, y + 0.3, z,
                (entity.level().random.nextDouble() - 0.5) * 0.05,
                0.05,
                (entity.level().random.nextDouble() - 0.5) * 0.05
            );
        }
    }
    
    @Override
    public boolean isParasiteOnly() {
        return true;
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % SPEED_TICK_INTERVAL == 0;
    }
}
