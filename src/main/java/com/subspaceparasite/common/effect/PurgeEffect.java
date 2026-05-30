package com.subspaceparasite.common.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Purge effect - Cleanses infection and provides temporary immunity.
 * <p>
 * This is the primary counter-effect to COTH and other infection effects.
 * When applied, it rapidly reduces infection level and grants temporary
 * immunity to further infection attempts. Essential for survival against
 * parasite threats.
 * </p>
 * 
 * <h2>Effects by Amplifier:</h2>
 * <ul>
 *   <li>Level 0: Slow infection reduction, short immunity</li>
 *   <li>Level 1: Moderate infection reduction, medium immunity</li>
 *   <li>Level 2+: Rapid infection reduction, long immunity + cleansing</li>
 * </ul>
 * 
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Infection level reduction through capability system</li>
 *   <li>Temporary immunity buff application</li>
 *   <li>Cleanses other negative parasite effects</li>
 *   <li>Visual purification particles</li>
 * </ul>
 * 
 * @author SRP Port Team
 * @see com.subspaceparasite.common.capability.ParasiteCapability
 */
public class PurgeEffect extends BaseSRPEffect {
    
    /** Infection reduction per tick at amplifier 0 */
    private static final int BASE_INFECTION_REDUCTION = 1;
    
    /** Additional reduction per amplifier level */
    private static final int REDUCTION_PER_LEVEL = 2;
    
    /** Immunity duration granted after full cleanse (in ticks) */
    private static final int BASE_IMMUNITY_DURATION = 600; // 30 seconds
    
    /** Tick interval for infection reduction */
    private static final int PURGE_TICK_INTERVAL = 20; // Every second
    
    /**
     * Creates the Purge effect with standard settings.
     */
    public PurgeEffect() {
        super(
            MobEffectCategory.BENEFICIAL,
            0xF0F8FF,  // Alice blue (pure/clean color)
            2,         // Max amplifier
            true,      // Can stack
            PURGE_TICK_INTERVAL
        );
    }
    
    /**
     * Applies purge cleansing each tick.
     * Reduces infection and removes negative effects.
     * 
     * @param entity    The entity being purified
     * @param amplifier The effect amplifier
     */
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnPurificationParticles(entity, amplifier);
            return;
        }
        
        // Reduce infection level
        reduceInfection(entity, amplifier);
        
        // Cleanse negative parasite effects
        cleanseNegativeEffects(entity, amplifier);
    }
    
    /**
     * Reduces infection level through the capability system.
     */
    private void reduceInfection(@NotNull LivingEntity entity, int amplifier) {
        entity.getCapability(com.subspaceparasite.common.capability.ParasiteCapability.CAPABILITY)
            .ifPresent(capability -> {
                int reduction = BASE_INFECTION_REDUCTION + (amplifier * REDUCTION_PER_LEVEL);
                capability.reduceInfection(reduction);
                
                // If fully cleansed, grant bonus immunity
                if (capability.getInfectionLevel() <= 0) {
                    grantBonusImmunity(entity, amplifier);
                }
            });
    }
    
    /**
     * Removes negative parasite-related effects from the entity.
     * Higher amplifiers cleanse more effect types.
     */
    private void cleanseNegativeEffects(@NotNull LivingEntity entity, int amplifier) {
        // Always remove COTH
        entity.removeEffect(net.minecraft.world.effect.MobEffects.WITHER);
        
        if (amplifier >= 1) {
            // Remove additional debuffs
            entity.removeEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN);
            entity.removeEffect(net.minecraft.world.effect.MobEffects.WEAKNESS);
        }
        
        if (amplifier >= 2) {
            // Full cleanse - remove all harmful effects
            entity.getActiveEffects().removeIf(effectInstance -> 
                effectInstance.getEffect().getCategory() == MobEffectCategory.HARMFUL
            );
        }
    }
    
    /**
     * Grants bonus immunity period after complete infection cleanse.
     */
    private void grantBonusImmunity(@NotNull LivingEntity entity, int amplifier) {
        entity.getCapability(com.subspaceparasite.common.capability.ParasiteCapability.CAPABILITY)
            .ifPresent(capability -> {
                int immunityDuration = BASE_IMMUNITY_DURATION + (amplifier * 300); // +15 sec per level
                capability.setInfectionCooldown(immunityDuration);
                capability.setImmune(true);
                
                // Schedule immunity removal
                entity.level().schedule(() -> {
                    capability.setImmune(false);
                }, immunityDuration);
            });
    }
    
    /**
     * Spawns purification particles (white/blue sparkles).
     */
    private void spawnPurificationParticles(@NotNull LivingEntity entity, int amplifier) {
        double x = entity.getX() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        double y = entity.getY() + entity.level().random.nextDouble() * entity.getBbHeight();
        double z = entity.getZ() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        
        // Base purification particles
        entity.level().addParticle(
            net.minecraft.core.particles.ParticleTypes.ENCHANTED_HIT,
            x, y, z,
            0.0, 0.05, 0.0
        );
        
        // Extra particles at higher amplifier
        if (amplifier >= 1) {
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.SPELL,
                x, y + 0.5, z,
                0.0, 0.08, 0.0
            );
        }
        
        if (amplifier >= 2) {
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.TOTEM_OF_UNDYING,
                x, y + 0.3, z,
                (entity.level().random.nextDouble() - 0.5) * 0.1,
                0.1,
                (entity.level().random.nextDouble() - 0.5) * 0.1
            );
        }
    }
    
    /**
     * Determines if purge should tick this frame.
     */
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % PURGE_TICK_INTERVAL == 0;
    }
    
    /**
     * Purge can be applied to any living entity.
     */
    @Override
    public boolean canApply(@NotNull LivingEntity entity) {
        return entity.isAlive();
    }
}
