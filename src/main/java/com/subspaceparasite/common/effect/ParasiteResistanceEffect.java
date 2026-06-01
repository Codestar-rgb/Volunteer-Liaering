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
 * Parasite Resistance Effect - Enhanced damage resistance for parasites.
 * <p>
 * This effect provides parasites with exceptional damage reduction and
 * resistance to harmful effects. Essential for surviving against armed
 * opponents and environmental hazards.
 * </p>
 * 
 * <h2>Effects by Amplifier:</h2>
 * <ul>
 *   <li>Level 0: Basic resistance, +10% damage reduction</li>
 *   <li>Level 1: Advanced resistance, +20% damage reduction + status immunity</li>
 *   <li>Level 2+: Perfect resistance, +30% damage reduction + near invulnerability</li>
 * </ul>
 * 
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Significant armor and toughness bonuses</li>
 *   <li>Knockback resistance</li>
 *   <li>Status effect resistance</li>
 *   <li>Environmental damage reduction</li>
 *   <li>Visual resistance barrier particles</li>
 * </ul>
 * 
 * @author SRP Port Team
 * @see ParasiteBuffEffectBase
 */
public class ParasiteResistanceEffect extends ParasiteBuffEffectBase {
    
    /** Armor boost per amplifier level */
    private static final double ARMOR_PER_LEVEL = 3.0;
    
    /** Armor toughness boost per amplifier level */
    private static final double TOUGHNESS_PER_LEVEL = 1.5;
    
    /** Knockback resistance per amplifier level */
    private static final double KNOCKBACK_RESISTANCE_PER_LEVEL = 0.15;
    
    /** UUIDs for attribute modifiers */
    private static final UUID RESISTANCE_ARMOR_UUID = UUID.fromString("d5d5d5d5-aaaa-bbbb-cccc-333000000001");
    private static final UUID RESISTANCE_TOUGHNESS_UUID = UUID.fromString("d5d5d5d5-aaaa-bbbb-cccc-333000000002");
    private static final UUID RESISTANCE_KB_UUID = UUID.fromString("d5d5d5d5-aaaa-bbbb-cccc-333000000003");
    
    /** Tick interval for resistance logic */
    private static final int RESISTANCE_TICK_INTERVAL = 20;
    
    /**
     * Creates the Parasite Resistance effect with standard settings.
     */
    public ParasiteResistanceEffect() {
        super(
            MobEffectCategory.BENEFICIAL,
            0xCD853F,  // Peru brown (resistance/toughness color)
            3,         // Max amplifier
            true,      // Can stack
            RESISTANCE_TICK_INTERVAL,
            true,      // Has attribute modifiers
            ARMOR_PER_LEVEL,
            AttributeModifier.Operation.ADDITION
        );
    }
    
    /**
     * Applies resistance buffs each tick.
     * Grants damage reduction and status immunity.
     * 
     * @param entity    The resistant parasite entity
     * @param amplifier The resistance level/amplifier
     */
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnResistanceParticles(entity, amplifier);
            return;
        }
        
        // Apply status effect resistance
        applyStatusResistance(entity, amplifier);
        
        // Apply absorption at high levels
        applyAbsorption(entity, amplifier);
    }
    
    /**
     * Provides resistance to negative status effects.
     */
    private void applyStatusResistance(@NotNull LivingEntity entity, int amplifier) {
        // Higher resistance grants more status immunity
        if (amplifier >= 1 && entity.tickCount % 40 == 0) {
            // Remove recent negative effects periodically
            entity.getActiveEffects().removeIf(effect -> 
                effect.getEffect().getCategory() == MobEffectCategory.HARMFUL &&
                effect.getDuration() < 100
            );
        }
    }
    
    /**
     * Grants temporary absorption hearts at high resistance.
     */
    private void applyAbsorption(@NotNull LivingEntity entity, int amplifier) {
        if (amplifier >= 2 && entity.tickCount % 60 == 0) {
            float absorptionAmount = amplifier * 3.0f;
            entity.setAbsorptionAmount(Math.max(entity.getAbsorptionAmount(), absorptionAmount));
        }
    }
    
    /**
     * Applies attribute modifiers for resistance effect.
     * Boosts armor, toughness, and knockback resistance.
     */
    @Override
    public void addAttributeModifiers(LivingEntity entity,
                                     AttributeMap attributeMap,
                                     int amplifier) {
        AttributeInstance armorAttr = entity.getAttribute(Attributes.ARMOR);
        if (armorAttr != null) {
            double amount = ARMOR_PER_LEVEL * (amplifier + 1);
            applyModifier(entity, armorAttr, RESISTANCE_ARMOR_UUID, amount);
        }
        AttributeInstance toughnessAttr = entity.getAttribute(Attributes.ARMOR_TOUGHNESS);
        if (toughnessAttr != null) {
            double amount = TOUGHNESS_PER_LEVEL * (amplifier + 1);
            applyModifier(entity, toughnessAttr, RESISTANCE_TOUGHNESS_UUID, amount);
        }
        AttributeInstance kbAttr = entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (kbAttr != null) {
            double amount = KNOCKBACK_RESISTANCE_PER_LEVEL * (amplifier + 1);
            applyModifier(entity, kbAttr, RESISTANCE_KB_UUID, amount);
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
     * Spawns resistance particles (brown/orange shield effect).
     */
    private void spawnResistanceParticles(@NotNull LivingEntity entity, int amplifier) {
        double x = entity.getX() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        double y = entity.getY() + entity.level().random.nextDouble() * entity.getBbHeight();
        double z = entity.getZ() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        
        // Base resistance particles
        entity.level().addParticle(
            net.minecraft.core.particles.ParticleTypes.TOTEM_OF_UNDYING,
            x, y, z,
            0.0, 0.02, 0.0
        );
        
        // Extra barrier particles at higher amplifier
        if (amplifier >= 1) {
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.END_ROD,
                x, y + 0.5, z,
                (entity.level().random.nextDouble() - 0.5) * 0.04,
                0.04,
                (entity.level().random.nextDouble() - 0.5) * 0.04
            );
        }
        
        if (amplifier >= 2) {
            // Full resistance shield visualization
            for (int i = 0; i < 4; i++) {
                double angle = (entity.tickCount + i * 1.5) * 0.1;
                double radius = entity.getBbWidth() / 2.0 + 0.25;
                double px = entity.getX() + Math.cos(angle) * radius;
                double pz = entity.getZ() + Math.sin(angle) * radius;
                
                entity.level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.NAUTILUS,
                    px, y + 0.3, pz,
                    0.0, 0.015, 0.0
                );
            }
        }
    }
    
    /**
     * Resistance is specifically for parasite entities.
     */
    @Override
    public boolean isParasiteOnly() {
        return true;
    }
    
    /**
     * Determines if resistance should tick this frame.
     */
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % RESISTANCE_TICK_INTERVAL == 0;
    }
}
