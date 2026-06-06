package com.subspaceparasite.common.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Bleeding effect - Causes continuous damage based on movement.
 * <p>
 * Entities with this effect take damage proportional to their movement speed.
 * Standing still reduces damage taken, while sprinting increases it significantly.
 * This creates tactical gameplay where infected entities must choose between
 * fleeing (and taking more damage) or staying put (risking conversion).
 * </p>
 * 
 * <h2>Damage Formula:</h2>
 * <pre>
 * baseDamage = 0.5 + (amplifier * 0.25)
 * movementMultiplier = 1.0 + (speedRatio * 2.0)
 * totalDamage = baseDamage * movementMultiplier
 * </pre>
 * 
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Movement-based damage scaling</li>
 *   <li>Blood particle effects</li>
 *   <li>Synergy with COTH for faster conversion</li>
 *   <li>Visual wound indicators</li>
 * </ul>
 * 
 * @author SRP Port Team
 */
public class BleedEffect extends BaseSRPEffect {
    
    /** Base damage per tick at amplifier 0 */
    private static final float BASE_DAMAGE = 0.5f;
    
    /** Additional damage per amplifier level */
    private static final float DAMAGE_PER_LEVEL = 0.25f;
    
    /** Movement speed multiplier for damage calculation */
    private static final float MOVEMENT_DAMAGE_MULTIPLIER = 2.0f;
    
    /** Tick interval for damage application */
    private static final int BLEED_TICK_INTERVAL = 10;
    
    /**
     * Creates the Bleeding effect with standard settings.
     */
    public BleedEffect() {
        super(
            MobEffectCategory.HARMFUL,
            0x8B0000,  // Dark blood red
            3,         // Max amplifier
            true,      // Can stack
            BLEED_TICK_INTERVAL
        );
    }
    
    /**
     * Applies bleeding damage based on entity movement.
     * 
     * @param entity    The bleeding entity
     * @param amplifier The effect amplifier
     */
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnBloodParticles(entity, amplifier);
            return;
        }
        
        // Calculate damage based on movement
        float damage = calculateBleedDamage(entity, amplifier);
        
        if (damage > 0) {
            entity.hurt(entity.damageSources().magic(), damage);
        }
    }
    
    /**
     * Calculates bleed damage based on movement speed and amplifier.
     * 
     * @param entity    The target entity
     * @param amplifier Effect amplifier
     * @return Damage amount
     */
    private float calculateBleedDamage(@NotNull LivingEntity entity, int amplifier) {
        // Base damage from amplifier
        float baseDamage = BASE_DAMAGE + (amplifier * DAMAGE_PER_LEVEL);
        
        // Get movement speed ratio (0-1 normalized)
        float currentSpeed = (float) entity.getDeltaMovement().horizontalDistance();
        float maxSpeed = entity.getSpeed();
        float speedRatio = maxSpeed > 0 ? Math.min(1.0f, currentSpeed / maxSpeed) : 0.0f;
        
        // Apply movement multiplier
        float movementMultiplier = 1.0f + (speedRatio * MOVEMENT_DAMAGE_MULTIPLIER);
        
        // Final damage calculation
        return baseDamage * movementMultiplier;
    }
    
    /**
     * Spawns blood particles around the entity.
     * More particles at higher amplifier levels.
     */
    private void spawnBloodParticles(@NotNull LivingEntity entity, int amplifier) {
        double x = entity.getX() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        double y = entity.getY() + 0.5 + entity.level().random.nextDouble() * entity.getBbHeight() * 0.5;
        double z = entity.getZ() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        
        // Base particle count
        int particleCount = 1 + amplifier;
        
        for (int i = 0; i < particleCount; i++) {
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.DRIPPING_HONEY, // Using as blood proxy
                x, y, z,
                0.0, -0.1, 0.0
            );
        }
        
        // Add heart particles at high amplifier
        if (amplifier >= 2) {
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.HEART,
                x, y + 0.2, z,
                0.0, 0.05, 0.0
            );
        }
    }
    
    /**
     * Determines if bleed should tick this frame.
     * 
     * @param duration  Remaining duration
     * @param amplifier Effect amplifier
     * @return true if should tick
     */
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % BLEED_TICK_INTERVAL == 0;
    }
}
