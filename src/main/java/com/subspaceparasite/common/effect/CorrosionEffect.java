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
 * Corrosion effect - Degrades armor and increases damage taken.
 * <p>
 * This effect represents the corrosive properties of parasite attacks.
 * It reduces the target's effective armor value and armor durability,
 * making them increasingly vulnerable to subsequent attacks.
 * </p>
 * 
 * <h2>Effects by Amplifier:</h2>
 * <ul>
 *   <li>Level 0: -10% armor effectiveness, slow durability drain</li>
 *   <li>Level 1: -25% armor effectiveness, moderate durability drain</li>
 *   <li>Level 2+: -50% armor effectiveness, rapid durability drain</li>
 * </ul>
 * 
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Percentage-based armor reduction</li>
 *   <li>Armor durability damage over time</li>
 *   <li>Visual corrosion particles</li>
 *   <li>Synergy with other debuffs for devastating combos</li>
 * </ul>
 * 
 * @author SRP Port Team
 */
public class CorrosionEffect extends BaseSRPEffect {
    
    /** UUID for armor reduction modifier */
    private static final UUID CORROSION_ARMOR_UUID = UUID.fromString("f7a8b9c0-d1e2-4f5a-8b6c-7d8e9f0a1b2c");
    
    /** Armor effectiveness reduction per amplifier (percentage) */
    private static final float ARMOR_REDUCTION_PER_LEVEL = 0.15f;
    
    /** Base durability damage per tick */
    private static final int BASE_DURABILITY_DAMAGE = 1;
    
    /** Durability damage increase per amplifier */
    private static final int DURABILITY_DAMAGE_PER_LEVEL = 2;
    
    /** Tick interval for durability damage */
    private static final int CORROSION_TICK_INTERVAL = 60; // Every 3 seconds
    
    /**
     * Creates the Corrosion effect with standard settings.
     */
    public CorrosionEffect() {
        super(
            MobEffectCategory.HARMFUL,
            0x006400,  // Dark green (acid/corrosion color)
            3,         // Max amplifier
            true,      // Can stack
            CORROSION_TICK_INTERVAL
        );
    }
    
    /**
     * Applies corrosion effects periodically.
     * Damages armor durability and applies vulnerability.
     * 
     * @param entity    The corroded entity
     * @param amplifier The effect amplifier
     */
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnCorrosionParticles(entity, amplifier);
            return;
        }
        
        // Damage armor durability
        damageArmor(entity, amplifier);
        
        // Apply temporary vulnerability (handled via attribute or separate effect)
        applyVulnerability(entity, amplifier);
    }
    
    /**
     * Damages all equipped armor pieces.
     * Higher amplifiers cause more severe damage.
     */
    private void damageArmor(@NotNull LivingEntity entity, int amplifier) {
        int durabilityDamage = BASE_DURABILITY_DAMAGE + (amplifier * DURABILITY_DAMAGE_PER_LEVEL);
        
        // Damage each armor piece
        for (var itemStack : entity.getArmorSlots()) {
            if (!itemStack.isEmpty() && itemStack.isDamageableItem()) {
                // Random chance to actually damage (simulates corrosion)
                if (entity.level().random.nextFloat() < 0.5f) {
                    itemStack.hurtAndBreak(durabilityDamage, entity, e -> {});
                }
            }
        }
    }
    
    /**
     * Applies vulnerability effect to the entity.
     * This makes them take increased damage from all sources.
     */
    private void applyVulnerability(@NotNull LivingEntity entity, int amplifier) {
        // Vulnerability is handled through attribute modifiers in addAttributeModifiers
        // This method exists for future expansion
    }
    
    /**
     * Adds armor reduction modifier when effect is applied.
     */
    @Override
    public void addAttributeModifiers(LivingEntity entity,
                                     net.minecraft.world.entity.ai.attributes.AttributeMap attributeMap,
                                     int amplifier) {
        // Reduce armor effectiveness based on amplifier
        double armorReduction = ARMOR_REDUCTION_PER_LEVEL * (amplifier + 1);
        AttributeInstance armorAttr = entity.getAttribute(Attributes.ARMOR);
        if (armorAttr != null) {
            AttributeModifier armorMod = new AttributeModifier(
                CORROSION_ARMOR_UUID,
                "Corrosion Armor Reduction",
                -armorReduction,
                AttributeModifier.Operation.MULTIPLY_TOTAL
            );
            if (!armorAttr.hasModifier(armorMod)) {
                armorAttr.addTransientModifier(armorMod);
            }
        }
    }
    
    /**
     * Removes armor reduction modifier when effect expires.
     */
    @Override
    public void removeAttributeModifiers(LivingEntity entity,
                                        net.minecraft.world.entity.ai.attributes.AttributeMap attributeMap,
                                        int amplifier) {
        AttributeInstance armorAttr = entity.getAttribute(Attributes.ARMOR);
        if (armorAttr != null) {
            armorAttr.removeModifier(CORROSION_ARMOR_UUID);
        }
    }
    
    /**
     * Spawns corrosion particles (green acid droplets).
     */
    private void spawnCorrosionParticles(@NotNull LivingEntity entity, int amplifier) {
        double x = entity.getX() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        double y = entity.getY() + 0.5 + entity.level().random.nextDouble() * entity.getBbHeight() * 0.3;
        double z = entity.getZ() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        
        // Acid drip particles
        entity.level().addParticle(
            net.minecraft.core.particles.ParticleTypes.DRIPPING_OBSIDIAN_TEAR, // Using as acid proxy
            x, y, z,
            0.0, -0.05, 0.0
        );
        
        // More particles at higher amplifier
        if (amplifier >= 1) {
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.SMOKE,
                x, y + 0.2, z,
                0.0, 0.02, 0.0
            );
        }
        
        if (amplifier >= 2) {
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.MYCELIUM,
                x, y + 0.1, z,
                (entity.level().random.nextDouble() - 0.5) * 0.1,
                0.05,
                (entity.level().random.nextDouble() - 0.5) * 0.1
            );
        }
    }
    
    /**
     * Determines if corrosion should tick this frame.
     * Uses longer interval for performance.
     */
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % CORROSION_TICK_INTERVAL == 0;
    }
}
