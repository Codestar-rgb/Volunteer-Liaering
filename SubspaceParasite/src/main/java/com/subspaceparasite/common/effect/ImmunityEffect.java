package com.subspaceparasite.common.effect;

import com.subspaceparasite.common.capability.ParasiteCapability;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Immunity Effect - Complete protection against parasite infection.
 * <p>
 * This is the ultimate defensive effect against SRP parasites. When active,
 * the entity becomes completely immune to all forms of infection, COTH effects,
 * and parasite-related debuffs. Essential for high-level parasite combat.
 * </p>
 * 
 * <h2>Effects by Amplifier:</h2>
 * <ul>
 *   <li>Level 0: Basic immunity to infection, standard duration</li>
 *   <li>Level 1: Enhanced immunity with resistance boost</li>
 *   <li>Level 2+: Perfect immunity with cleansing aura</li>
 * </ul>
 * 
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Complete infection immunity via capability system</li>
 *   <li>Blocks all COTH and infection effect applications</li>
 *   <li>Gradual resistance increase during effect</li>
 *   <li>Cleansing aura at high amplifier levels</li>
 *   <li>Visual shield particles</li>
 * </ul>
 * 
 * @author SRP Port Team
 * @see ParasiteCapability
 */
public class ImmunityEffect extends BaseSRPEffect {
    
    /** Resistance boost per tick at amplifier 0 */
    private static final float BASE_RESISTANCE_BOOST = 0.01f;
    
    /** Additional resistance per amplifier level */
    private static final float RESISTANCE_PER_LEVEL = 0.005f;
    
    /** Maximum resistance cap while effect is active */
    private static final float MAX_RESISTANCE_CAP = 1.0f;
    
    /** Cleansing radius for high-level immunity (in blocks) */
    private static final double CLEANSING_RADIUS = 3.0;
    
    /** Tick interval for resistance application */
    private static final int RESISTANCE_TICK_INTERVAL = 10;
    
    /**
     * Creates the Immunity effect with standard settings.
     */
    public ImmunityEffect() {
        super(
            MobEffectCategory.BENEFICIAL,
            0x00FFFF,  // Cyan color (pure protection)
            2,         // Max amplifier
            true,      // Can stack
            RESISTANCE_TICK_INTERVAL
        );
    }
    
