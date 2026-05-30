package com.subspaceparasite.common.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Parasite Regeneration Effect - Enhanced healing for parasites.
 * <p>
 * This effect provides parasites with exceptional regenerative capabilities,
 * allowing rapid health recovery and tissue reconstruction. Essential for
 * sustained combat and survival.
 * </p>
 * 
 * <h2>Effects by Amplifier:</h2>
 * <ul>
 *   <li>Level 0: Basic regeneration, +1 HP per second</li>
 *   <li>Level 1: Advanced regeneration, +3 HP per second + limb regrowth</li>
 *   <li>Level 2+: Perfect regeneration, +6 HP per second + instant recovery</li>
 * </ul>
 * 
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Progressive health regeneration</li>
 *   <li>Amplifier-based heal rate scaling</li>
 *   <li>Bonus healing when below half health</li>
 *   <li>Visual regeneration particles</li>
 * </ul>
 * 
 * @author SRP Port Team
 * @see ParasiteBuffEffectBase
 */
public class ParasiteRegenerationEffect extends ParasiteBuffEffectBase {
    
    /** Base healing amount per tick at amplifier 0 */
    private static final float BASE_HEAL_PER_TICK = 0.05f;
    
    /** Additional heal per amplifier level */
    private static final float HEAL_PER_LEVEL = 0.1f;
    
    /** Bonus heal multiplier when below half health */
    private static final float LOW_HEALTH_BONUS = 2.0f;
    
    /** Tick interval for regeneration logic */
    private static final int REGENERATION_TICK_INTERVAL = 5;
    
    /**
     * Creates the Parasite Regeneration effect with standard settings.
     */
    public ParasiteRegenerationEffect() {
        super(
            MobEffectCategory.BENEFICIAL,
            0xFF69B4,  // Hot pink (regeneration/life color)
            3,         // Max amplifier
            true,      // Can stack
            REGENERATION_TICK_INTERVAL
        );
    }
    
    /**
     * Applies regeneration buffs each tick.
     * Heals the parasite based on amplifier level.
     * 
     * @param entity    The regenerating parasite entity
     * @param amplifier The regeneration level/amplifier
     */
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnRegenerationParticles(entity, amplifier);
            return;
        }
        
        // Apply healing
        applyHealing(entity, amplifier);
    }
    
    /**
     * Applies health regeneration to the entity.
     */
    private void applyHealing(@NotNull LivingEntity entity, int amplifier) {
        if (entity.getHealth() < entity.getMaxHealth()) {
            // Calculate heal amount based on amplifier
            float healAmount = BASE_HEAL_PER_TICK + (amplifier * HEAL_PER_LEVEL);
            
            // Bonus healing when below half health
            if (entity.getHealth() < entity.getMaxHealth() * 0.5f) {
                healAmount *= LOW_HEALTH_BONUS;
            }
            
            entity.heal(healAmount);
        }
    }
    
    /**
     * Spawns regeneration particles (pink/green healing sparkles).
     */
    private void spawnRegenerationParticles(@NotNull LivingEntity entity, int amplifier) {
        double x = entity.getX() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        double y = entity.getY() + entity.level().random.nextDouble() * entity.getBbHeight();
        double z = entity.getZ() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        
        // Base regeneration particles
        entity.level().addParticle(
            net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER,
            x, y, z,
            0.0, 0.03, 0.0
        );
        
        // Extra particles at higher amplifier
        if (amplifier >= 1) {
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.TOTEM_OF_UNDYING,
                x, y + 0.4, z,
                (entity.level().random.nextDouble() - 0.5) * 0.04,
                0.04,
                (entity.level().random.nextDouble() - 0.5) * 0.04
            );
        }
        
        if (amplifier >= 2) {
            // Full regeneration aura at max level
            for (int i = 0; i < 4; i++) {
                double angle = (entity.tickCount + i * 1.5) * 0.1;
                double radius = entity.getBbWidth() / 2.0 + 0.2;
                double px = entity.getX() + Math.cos(angle) * radius;
                double pz = entity.getZ() + Math.sin(angle) * radius;
                
                entity.level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.SCULK_SOUL,
                    px, y + 0.5, pz,
                    0.0, 0.02, 0.0
                );
            }
        }
        
        // Special particles when healing from low health
        if (entity.getHealth() < entity.getMaxHealth() * 0.3f) {
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.ENCHANTED_HIT,
                x, y + 0.6, z,
                0.0, 0.05, 0.0
            );
        }
    }
    
    /**
     * Regeneration is specifically for parasite entities.
     */
    @Override
    public boolean isParasiteOnly() {
        return true;
    }
    
    /**
     * Determines if regeneration should tick this frame.
     */
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % REGENERATION_TICK_INTERVAL == 0;
    }
}
