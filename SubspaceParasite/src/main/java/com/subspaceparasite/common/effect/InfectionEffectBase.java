package com.subspaceparasite.common.effect;

import com.subspaceparasite.common.capability.ParasiteCapability;
import com.subspaceparasite.config.ModConfigSystems;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for all infection-related effects.
 * <p>
 * Provides common functionality for infection progression, resistance handling,
 * and conversion tracking. All infection effects should extend this class
 * to ensure consistent behavior across the infection system.
 * </p>
 * 
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Standardized infection level increase logic</li>
 *   <li>Resistance and immunity checks through capability system</li>
 *   <li>Conversion threshold detection</li>
 *   <li>Infection source tracking</li>
 *   <li>Configurable infection rates</li>
 * </ul>
 * 
 * @author SRP Port Team
 * @see ParasiteCapability
 * @see CothEffect
 */
public abstract class InfectionEffectBase extends BaseSRPEffect {
    
    /** Base infection increase per tick */
    protected final float baseInfectionRate;
    
    /** Infection rate multiplier per amplifier level */
    protected final float infectionRatePerLevel;
    
    /** Whether this effect can trigger conversion */
    protected final boolean canTriggerConversion;
    
    /**
     * Creates a new infection effect with default settings.
     * 
     * @param category The effect category (should be HARMFUL)
     * @param color    The color for the effect icon (RGB packed int)
     * @param baseInfectionRate Base infection points per tick
     * @param infectionRatePerLevel Additional infection per amplifier
     * @param canTriggerConversion Whether this effect can trigger entity conversion
     */
    protected InfectionEffectBase(MobEffectCategory category, 
                                   int color,
                                   float baseInfectionRate,
                                   float infectionRatePerLevel,
                                   boolean canTriggerConversion) {
        super(category, color, 3, true, 20); // Max amp 3, can stack, 20 tick interval
        this.baseInfectionRate = baseInfectionRate;
        this.infectionRatePerLevel = infectionRatePerLevel;
        this.canTriggerConversion = canTriggerConversion;
    }
    
    /**
     * Applies infection to the target entity.
     * Uses the ParasiteCapability system for tracking.
     * 
     * @param entity    The target entity
     * @param amplifier The effect amplifier
     * @return true if infection was successfully applied
     */
    protected boolean applyInfection(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            return false;
        }
        
        return entity.getCapability(ParasiteCapability.CAPABILITY)
            .map(capability -> {
                // Check immunity
                if (capability.isImmune() || capability.getInfectionCooldown() > 0) {
                    return false;
                }
                
                // Calculate effective infection amount
                float infectionAmount = calculateInfectionAmount(amplifier);
                
                // Apply resistance modifier
                float resistance = capability.getInfectionResistance();
                infectionAmount *= (1.0f - resistance);
                
                // Apply infection
                int increaseAmount = Math.max(1, (int) infectionAmount);
                capability.addInfection(increaseAmount);
                
                // Check for conversion
                if (canTriggerConversion && capability.isFullyInfected()) {
                    onFullInfection(entity, amplifier);
                }
                
                return true;
            })
            .orElse(false);
    }
    
    /**
     * Calculates the infection amount based on amplifier and config.
     * 
     * @param amplifier The effect amplifier
     * @return The calculated infection amount
     */
    protected float calculateInfectionAmount(int amplifier) {
        float base = baseInfectionRate + (amplifier * infectionRatePerLevel);
        return base * ModConfigSystems.getInfectionSpreadMultiplier();
    }
    
    /**
     * Called when an entity reaches full infection.
     * Override to implement custom conversion logic.
     * 
     * @param entity    The fully infected entity
     * @param amplifier The effect amplifier
     */
    protected void onFullInfection(@NotNull LivingEntity entity, int amplifier) {
        // Default implementation - conversion handled by InfectionComponent
        if (!entity.level().isClientSide()) {
            spawnInfectionParticles(entity, amplifier);
        }
    }
    
    /**
     * Spawns infection particles (dark red/purple).
     * 
     * @param entity    The infected entity
     * @param amplifier The effect amplifier
     */
    protected void spawnInfectionParticles(@NotNull LivingEntity entity, int amplifier) {
        double x = entity.getX() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        double y = entity.getY() + entity.level().random.nextDouble() * entity.getBbHeight();
        double z = entity.getZ() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        
        // Dark portal particles for infection
        entity.level().addParticle(
            net.minecraft.core.particles.ParticleTypes.PORTAL,
            x, y, z,
            0.0, 0.05, 0.0
        );
        
        // Additional particles at higher amplifier
        if (amplifier >= 1) {
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.SCULK_SOUL,
                x, y + 0.5, z,
                0.0, 0.02, 0.0
            );
        }
        
        if (amplifier >= 2) {
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.ENCHANTED_HIT,
                x, y + 0.3, z,
                (entity.level().random.nextDouble() - 0.5) * 0.1,
                0.05,
                (entity.level().random.nextDouble() - 0.5) * 0.1
            );
        }
    }
    
    /**
     * Checks if this infection effect can be applied to the entity.
     * 
     * @param entity The target entity
     * @return true if infection can be applied
     */
    @Override
    public boolean canApply(@NotNull LivingEntity entity) {
        // Check immunity through capability
        return entity.getCapability(ParasiteCapability.CAPABILITY)
            .map(cap -> !cap.isImmune())
            .orElse(true);
    }
    
    /**
     * Gets the infection rate for this effect.
     * 
     * @return Base infection rate
     */
    public float getBaseInfectionRate() {
        return baseInfectionRate;
    }
    
    /**
     * Gets whether this effect can trigger conversion.
     * 
     * @return true if conversion can be triggered
     */
    public boolean canTriggerConversion() {
        return canTriggerConversion;
    }
}