    /**
     * Applies immunity logic each tick.
     * Sets complete immunity and boosts resistance.
     * 
     * @param entity    The protected entity
     * @param amplifier The effect amplifier
     */
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnImmunityParticles(entity, amplifier);
            return;
        }
        
        // Set complete immunity state
        setCompleteImmunity(entity, amplifier);
        
        // Apply resistance boost over time
        boostResistance(entity, amplifier);
        
        // Cleansing aura at high levels
        if (amplifier >= 2) {
            applyCleansingAura(entity);
        }
    }
    
    /**
     * Sets the entity to complete immunity state.
     */
    private void setCompleteImmunity(@NotNull LivingEntity entity, int amplifier) {
        entity.getCapability(ParasiteCapability.CAPABILITY).ifPresent(capability -> {
            // Set permanent immunity flag for duration of effect
            capability.setImmune(true);
            
            // Ensure cooldown is active to block any edge cases
            int remainingDuration = entity.getEffect(this).getDuration();
            capability.setInfectionCooldown(Math.max(capability.getInfectionCooldown(), remainingDuration));
        });
    }
    
    /**
     * Gradually increases infection resistance during the effect.
     */
    private void boostResistance(@NotNull LivingEntity entity, int amplifier) {
        entity.getCapability(ParasiteCapability.CAPABILITY).ifPresent(capability -> {
            float currentResistance = capability.getInfectionResistance();
            float boostAmount = BASE_RESISTANCE_BOOST + (amplifier * RESISTANCE_PER_LEVEL);
            float newResistance = Math.min(MAX_RESISTANCE_CAP, currentResistance + boostAmount);
            capability.setInfectionResistance(newResistance);
        });
    }
    
    /**
     * Applies cleansing aura to nearby entities at amplifier 2+.
     * Reduces infection levels of allies in range.
     */
    private void applyCleansingAura(@NotNull LivingEntity entity) {
        entity.level().getEntitiesOfClass(
            LivingEntity.class,
            entity.getBoundingBox().inflate(CLEANSING_RADIUS)
        ).forEach(nearbyEntity -> {
            if (nearbyEntity != entity && !nearbyEntity.isEnemy(entity)) {
                // Allies get infection reduction
                nearbyEntity.getCapability(ParasiteCapability.CAPABILITY).ifPresent(cap -> {
                    if (cap.getInfectionLevel() > 0) {
                        cap.reduceInfection(1);
                    }
                });
                
                // Spawn cleansing particles on allies
                if (entity.level().isClientSide()) {
                    spawnAllyCleansingParticles(nearbyEntity);
                }
            }
        });
    }
    
    /**
     * Called when the effect is removed.
     * Resets immunity state but maintains resistance gains.
     */
    @Override
    public void removeAttributeModifiers(LivingEntity entity,
                                        net.minecraft.world.entity.ai.attributes.AttributeInstance attribute,
                                        java.util.UUID uuid) {
        if (!entity.level().isClientSide()) {
            entity.getCapability(ParasiteCapability.CAPABILITY).ifPresent(capability -> {
                // Remove immunity flag but keep resistance
                capability.setImmune(false);
                
                // Gradually reduce resistance over time (not instant removal)
                float currentResistance = capability.getInfectionResistance();
                if (currentResistance > 0.5f) {
                    capability.setInfectionResistance(0.5f);
                }
            });
        }
    }
    
    /**
     * Spawns immunity shield particles (cyan barrier effect).
     */
    private void spawnImmunityParticles(@NotNull LivingEntity entity, int amplifier) {
        double x = entity.getX() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        double y = entity.getY() + entity.level().random.nextDouble() * entity.getBbHeight();
        double z = entity.getZ() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        
        // Base shield particles
        entity.level().addParticle(
            net.minecraft.core.particles.ParticleTypes.END_ROD,
            x, y, z,
            0.0, 0.02, 0.0
        );
        
        // Extra barrier effect at higher amplifier
        if (amplifier >= 1) {
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER,
                x, y + 0.5, z,
                (entity.level().random.nextDouble() - 0.5) * 0.05,
                0.05,
                (entity.level().random.nextDouble() - 0.5) * 0.05
            );
        }
        
        // Full shield visualization at max amplifier
        if (amplifier >= 2) {
            for (int i = 0; i < 3; i++) {
                double angle = (entity.tickCount + i * 2) * 0.1;
                double radius = entity.getBbWidth() / 2.0 + 0.2;
                double px = entity.getX() + Math.cos(angle) * radius;
                double pz = entity.getZ() + Math.sin(angle) * radius;
                
                entity.level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.NAUTILUS,
                    px, y + 0.5, pz,
                    0.0, 0.0, 0.0
                );
            }
        }
    }
    
    /**
     * Spawns cleansing particles on allied entities.
     */
    private void spawnAllyCleansingParticles(@NotNull LivingEntity ally) {
        double x = ally.getX() + (ally.level().random.nextDouble() - 0.5) * ally.getBbWidth();
        double y = ally.getY() + ally.level().random.nextDouble() * ally.getBbHeight();
        double z = ally.getZ() + (ally.level().random.nextDouble() - 0.5) * ally.getBbWidth();
        
        ally.level().addParticle(
            net.minecraft.core.particles.ParticleTypes.ENCHANTED_HIT,
            x, y, z,
            0.0, 0.03, 0.0
        );
    }
    
    /**
     * Determines if immunity should tick this frame.
     */
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % RESISTANCE_TICK_INTERVAL == 0;
    }
    
    /**
     * Immunity can be applied to any living entity.
     */
    @Override
    public boolean canApply(@NotNull LivingEntity entity) {
        return entity.isAlive();
    }
}
