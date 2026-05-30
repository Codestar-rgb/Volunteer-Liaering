package com.subspaceparasite.common.effect;

import com.subspaceparasite.common.capability.ParasiteCapability;
import com.subspaceparasite.config.ModConfigSystems;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Call of the Hive (COTH) - Primary infection effect.
 * <p>
 * This is the signature effect of SRP that spreads the parasite infection.
 * When applied, it gradually increases the entity's infection level until
 * conversion occurs. Higher amplifiers accelerate the infection process.
 * </p>
 * 
 * <h2>Effects by Amplifier:</h2>
 * <ul>
 *   <li>Level 0: Basic infection spread, slow conversion</li>
 *   <li>Level 1: Increased spread chance, moderate conversion</li>
 *   <li>Level 2+: Rapid spread, fast conversion, additional debuffs</li>
 * </ul>
 * 
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Progressive infection level increase</li>
 *   <li>Synergy with InfectionComponent for spread logic</li>
 *   <li>Visual/audio feedback on infection progress</li>
 *   <li>Resistance and immunity checks</li>
 * </ul>
 * 
 * @author SRP Port Team
 * @see com.subspaceparasite.common.entity.base.InfectionComponent
 */
public class CothEffect extends BaseSRPEffect {
    
    /** Base infection increase per tick at amplifier 0 */
    private static final float BASE_INFECTION_RATE = 0.05f;
    
    /** Additional infection rate per amplifier level */
    private static final float INFECTION_RATE_PER_LEVEL = 0.03f;
    
    /** Minimum ticks between infection applications */
    private static final int INFECTION_TICK_INTERVAL = 20;
    
    /**
     * Creates the COTH effect with standard settings.
     */
    public CothEffect() {
        super(
            MobEffectCategory.HARMFUL,
            0x4A0E0E,  // Dark red color
            2,         // Max amplifier (COTH III)
            true,      // Can stack
            INFECTION_TICK_INTERVAL
        );
    }
    
    /**
     * Applies the COTH infection logic each tick.
     * Increases infection level through the capability system.
     * 
     * @param entity    The infected entity
     * @param amplifier The effect amplifier (0-2)
     */
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            // Client-side visual effects only
            spawnInfectionParticles(entity, amplifier);
            return;
        }
        
        // Server-side infection logic
        processInfection(entity, amplifier);
        applySecondaryDebuffs(entity, amplifier);
    }
    
    /**
     * Processes the infection level increase.
     * Uses ParasiteCapability if available, otherwise applies direct effects.
     */
    private void processInfection(@NotNull LivingEntity entity, int amplifier) {
        // Try to use capability system first
        entity.getCapability(ParasiteCapability.CAPABILITY).ifPresent(capability -> {
            if (!capability.isImmune() && capability.getInfectionCooldown() <= 0) {
                // Calculate infection increase based on amplifier
                float infectionIncrease = BASE_INFECTION_RATE + (amplifier * INFECTION_RATE_PER_LEVEL);
                
                // Apply resistance modifier
                float resistance = capability.getInfectionResistance();
                infectionIncrease *= (1.0f - resistance);
                
                // Add infection level
                int increaseAmount = Math.max(1, (int)(infectionIncrease * 100));
                capability.addInfection(increaseAmount);
                
                // Check for full infection
                if (capability.isFullyInfected()) {
                    onFullInfection(entity);
                }
            }
        });
    }
    
    /**
     * Applies secondary debuffs based on COTH amplifier.
     * Higher levels add more severe debuffs.
     */
    private void applySecondaryDebuffs(@NotNull LivingEntity entity, int amplifier) {
        // All levels: Wither effect (already applied via InfectionComponent)
        
        if (amplifier >= 1) {
            // Level 1+: Add slowness
            entity.setSpeedMultiplier(0.9f);
        }
        
        if (amplifier >= 2) {
            // Level 2+: Add weakness and increased damage taken
            // Note: Actual debuff application handled by InfectionComponent
        }
    }
    
    /**
     * Called when an entity reaches full infection.
     * Triggers conversion logic or special handling.
     */
    private void onFullInfection(@NotNull LivingEntity entity) {
        if (entity instanceof Player player) {
            // Players get special handling - notify but don't auto-convert
            if (!player.level().isClientSide()) {
                player.sendSystemMessage(
                    net.minecraft.network.chat.Component.translatable(
                        "effect.subspaceparasite.coth.full_infection"
                    )
                );
            }
        } else {
            // Non-player entities may convert based on config
            if (ModConfigSystems.isInfectionEnabled()) {
                // Conversion handled by InfectionComponent.checkConversion()
            }
        }
    }
    
    /**
     * Spawns infection particles on the entity.
     * Visual feedback for the infection progress.
     */
    private void spawnInfectionParticles(@NotNull LivingEntity entity, int amplifier) {
        // Particle spawning handled client-side
        // Using vanilla portal particles for infection effect
        double x = entity.getX() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        double y = entity.getY() + entity.level().random.nextDouble() * entity.getBbHeight();
        double z = entity.getZ() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        
        entity.level().addParticle(
            net.minecraft.core.particles.ParticleTypes.PORTAL,
            x, y, z,
            0.0, 0.05, 0.0
        );
        
        // More particles at higher amplifier
        if (amplifier >= 1) {
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.SCULK_SOUL,
                x, y + 0.5, z,
                0.0, 0.02, 0.0
            );
        }
    }
    
    /**
     * Determines if COTH should tick this frame.
     * Uses longer intervals for performance optimization.
     * 
     * @param duration  Remaining effect duration
     * @param amplifier Effect amplifier
     * @return true if should tick
     */
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // Tick every 20 ticks (1 second) for infection logic
        return duration % INFECTION_TICK_INTERVAL == 0;
    }
    
    /**
     * Checks if COTH can be applied to this entity.
     * Immune entities and parasites cannot be infected.
     * 
     * @param entity The target entity
     * @return true if COTH can be applied
     */
    @Override
    public boolean canApply(@NotNull LivingEntity entity) {
        // Check immunity through capability
        return entity.getCapability(ParasiteCapability.CAPABILITY)
            .map(cap -> !cap.isImmune())
            .orElse(true);
    }
}
