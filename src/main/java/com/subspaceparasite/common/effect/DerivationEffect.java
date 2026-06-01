package com.subspaceparasite.common.effect;

import com.subspaceparasite.common.capability.ParasiteCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

/**
 * Derivation Effect - Parasite branching evolution and specialization.
 * <p>
 * This effect represents a parasite's ability to derive into specialized forms
 * or unlock advanced evolutionary branches. Enables form switching and
 * grants bonuses based on the derived specialization.
 * </p>
 * 
 * <h2>Effects by Amplifier:</h2>
 * <ul>
 *   <li>Level 0: Basic derivation, access to primary specialization</li>
 *   <li>Level 1: Advanced derivation, secondary specialization unlocked</li>
 *   <li>Level 2+: Perfect derivation, all specializations available + hybrid bonuses</li>
 * </ul>
 * 
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Enables parasite form transformation</li>
 *   <li>Grants specialization-specific bonuses</li>
 *   <li>Synergizes with Evolution, Adaptation, and Sentience</li>
 *   <li>Tracks derivation progress for metamorphosis</li>
 *   <li>Visual derivation aura particles</li>
 * </ul>
 * 
 * @author SRP Port Team
 * @see ParasiteBuffEffectBase
 * @see EvolutionEffect
 */
public class DerivationEffect extends ParasiteBuffEffectBase {
    
    /** Derivation progress increment per tick */
    private static final float DERIVATION_PROGRESS_PER_TICK = 0.01f;
    
    /** Maximum derivation progress before metamorphosis */
    private static final float MAX_DERIVATION_PROGRESS = 1.0f;
    
    /** Tick interval for derivation logic */
    private static final int DERIVATION_TICK_INTERVAL = 20;
    
    /** NBT key for storing derivation progress per entity */
    private static final String NBT_KEY_DERIVATION = "SRPDerivationProgress";
    
    /**
     * Creates the Derivation effect with standard settings.
     */
    public DerivationEffect() {
        super(
            MobEffectCategory.BENEFICIAL,
            0x6A5ACD,  // Slate blue (transformation/derivation color)
            3,         // Max amplifier
            true,      // Can stack
            DERIVATION_TICK_INTERVAL,
            false,     // No attribute modifiers
            0.0,
            AttributeModifier.Operation.ADDITION
        );
    }
    
