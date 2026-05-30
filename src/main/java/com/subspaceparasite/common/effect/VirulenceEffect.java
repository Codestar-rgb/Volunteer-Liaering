package com.subspaceparasite.common.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Virulence Effect - Increases infection spread rate and potency.
 * <p>
 * This effect represents the heightened infectiousness of parasite attacks.
 * Entities with virulence spread infection more rapidly to nearby entities
 * and their attacks carry stronger infection properties.
 * </p>
 * 
 * <h2>Effects by Amplifier:</h2>
 * <ul>
 *   <li>Level 0: +25% infection spread rate, basic contagion</li>
 *   <li>Level 1: +50% infection spread rate, moderate contagion</li>
 *   <li>Level 2+: +100% infection spread rate, severe contagion aura</li>
 * </ul>
 * 
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Increases infection spread multiplier for affected entity</li>
 *   <li>Creates passive infection aura at higher amplifiers</li>
 *   <li>Synergizes with COTH for rapid infection chains</li>
 *   <li>Visual indicators showing contagious state</li>
 * </ul>
 * 
 * @author SRP Port Team
 * @see InfectionEffectBase
 */
public class VirulenceEffect extends InfectionEffectBase {
    
    /** Spread rate increase per amplifier (percentage) */
    private static final float SPREAD_RATE_PER_LEVEL = 0.25f;
    
    /** Aura range in blocks */
    private static final float AURA_RANGE = 3.0f;
    
    /** Aura tick interval in ticks */
    private static final int AURA_TICK_INTERVAL = 60;
    
    /** Self-infection chance per tick when virulent */
    private static final float SELF_INFECTION_CHANCE = 0.01f;
    
    /**
     * Creates the Virulence effect with standard settings.
     */
    public VirulenceEffect() {
        super(
            MobEffectCategory.HARMFUL,
            0x9B3020,  // Rust red color
            0.08f,     // Base infection rate
            0.04f,     // Infection rate per level
            false      // Does not directly trigger conversion
        );
    }
    
    /**
     * Applies virulence effects each tick.
     * Enhances infection spread and may create contagion aura.
     * 
     * @param entity    The virulent entity
     * @param amplifier The effect amplifier
     */
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnVirulenceParticles(entity, amplifier);
            return;
        }
        
        // Apply self-infection risk (represents worsening condition)
        if (entity.level().random.nextFloat() < SELF_INFECTION_CHANCE * amplifier) {
            applyInfection(entity, amplifier);
        }
        
        // Create infection aura at higher amplifiers
        if (amplifier >= 2 && entity.srpTicks % AURA_TICK_INTERVAL == 0) {
            spreadInfectionAura(entity, amplifier);
        }
    }
    
    /**
     * Spreads infection to nearby entities through contagion aura.
     * 
     * @param entity    The source entity
     * @param amplifier The effect amplifier
     */
    private void spreadInfectionAura(@NotNull LivingEntity entity, int amplifier) {
        var nearbyEntities = entity.level().getEntitiesOfClass(
            LivingEntity.class,
            entity.getBoundingBox().inflate(AURA_RANGE),
            e -> e != entity && !e.isSpectator()
        );
        
        for (LivingEntity nearby : nearbyEntities) {
            // Skip parasites and immune entities
            if (nearby instanceof com.subspaceparasite.common.entity.base.EntityParasiteBase) {
                continue;
            }
            
            // Chance to infect based on amplifier
            float infectChance = SPREAD_RATE_PER_LEVEL * amplifier;
            if (entity.level().random.nextFloat() < infectChance) {
                // Apply minor infection to nearby entity
                nearby.getCapability(com.subspaceparasite.common.capability.ParasiteCapability.CAPABILITY)
                    .ifPresent(cap -> {
                        if (!cap.isImmune()) {
                            cap.addInfection(1 + amplifier);
                        }
                    });
            }
        }
    }
    
    /**
     * Spawns virulence particles (red/orange spores).
     * 
     * @param entity    The virulent entity
     * @param amplifier The effect amplifier
     */
    private void spawnVirulenceParticles(@NotNull LivingEntity entity, int amplifier) {
        double x = entity.getX() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        double y = entity.getY() + entity.level().random.nextDouble() * entity.getBbHeight();
        double z = entity.getZ() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        
        // Red particle indicating contagious state
        entity.level().addParticle(
            net.minecraft.core.particles.ParticleTypes.REDSTONE,
            x, y, z,
            0.0, 0.02, 0.0
        );
        
        // Aura particles at higher amplifier
        if (amplifier >= 2) {
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.SMOKE,
                x, y + 0.5, z,
                (entity.level().random.nextDouble() - 0.5) * 0.05,
                0.03,
                (entity.level().random.nextDouble() - 0.5) * 0.05
            );
        }
    }
    
    /**
     * Determines if virulence should tick this frame.
     * 
     * @param duration  Remaining duration
     * @param amplifier Effect amplifier
     * @return true if should tick
     */
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // Tick every 10 ticks for balance
        return duration % 10 == 0;
    }
    
    /**
     * Gets the effective spread rate multiplier for this amplifier.
     * 
     * @param amplifier The effect amplifier
     * @return Spread rate multiplier (1.0 = base rate)
     */
    public float getSpreadRateMultiplier(int amplifier) {
        return 1.0f + (amplifier * SPREAD_RATE_PER_LEVEL);
    }
    
    /**
     * Gets the aura range for this effect.
     * 
     * @return Aura range in blocks
     */
    public float getAuraRange() {
        return AURA_RANGE;
    }
}
