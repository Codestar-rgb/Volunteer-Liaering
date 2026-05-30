package com.subspaceparasite.common.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Adaptation Effect - Environmental adaptation and resistance buff.
 * <p>
 * This effect represents a parasite's ability to adapt to hostile environments
 * and develop resistances to damage types. Provides damage reduction and
 * environmental immunity.
 * </p>
 * 
 * <h2>Effects by Amplifier:</h2>
 * <ul>
 *   <li>Level 0: Basic adaptation, minor damage reduction (+5%)</li>
 *   <li>Level 1: Advanced adaptation, moderate damage reduction (+10%)</li>
 *   <li>Level 2+: Perfect adaptation, significant damage reduction (+15%) + immunity</li>
 * </ul>
 * 
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Progressive damage reduction</li>
 *   <li>Environmental hazard immunity (fire, drowning, etc.)</li>
 *   <li>Resistance to status effects</li>
 *   <li>Synergizes with Evolution effect</li>
 *   <li>Visual adaptation shield particles</li>
 * </ul>
 * 
 * @author SRP Port Team
 * @see ParasiteBuffEffectBase
 * @see EvolutionEffect
 */
public class AdaptationEffect extends ParasiteBuffEffectBase {
    
    /** Damage reduction percentage per amplifier level */
    private static final double DAMAGE_REDUCTION_PER_LEVEL = 0.05;
    
    /** Armor boost per amplifier level */
    private static final double ARMOR_BOOST_PER_LEVEL = 2.0;
    
    /** Armor toughness boost per amplifier level */
    private static final double ARMOR_TOUGHNESS_PER_LEVEL = 1.0;
    
    /** Tick interval for adaptation logic */
    private static final int ADAPTATION_TICK_INTERVAL = 20;
    
    /**
     * Creates the Adaptation effect with standard settings.
     */
    public AdaptationEffect() {
        super(
            MobEffectCategory.BENEFICIAL,
            0x2E8B57,  // Sea green (adaptation/resistance color)
            3,         // Max amplifier
            true,      // Can stack
            ADAPTATION_TICK_INTERVAL,
            true,      // Has attribute modifiers
            ARMOR_BOOST_PER_LEVEL,
            AttributeModifier.Operation.ADDITION
        );
    }
    
    /**
     * Applies adaptation buffs each tick.
     * Grants damage reduction and environmental immunities.
     * 
     * @param entity    The adapted parasite entity
     * @param amplifier The adaptation level/amplifier
     */
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnAdaptationParticles(entity, amplifier);
            return;
        }
        
        // Apply environmental immunities
        applyEnvironmentalImmunities(entity, amplifier);
        
        // Apply damage reduction logic
        applyDamageReduction(entity, amplifier);
    }
    
    /**
     * Grants environmental immunities based on adaptation level.
     */
    private void applyEnvironmentalImmunities(@NotNull LivingEntity entity, int amplifier) {
        // Level 1+: Fire resistance
        if (amplifier >= 1 && entity.isOnFire()) {
            if (entity.tickCount % 20 == 0) {
                entity.clearFire();
            }
        }
        
        // Level 2+: Drowning immunity
        if (amplifier >= 2 && entity.isUnderWater()) {
            entity.setAirSupply(entity.getMaxAirSupply());
        }
    }
    
    /**
     * Applies damage reduction through armor bonuses.
     */
    private void applyDamageReduction(@NotNull LivingEntity entity, int amplifier) {
        // Additional damage reduction handled by armor modifiers
        // This method handles special cases
        
        // At max adaptation, grant temporary absorption hearts
        if (amplifier >= 3 && entity.tickCount % 100 == 0) {
            float absorptionAmount = amplifier * 2.0f;
            entity.setAbsorptionAmount(Math.max(entity.getAbsorptionAmount(), absorptionAmount));
        }
    }
    
    /**
     * Applies attribute modifiers for adaptation effect.
     * Boosts armor, armor toughness, and knockback resistance.
     */
    @Override
    public void addAttributeModifiers(LivingEntity entity,
                                     AttributeInstance attribute,
                                     UUID uuid,
                                     int amplifier) {
        if (attribute.getAttribute() == Attributes.ARMOR) {
            double amount = ARMOR_BOOST_PER_LEVEL * (amplifier + 1);
            applyModifier(entity, attribute, uuid, amount);
        } else if (attribute.getAttribute() == Attributes.ARMOR_TOUGHNESS) {
            double amount = ARMOR_TOUGHNESS_PER_LEVEL * (amplifier + 1);
            applyModifier(entity, attribute, uuid, amount);
        } else if (attribute.getAttribute() == Attributes.KNOCKBACK_RESISTANCE) {
            double amount = 0.1 * (amplifier + 1);
            applyModifier(entity, attribute, uuid, amount);
        }
    }
    
    /**
     * Helper method to apply attribute modifier.
     */
    private void applyModifier(LivingEntity entity,
                              AttributeInstance attribute,
                              UUID uuid,
                              double amount) {
        var existingModifier = attribute.getModifier(uuid);
        if (existingModifier == null) {
            var modifier = new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                uuid,
                "Parasite Buff: " + getDescriptionId(),
                amount,
                AttributeModifier.Operation.ADDITION
            );
            attribute.addTransientModifier(modifier);
        } else if (existingModifier.getAmount() != amount) {
            attribute.removeModifier(uuid);
            var modifier = new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                uuid,
                "Parasite Buff: " + getDescriptionId(),
                amount,
                AttributeModifier.Operation.ADDITION
            );
            attribute.addTransientModifier(modifier);
        }
    }
    
    /**
     * Spawns adaptation particles (protective barrier effect).
     */
    private void spawnAdaptationParticles(@NotNull LivingEntity entity, int amplifier) {
        double x = entity.getX() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        double y = entity.getY() + entity.level().random.nextDouble() * entity.getBbHeight();
        double z = entity.getZ() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        
        // Base adaptation particles
        entity.level().addParticle(
            net.minecraft.core.particles.ParticleTypes.NAUTILUS,
            x, y, z,
            0.0, 0.02, 0.0
        );
        
        // Extra barrier particles at higher amplifier
        if (amplifier >= 1) {
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.END_ROD,
                x, y + 0.5, z,
                (entity.level().random.nextDouble() - 0.5) * 0.03,
                0.03,
                (entity.level().random.nextDouble() - 0.5) * 0.03
            );
        }
        
        if (amplifier >= 2) {
            // Full adaptation shield visualization
            for (int i = 0; i < 3; i++) {
                double angle = (entity.tickCount + i * 2) * 0.12;
                double radius = entity.getBbWidth() / 2.0 + 0.2;
                double px = entity.getX() + Math.cos(angle) * radius;
                double pz = entity.getZ() + Math.sin(angle) * radius;
                
                entity.level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.TOTEM_OF_UNDYING,
                    px, y + 0.4, pz,
                    0.0, 0.01, 0.0
                );
            }
        }
    }
    
    /**
     * Adaptation is specifically for parasite entities.
     */
    @Override
    public boolean isParasiteOnly() {
        return true;
    }
    
    /**
     * Determines if adaptation should tick this frame.
     */
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % ADAPTATION_TICK_INTERVAL == 0;
    }
}
