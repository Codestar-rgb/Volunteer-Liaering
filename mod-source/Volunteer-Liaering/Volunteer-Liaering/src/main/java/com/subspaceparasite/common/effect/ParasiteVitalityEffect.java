package com.subspaceparasite.common.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Parasite Vitality effect - Exclusive buff for parasite entities.
 * <p>
 * Provides significant health regeneration, damage resistance, and
 * increased maximum health to parasite entities. This effect represents
 * the enhanced biological functions gained through parasite evolution.
 * </p>
 * 
 * <h2>Buffs by Amplifier:</h2>
 * <ul>
 *   <li>Level 0: +2 Health/sec regen, +10% max health</li>
 *   <li>Level 1: +4 Health/sec regen, +20% max health, +5% damage resist</li>
 *   <li>Level 2+: +8 Health/sec regen, +40% max health, +10% damage resist</li>
 * </ul>
 * 
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Stacking health regeneration</li>
 *   <li>Percentage-based max health increase</li>
 *   <li>Damage resistance attribute modification</li>
 *   <li>Parasite-only application (configurable)</li>
 * </ul>
 * 
 * @author SRP Port Team
 */
public class ParasiteVitalityEffect extends BaseSRPEffect {
    
    /** Unique UUID for health modifier */
    private static final UUID HEALTH_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    
    /** Unique UUID for resistance modifier */
    private static final UUID RESISTANCE_UUID = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901");
    
    /** Base health regeneration per second */
    private static final float BASE_REGEN = 0.1f; // 2 HP per second (20 ticks)
    
    /** Regen increase per amplifier */
    private static final float REGEN_PER_LEVEL = 0.1f;
    
    /** Base max health percentage increase */
    private static final float BASE_HEALTH_PERCENT = 0.10f; // +10%
    
    /** Health percent increase per amplifier */
    private static final float HEALTH_PERCENT_PER_LEVEL = 0.10f;
    
    /** Tick interval for regeneration */
    private static final int REGEN_TICK_INTERVAL = 1;
    
    /**
     * Creates the Parasite Vitality effect with standard settings.
     */
    public ParasiteVitalityEffect() {
        super(
            MobEffectCategory.BENEFICIAL,
            0xFF6347,  // Tomato red (vitality color)
            3,         // Max amplifier
            true,      // Can stack
            REGEN_TICK_INTERVAL
        );
    }
    
    /**
     * Applies vitality buffs each tick.
     * Primarily handles health regeneration.
     * 
     * @param entity    The parasite entity
     * @param amplifier The effect amplifier
     */
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnVitalityParticles(entity, amplifier);
            return;
        }
        
        // Apply health regeneration
        applyRegeneration(entity, amplifier);
    }
    
    /**
     * Applies health regeneration based on amplifier level.
     */
    private void applyRegeneration(@NotNull LivingEntity entity, int amplifier) {
        // Calculate regen amount
        float regenAmount = BASE_REGEN + (amplifier * REGEN_PER_LEVEL);
        
        // Only heal if not at full health
        if (entity.getHealth() < entity.getMaxHealth()) {
            entity.heal(regenAmount);
        }
    }
    
    /**
     * Adds attribute modifiers when effect is applied.
     * Increases max health and damage resistance.
     */
    @Override
    public void addAttributeModifiers(LivingEntity entity,
                                     AttributeMap attributeMap,
                                     int amplifier) {
        // Add max health modifier
        AttributeModifier healthMod = new AttributeModifier(
            HEALTH_UUID,
            "Parasite Vitality Health Boost",
            BASE_HEALTH_PERCENT + (amplifier * HEALTH_PERCENT_PER_LEVEL),
            AttributeModifier.Operation.MULTIPLY_TOTAL
        );
        
        AttributeInstance maxHealthAttr = entity.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealthAttr != null && !maxHealthAttr.hasModifier(healthMod)) {
            maxHealthAttr.addTransientModifier(healthMod);
        }
        
        // Add resistance modifier at higher amplifiers
        if (amplifier >= 1) {
            float resistance = 0.05f + ((amplifier - 1) * 0.025f);
            AttributeModifier resistMod = new AttributeModifier(
                RESISTANCE_UUID,
                "Parasite Vitality Resistance",
                resistance,
                AttributeModifier.Operation.MULTIPLY_TOTAL
            );
            
            AttributeInstance armorAttr = entity.getAttribute(Attributes.ARMOR);
            if (armorAttr != null && !armorAttr.hasModifier(resistMod)) {
                armorAttr.addTransientModifier(resistMod);
            }
        }
    }
    
    /**
     * Removes attribute modifiers when effect expires.
     */
    @Override
    public void removeAttributeModifiers(LivingEntity entity,
                                        AttributeMap attributeMap,
                                        int amplifier) {
        // Remove health modifier
        AttributeInstance maxHealthAttr = entity.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealthAttr != null) {
            maxHealthAttr.removeModifier(HEALTH_UUID);
        }
        
        // Remove resistance modifier
        AttributeInstance armorAttr = entity.getAttribute(Attributes.ARMOR);
        if (armorAttr != null) {
            armorAttr.removeModifier(RESISTANCE_UUID);
        }
    }
    
    /**
     * Spawns vitality particles (green sparkles).
     */
    private void spawnVitalityParticles(@NotNull LivingEntity entity, int amplifier) {
        double x = entity.getX() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        double y = entity.getY() + entity.level().random.nextDouble() * entity.getBbHeight();
        double z = entity.getZ() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        
        // Green sparkle particles
        entity.level().addParticle(
            net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER,
            x, y, z,
            0.0, 0.02, 0.0
        );
        
        // Extra particles at higher amplifier
        if (amplifier >= 2) {
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.ENCHANT,
                x, y + 0.5, z,
                0.0, 0.05, 0.0
            );
        }
    }
}
