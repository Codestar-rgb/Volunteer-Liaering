package com.subspaceparasite.common.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Infection Stage Effect - Represents advanced infection stages (INFECTION_II, INFECTION_III).
 * <p>
 * These effects represent progressed states of parasite infection that occur after
 * initial COTH exposure. Higher stages cause faster infection spread and more severe
 * debuffs, eventually leading to entity conversion.
 * </p>
 * 
 * <h2>Effects by Amplifier:</h2>
 * <ul>
 *   <li>Level 0 (Stage I): Moderate infection rate, minor debuffs</li>
 *   <li>Level 1 (Stage II): High infection rate, moderate debuffs + armor reduction</li>
 *   <li>Level 2+ (Stage III): Extreme infection rate, severe debuffs + conversion imminent</li>
 * </ul>
 * 
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Accelerated infection progression compared to base COTH</li>
 *   <li>Stacks with COTH for compounded effect</li>
 *   <li>Applies additional debuffs based on stage</li>
 *   <li>Visual indicators showing infection severity</li>
 * </ul>
 * 
 * @author SRP Port Team
 * @see InfectionEffectBase
 * @see CothEffect
 */
public class InfectionStageEffect extends InfectionEffectBase {
    
    /** Base damage per tick at stage I */
    private static final float BASE_DAMAGE = 0.5f;
    
    /** Damage increase per amplifier */
    private static final float DAMAGE_PER_LEVEL = 0.5f;
    
    /** Movement speed reduction per amplifier level */
    private static final float SLOWNESS_PER_LEVEL = 0.05f;
    
    /** Tick interval for damage application */
    private static final int DAMAGE_TICK_INTERVAL = 20;
    
    /** The stage name for display purposes */
    private final String stageName;
    
    /**
     * Creates a new infection stage effect.
     * 
     * @param stageNumber The stage number (2 for INFECTION_II, 3 for INFECTION_III)
     */
    public InfectionStageEffect(int stageNumber) {
        super(
            MobEffectCategory.HARMFUL,
            getStageColor(stageNumber),
            calculateInfectionRate(stageNumber),
            0.04f,  // Infection rate per level
            true    // Can trigger conversion
        );
        this.stageName = "Stage " + stageNumber;
    }
    
    /**
     * Gets the color for the given stage number.
     * 
     * @param stageNumber The stage number
     * @return RGB packed color
     */
    private static int getStageColor(int stageNumber) {
        return switch (stageNumber) {
            case 2 -> 0x6B1A1A;  // Dark red for Stage II
            case 3 -> 0x8B2626;  // Brighter red for Stage III
            default -> 0x5A1515; // Default dark red
        };
    }
    
    /**
     * Calculates infection rate based on stage number.
     * Higher stages have faster infection rates.
     * 
     * @param stageNumber The stage number
     * @return Base infection rate
     */
    private static float calculateInfectionRate(int stageNumber) {
        return switch (stageNumber) {
            case 2 -> 0.15f;  // Stage II: 3x base COTH rate
            case 3 -> 0.25f;  // Stage III: 5x base COTH rate
            default -> 0.10f;
        };
    }
    
    /**
     * Applies the infection stage effect each tick.
     * Deals damage and applies debuffs based on stage severity.
     * 
     * @param entity    The infected entity
     * @param amplifier The effect amplifier
     */
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnStageParticles(entity, amplifier);
            return;
        }
        
        // Apply infection progression
        applyInfection(entity, amplifier);
        
        // Apply damage over time
        applyDamageOverTime(entity, amplifier);
        
        // Apply stage-specific debuffs
        applyStageDebuffs(entity, amplifier);
    }
    
    /**
     * Applies damage over time based on stage severity.
     * 
     * @param entity    The target entity
     * @param amplifier The effect amplifier
     */
    private void applyDamageOverTime(@NotNull LivingEntity entity, int amplifier) {
        float damage = BASE_DAMAGE + (amplifier * DAMAGE_PER_LEVEL);
        
        if (damage > 0) {
            entity.hurt(entity.damageSources().magic(), damage);
        }
    }
    
    /**
     * Applies stage-specific debuffs to the entity.
     * Higher stages apply more severe debuffs.
     * 
     * @param entity    The target entity
     * @param amplifier The effect amplifier
     */
    private void applyStageDebuffs(@NotNull LivingEntity entity, int amplifier) {
        // Apply slowness using proper 1.20.1 API
        applySlownessEffect(entity, amplifier);
        
        // Stage III applies additional weakness
        if (amplifier >= 2) {
            // Apply Weakness effect
            net.minecraft.world.effect.MobEffectInstance currentWeakness = 
                entity.getEffect(net.minecraft.world.effect.MobEffects.WEAKNESS);
            
            if (currentWeakness == null || currentWeakness.getAmplifier() < amplifier) {
                entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.WEAKNESS,
                    DAMAGE_TICK_INTERVAL * 2,
                    Math.min(3, amplifier),
                    false,
                    false,
                    true
                ));
            }
        }
    }
    
    /**
     * Applies slowness effect using the correct 1.20.1 API.
     */
    private void applySlownessEffect(@NotNull LivingEntity entity, int amplifier) {
        float slownessAmount = SLOWNESS_PER_LEVEL * amplifier;
        int effectLevel = Math.min(5, (int)(slownessAmount * 10));
        
        net.minecraft.world.effect.MobEffectInstance currentSlowness = 
            entity.getEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN);
        
        if (currentSlowness == null || currentSlowness.getAmplifier() < effectLevel) {
            entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN,
                DAMAGE_TICK_INTERVAL * 2,
                effectLevel,
                false,
                false,
                true
            ));
        }
    }
    
    /**
     * Spawns particles indicating infection stage severity.
     * 
     * @param entity    The infected entity
     * @param amplifier The effect amplifier
     */
    private void spawnStageParticles(@NotNull LivingEntity entity, int amplifier) {
        double x = entity.getX() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        double y = entity.getY() + entity.level().random.nextDouble() * entity.getBbHeight();
        double z = entity.getZ() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        
        // Base infection particles
        entity.level().addParticle(
            net.minecraft.core.particles.ParticleTypes.REDSTONE,
            x, y, z,
            0.0, 0.0, 0.0
        );
        
        // More intense particles at higher stages
        if (amplifier >= 1) {
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.SCULK_SOUL,
                x, y + 0.5, z,
                0.0, 0.03, 0.0
            );
        }
        
        if (amplifier >= 2) {
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.SOUL_FIRE_FLAME,
                x, y + 0.3, z,
                (entity.level().random.nextDouble() - 0.5) * 0.1,
                0.05,
                (entity.level().random.nextDouble() - 0.5) * 0.1
            );
        }
    }
    
    /**
     * Determines if the effect should tick this frame.
     * Uses longer interval for performance optimization.
     * 
     * @param duration  Remaining duration
     * @param amplifier Effect amplifier
     * @return true if should tick
     */
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % DAMAGE_TICK_INTERVAL == 0;
    }
    
    /**
     * Called when an entity reaches full infection from this stage.
     * Triggers immediate conversion checks.
     * 
     * @param entity    The fully infected entity
     * @param amplifier The effect amplifier
     */
    @Override
    protected void onFullInfection(@NotNull LivingEntity entity, int amplifier) {
        super.onFullInfection(entity, amplifier);
        
        // Stage III causes near-instant conversion
        if (amplifier >= 2) {
            // Additional conversion logic can be triggered here
            // Actual conversion handled by InfectionComponent
        }
    }
}