    /**
     * Applies derivation buffs each tick.
     * Tracks derivation progress and enables transformations.
     * 
     * @param entity    The deriving parasite entity
     * @param amplifier The derivation level/amplifier
     */
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnDerivationParticles(entity, amplifier);
            return;
        }
        
        // Update derivation progress
        updateDerivationProgress(entity, amplifier);
        
        // Check for metamorphosis readiness
        checkMetamorphosisReadiness(entity, amplifier);
        
        // Apply derivation bonuses
        applyDerivationBonuses(entity, amplifier);
    }
    
    /**
     * Updates derivation progress over time.
     * Progress is stored per-entity via capability NBT to avoid singleton state bug.
     */
    private void updateDerivationProgress(@NotNull LivingEntity entity, int amplifier) {
        float progressIncrease = DERIVATION_PROGRESS_PER_TICK * (1.0f + amplifier * 0.5f);
        float currentProgress = getDerivationProgress(entity);
        float newProgress = Math.min(MAX_DERIVATION_PROGRESS, currentProgress + progressIncrease);
        setDerivationProgress(entity, newProgress);
    }
    
    /**
     * Checks if the parasite is ready for metamorphosis.
     */
    private void checkMetamorphosisReadiness(@NotNull LivingEntity entity, int amplifier) {
        float derivationProgress = getDerivationProgress(entity);
        if (derivationProgress >= MAX_DERIVATION_PROGRESS && amplifier >= 2) {
            // Metamorphosis ready - trigger transformation logic
            // In full implementation, this would:
            // 1. Save current state
            // 2. Transform to new form
            // 3. Reset derivation progress
            // 4. Grant bonus effects
            
            if (entity.tickCount % 20 == 0) {
                // Visual feedback for metamorphosis readiness
                // Actual transformation handled by entity code
            }
        }
    }
    
    /**
     * Applies derivation-based bonuses.
     */
    private void applyDerivationBonuses(@NotNull LivingEntity entity, int amplifier) {
        // Derivation provides stacking bonuses based on progress
        float derivationProgress = getDerivationProgress(entity);
        float progressBonus = derivationProgress * amplifier;
        
        // Progressive health regeneration
        if (progressBonus >= 0.5f && entity.tickCount % 40 == 0) {
            entity.heal(1.0f);
        }
        
        // At full derivation, grant temporary buffs
        if (derivationProgress >= MAX_DERIVATION_PROGRESS) {
            // Full derivation bonuses
            // Would grant special abilities in complete implementation
        }
    }
    
    /**
     * Resets derivation progress for a specific entity (called after metamorphosis).
     */
    public static void resetDerivationProgress(LivingEntity entity) {
        setDerivationProgress(entity, 0.0f);
    }
    
    /**
     * Returns current derivation progress for a specific entity.
     * Progress is stored per-entity via capability to avoid singleton state.
     * 
     * @param entity The entity to query
     * @return Progress value from 0.0 to 1.0
     */
    public static float getDerivationProgress(LivingEntity entity) {
        return entity.getCapability(ParasiteCapability.CAPABILITY)
            .map(cap -> {
                CompoundTag extraData = cap.getExtraData();
                return extraData.contains(NBT_KEY_DERIVATION) ? extraData.getFloat(NBT_KEY_DERIVATION) : 0.0f;
            })
            .orElse(0.0f);
    }
    
    /**
     * Sets derivation progress for a specific entity.
     */
    private static void setDerivationProgress(LivingEntity entity, float progress) {
        entity.getCapability(ParasiteCapability.CAPABILITY).ifPresent(cap -> {
            CompoundTag extraData = cap.getExtraData();
            extraData.putFloat(NBT_KEY_DERIVATION, progress);
            cap.markDirty();
        });
    }
    
    /**
     * Spawns derivation particles (blue/purple transformation aura).
     */
    private void spawnDerivationParticles(@NotNull LivingEntity entity, int amplifier) {
        double x = entity.getX() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        double y = entity.getY() + entity.level().random.nextDouble() * entity.getBbHeight();
        double z = entity.getZ() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        
        // Base derivation particles
        entity.level().addParticle(
            net.minecraft.core.particles.ParticleTypes.DRAGON_BREATH,
            x, y, z,
            0.0, 0.025, 0.0
        );
        
        // Extra particles at higher amplifier
        if (amplifier >= 1) {
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.SCULK_SOUL,
                x, y + 0.4, z,
                (entity.level().random.nextDouble() - 0.5) * 0.04,
                0.04,
                (entity.level().random.nextDouble() - 0.5) * 0.04
            );
        }
        
        if (amplifier >= 2) {
            // Transformation vortex at high derivation
            for (int i = 0; i < 4; i++) {
                double angle = (entity.tickCount + i * 1.8) * 0.1;
                double radius = entity.getBbWidth() / 2.0 + 0.3;
                double px = entity.getX() + Math.cos(angle) * radius;
                double pz = entity.getZ() + Math.sin(angle) * radius;
                
                entity.level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.PORTAL,
                    px, y + 0.5, pz,
                    0.0, 0.02, 0.0
                );
            }
        }
        
        // Special particles when near metamorphosis
        float derivationProgress = getDerivationProgress(entity);
        if (derivationProgress >= 0.8f) {
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.TOTEM_OF_UNDYING,
                x, y + 0.8, z,
                0.0, 0.05, 0.0
            );
        }
    }
    
    /**
     * Derivation is specifically for parasite entities.
     */
    @Override
    public boolean isParasiteOnly() {
        return true;
    }
    
    /**
     * Determines if derivation should tick this frame.
     */
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % DERIVATION_TICK_INTERVAL == 0;
    }
}
