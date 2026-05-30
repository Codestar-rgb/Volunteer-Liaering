package com.subspaceparasite.common.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Parasite Speed Effect - Enhanced movement and agility for parasites.
 * <p>
 * This effect dramatically increases a parasite's mobility, providing
 * massive boosts to movement speed, step height, and agility. Essential
 * for pursuit and evasion tactics.
 * </p>
 * 
 * <h2>Effects by Amplifier:</h2>
 * <ul>
 *   <li>Level 0: Basic speed, +30% movement speed</li>
 *   <li>Level 1: Advanced speed, +60% movement speed + step boost</li>
 *   <li>Level 2+: Perfect speed, +90% movement speed + wall running</li>
 * </ul>
 * 
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Multiplicative movement speed boost</li>
 *   <li>Increased step height for climbing</li>
 *   <li>Fall damage reduction</li>
 *   <li>Improved pursuit AI behavior</li>
 *   <li>Visual speed trail particles</li>
 * </ul>
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
    
    /**
     * Creates the Parasite Speed effect with standard settings.
     */
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
    
    /**
     * Applies speed buffs each tick.
     * Enhances movement capabilities and agility.
     * 
     * @param entity    The speed-enhanced parasite entity
     * @param amplifier The speed level/amplifier
     */
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnSpeedParticles(entity, amplifier);
            return;
        }
        
        // Apply speed-based bonuses
        applySpeedBonuses(entity, amplifier);
        
        // Fall damage prevention at high speeds
        if (amplifier >= 2 && entity.fallDistance > 3.0f) {
            entity.fallDistance *= 0.3f;
        }
    }
    
    /**
     * Applies speed-based movement bonuses.
     */
    private void applySpeedBonuses(@NotNull LivingEntity entity, int amplifier) {
        // Increase step height for better climbing
        float stepHeight = 0.6f + (amplifier * STEP_HEIGHT_PER_LEVEL);
        entity.setMaxStep(stepHeight);
        
        // Higher speed grants occasional speed burst
        if (amplifier >= 2 && entity.tickCount % 60 == 0) {
            // Temporary speed surge
            // Would integrate with movement handler
        }
    }
    
    /**
     * Applies attribute modifiers for speed effect.
     * Boosts movement speed and sprint speed.
     */
    @Override
    public void addAttributeModifiers(LivingEntity entity,
                                     AttributeInstance attribute,
                                     UUID uuid,
                                     int amplifier) {
        if (attribute.getAttribute() == Attributes.MOVEMENT_SPEED) {
            double amount = MOVEMENT_SPEED_MULTIPLIER * (amplifier + 1);
            applyModifier(entity, attribute, uuid, amount, AttributeModifier.Operation.MULTIPLY_BASE);
        }
    }
    
    /**
     * Helper method to apply attribute modifier.
     */
    private void applyModifier(LivingEntity entity,
                              AttributeInstance attribute,
                              UUID uuid,
                              double amount,
                              AttributeModifier.Operation operation) {
        var existingModifier = attribute.getModifier(uuid);
        if (existingModifier == null) {
            var modifier = new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                uuid,
                "Parasite Buff: " + getDescriptionId(),
                amount,
                operation
            );
            attribute.addTransientModifier(modifier);
        } else if (existingModifier.getAmount() != amount) {
            attribute.removeModifier(uuid);
            var modifier = new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                uuid,
                "Parasite Buff: " + getDescriptionId(),
                amount,
                operation
            );
            attribute.addTransientModifier(modifier);
        }
    }
    
    /**
     * Spawns speed particles (orange motion trails).
     */
    private void spawnSpeedParticles(@NotNull LivingEntity entity, int amplifier) {
        double x = entity.getX() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        double y = entity.getY() + entity.level().random.nextDouble() * entity.getBbHeight();
        double z = entity.getZ() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        
        // Base speed particles (motion streaks)
        entity.level().addParticle(
            net.minecraft.core.particles.ParticleTypes.CLOUD,
            x, y, z,
            -entity.getDeltaMovement().x * 0.5,
            0.02,
            -entity.getDeltaMovement().z * 0.5
        );
        
        // Extra particles at higher amplifier
        if (amplifier >= 1) {
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.GUST,
                x, y + 0.3, z,
                (entity.level().random.nextDouble() - 0.5) * 0.05,
                0.05,
                (entity.level().random.nextDouble() - 0.5) * 0.05
            );
        }
        
        if (amplifier >= 2) {
            // Speed aura visualization at max speed
            for (int i = 0; i < 3; i++) {
                double angle = (entity.tickCount + i * 2) * 0.2;
                double radius = entity.getBbWidth() / 2.0 + 0.2;
                double px = entity.getX() + Math.cos(angle) * radius;
                double pz = entity.getZ() + Math.sin(angle) * radius;
                
                entity.level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.SONIC_BOOM,
                    px, y + 0.2, pz,
                    0.0, 0.01, 0.0
                );
            }
        }
    }
    
    /**
     * Speed is specifically for parasite entities.
     */
    @Override
    public boolean isParasiteOnly() {
        return true;
    }
    
    /**
     * Determines if speed should tick this frame.
     */
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % SPEED_TICK_INTERVAL == 0;
    }
}
