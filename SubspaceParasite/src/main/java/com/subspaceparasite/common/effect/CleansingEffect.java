package com.subspaceparasite.common.effect;

import com.subspaceparasite.common.capability.ParasiteCapability;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Cleansing Effect - Continuous purification and infection resistance.
 * <p>
 * This effect provides ongoing protection against parasite infection by
 * continuously reducing infection levels and building resistance over time.
 * Unlike Purge which is instant cleansing, Cleansing is a sustained defensive buff.
 * </p>
 * 
 * <h2>Effects by Amplifier:</h2>
 * <ul>
 *   <li>Level 0: Slow infection reduction, basic resistance build</li>
 *   <li>Level 1: Moderate infection reduction, enhanced resistance</li>
 *   <li>Level 2+: Rapid infection reduction, maximum resistance + effect cleanse</li>
 * </ul>
 * 
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Continuous infection level reduction</li>
 *   <li>Progressive resistance building</li>
 *   <li>Periodic negative effect cleansing</li>
 *   <li>Synergy with Purge and Immunity effects</li>
 *   <li>Visual purification particles</li>
 * </ul>
 * 
 * @author SRP Port Team
 * @see ParasiteCapability
 * @see PurgeEffect
 */
public class CleansingEffect extends BaseSRPEffect {
    
    /** Infection reduction per tick at amplifier 0 */
    private static final int BASE_INFECTION_REDUCTION = 1;
    
    /** Additional reduction per amplifier level */
    private static final int REDUCTION_PER_LEVEL = 1;
    
    /** Resistance build rate per tick */
    private static final float BASE_RESISTANCE_BUILD = 0.005f;
    
    /** Maximum resistance cap from cleansing */
    private static final float MAX_RESISTANCE_FROM_CLEANSING = 0.8f;
    
    /** Tick interval for cleansing effects */
    private static final int CLEANSING_TICK_INTERVAL = 20;
    
    /** Interval for effect cleansing (in ticks) */
    private static final int EFFECT_CLEANSE_INTERVAL = 100;
    
    /**
     * Creates the Cleansing effect with standard settings.
     */
    public CleansingEffect() {
        super(
            MobEffectCategory.BENEFICIAL,
            0xE0FFFF,  // Light cyan (clean/pure color)
            2,         // Max amplifier
            true,      // Can stack
            CLEANSING_TICK_INTERVAL
        );
    }
    
    /**
     * Applies cleansing logic each tick.
     * Reduces infection and builds resistance.
     * 
     * @param entity    The entity being cleansed
     * @param amplifier The effect amplifier
     */
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnCleansingParticles(entity, amplifier);
            return;
        }
        
        // Reduce infection level continuously
        reduceInfection(entity, amplifier);
        
        // Build resistance over time
        buildResistance(entity, amplifier);
        
        // Periodic effect cleansing
        if (entity.tickCount % EFFECT_CLEANSE_INTERVAL == 0) {
            cleanseNegativeEffects(entity, amplifier);
        }
    }
    
    /**
     * Continuously reduces infection level.
     */
    private void reduceInfection(@NotNull LivingEntity entity, int amplifier) {
        entity.getCapability(ParasiteCapability.CAPABILITY).ifPresent(capability -> {
            if (capability.getInfectionLevel() > 0 && !capability.isImmune()) {
                int reduction = BASE_INFECTION_REDUCTION + (amplifier * REDUCTION_PER_LEVEL);
                capability.reduceInfection(reduction);
            }
        });
    }
    
    /**
     * Gradually builds infection resistance over time.
     */
    private void buildResistance(@NotNull LivingEntity entity, int amplifier) {
        entity.getCapability(ParasiteCapability.CAPABILITY).ifPresent(capability -> {
            float currentResistance = capability.getInfectionResistance();
            if (currentResistance < MAX_RESISTANCE_FROM_CLEANSING) {
                float buildRate = BASE_RESISTANCE_BUILD * (1.0f + amplifier * 0.5f);
                float newResistance = Math.min(MAX_RESISTANCE_FROM_CLEANSING, currentResistance + buildRate);
                capability.setInfectionResistance(newResistance);
            }
        });
    }
    
    /**
     * Removes negative parasite-related effects periodically.
     */
    private void cleanseNegativeEffects(@NotNull LivingEntity entity, int amplifier) {
        // Always remove basic infection debuffs
        entity.removeEffect(net.minecraft.world.effect.MobEffects.WITHER);
        entity.removeEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN);
        
        if (amplifier >= 1) {
            // Remove weakness and hunger
            entity.removeEffect(net.minecraft.world.effect.MobEffects.WEAKNESS);
            entity.removeEffect(net.minecraft.world.effect.MobEffects.HUNGER);
        }
        
        if (amplifier >= 2) {
            // Full cleanse - remove all harmful effects
            entity.getActiveEffects().removeIf(effectInstance -> 
                effectInstance.getEffect().getCategory() == MobEffectCategory.HARMFUL &&
                !(effectInstance.getEffect() instanceof CleansingEffect) &&
                !(effectInstance.getEffect() instanceof PurgeEffect) &&
                !(effectInstance.getEffect() instanceof ImmunityEffect)
            );
        }
    }
    
    /**
     * Called when the effect is removed.
     * Maintains partial resistance as a bonus.
     */
    @Override
    public void removeAttributeModifiers(LivingEntity entity,
                                        net.minecraft.world.entity.ai.attributes.AttributeInstance attribute,
                                        java.util.UUID uuid) {
        if (!entity.level().isClientSide()) {
            entity.getCapability(ParasiteCapability.CAPABILITY).ifPresent(capability -> {
                // Keep 50% of the resistance built from cleansing
                float currentResistance = capability.getInfectionResistance();
                if (currentResistance > 0.1f) {
                    capability.setInfectionResistance(currentResistance * 0.5f);
                }
            });
        }
    }
    
    /**
     * Spawns cleansing particles (light blue/white sparkles).
     */
    private void spawnCleansingParticles(@NotNull LivingEntity entity, int amplifier) {
        double x = entity.getX() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        double y = entity.getY() + entity.level().random.nextDouble() * entity.getBbHeight();
        double z = entity.getZ() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        
        // Base cleansing particles
        entity.level().addParticle(
            net.minecraft.core.particles.ParticleTypes.SPELL,
            x, y, z,
            0.0, 0.04, 0.0
        );
        
        // Extra particles at higher amplifier
        if (amplifier >= 1) {
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.ENCHANTED_HIT,
                x, y + 0.3, z,
                (entity.level().random.nextDouble() - 0.5) * 0.05,
                0.06,
                (entity.level().random.nextDouble() - 0.5) * 0.05
            );
        }
        
        if (amplifier >= 2) {
            // Swirling purification effect
            for (int i = 0; i < 2; i++) {
                double angle = (entity.tickCount + i * 3) * 0.15;
                double radius = entity.getBbWidth() / 2.0 + 0.1;
                double px = entity.getX() + Math.cos(angle) * radius;
                double pz = entity.getZ() + Math.sin(angle) * radius;
                
                entity.level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.TOTEM_OF_UNDYING,
                    px, y + 0.2, pz,
                    0.0, 0.02, 0.0
                );
            }
        }
    }
    
    /**
     * Determines if cleansing should tick this frame.
     */
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % CLEANSING_TICK_INTERVAL == 0;
    }
    
    /**
     * Cleansing can be applied to any living entity.
     */
    @Override
    public boolean canApply(@NotNull LivingEntity entity) {
        return entity.isAlive();
    }
}
