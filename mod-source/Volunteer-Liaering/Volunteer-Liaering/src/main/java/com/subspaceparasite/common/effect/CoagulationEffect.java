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
 * Coagulation Effect - Reduces healing effectiveness and causes blood clotting.
 * <p>
 * This effect represents the parasite's ability to interfere with the target's
 * natural healing processes. It reduces all incoming healing and can cause
 * additional damage when the entity attempts to heal.
 * </p>
 * 
 * <h2>Effects by Amplifier:</h2>
 * <ul>
 *   <li>Level 0: -25% healing received, minor clot damage on heal</li>
 *   <li>Level 1: -50% healing received, moderate clot damage on heal</li>
 *   <li>Level 2+: -75% healing received, severe clot damage + DoT</li>
 * </ul>
 * 
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Percentage-based healing reduction</li>
 *   <li>Damage reflection when healing is attempted</li>
 *   <li>Continuous damage over time at high amplifiers</li>
 *   <li>Synergizes with BLEED for devastating effect</li>
 * </ul>
 * 
 * @author SRP Port Team
 * @see BleedEffect
 */
public class CoagulationEffect extends BaseSRPEffect {
    
    /** Unique UUID for armor reduction modifier (proxy until healing event is implemented) */
    private static final UUID ARMOR_REDUCTION_UUID = UUID.fromString("c3d4e5f6-a7b8-9012-cdef-123456789abc");
    
    /** Healing reduction per amplifier (percentage) */
    private static final float HEALING_REDUCTION_PER_LEVEL = 0.25f;
    
    /** Damage reflected when healing is attempted */
    private static final float REFLECT_DAMAGE_PER_HEAL = 0.5f;
    
    /** Base damage over time per tick */
    private static final float BASE_DOT_DAMAGE = 0.25f;
    
    /** Tick interval for DOT application */
    private static final int DOT_TICK_INTERVAL = 40;
    
    /**
     * Creates the Coagulation effect with standard settings.
     */
    public CoagulationEffect() {
        super(
            MobEffectCategory.HARMFUL,
            0x7A0000,  // Dried blood red
            3,         // Max amplifier
            true,      // Can stack
            DOT_TICK_INTERVAL
        );
    }
    
    /**
     * Applies coagulation effects each tick.
     * At high amplifiers, applies continuous damage over time.
     * 
     * @param entity    The affected entity
     * @param amplifier The effect amplifier
     */
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnCoagulationParticles(entity, amplifier);
            return;
        }
        
        // Apply DOT at higher amplifiers
        if (amplifier >= 2) {
            applyDamageOverTime(entity, amplifier);
        }
        
        // Note: Healing reduction is handled via attribute modifiers
        // Damage reflection would require a Forge event listener
    }
    
    /**
     * Applies damage over time based on amplifier level.
     * Represents worsening blood clots.
     * 
     * @param entity    The target entity
     * @param amplifier The effect amplifier
     */
    private void applyDamageOverTime(@NotNull LivingEntity entity, int amplifier) {
        float damage = BASE_DOT_DAMAGE + ((amplifier - 2) * 0.15f);
        
        if (damage > 0) {
            entity.hurt(entity.damageSources().magic(), damage);
        }
    }
    
    /**
     * Adds attribute modifiers when effect is applied.
     * Reduces healing effectiveness through custom attribute.
     * 
     * @param entity     The entity
     * @param attribute  The attribute instance
     * @param uuid       The modifier UUID
     * @param amplifier  The effect amplifier
     */
    @Override
    public void addAttributeModifiers(LivingEntity entity,
                                     AttributeMap attributeMap,
                                     int amplifier) {
        // Apply armor reduction as a proxy for reduced vitality
        // TODO: Replace with a Forge LivingHealEvent handler for proper healing reduction
        // The armor reduction simulates the entity being more vulnerable due to impaired healing
        float armorReduction = HEALING_REDUCTION_PER_LEVEL * amplifier;
        AttributeModifier armorReductionMod = new AttributeModifier(
            ARMOR_REDUCTION_UUID,
            "Coagulation Armor Reduction",
            -armorReduction,
            AttributeModifier.Operation.MULTIPLY_TOTAL
        );
        
        AttributeInstance armorAttr = entity.getAttribute(Attributes.ARMOR);
        if (armorAttr != null && !armorAttr.hasModifier(armorReductionMod)) {
            armorAttr.addTransientModifier(armorReductionMod);
        }
    }
    
    /**
     * Removes attribute modifiers when effect expires.
     * 
     * @param entity    The entity
     * @param attribute The attribute instance
     * @param uuid      The modifier UUID
     */
    @Override
    public void removeAttributeModifiers(LivingEntity entity,
                                        AttributeMap attributeMap,
                                        int amplifier) {
        // Remove armor reduction modifier
        AttributeInstance armorAttr = entity.getAttribute(Attributes.ARMOR);
        if (armorAttr != null) {
            armorAttr.removeModifier(ARMOR_REDUCTION_UUID);
        }
    }
    
    /**
     * Spawns coagulation particles (dark red clots).
     * 
     * @param entity    The affected entity
     * @param amplifier The effect amplifier
     */
    private void spawnCoagulationParticles(@NotNull LivingEntity entity, int amplifier) {
        double x = entity.getX() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        double y = entity.getY() + 0.5 + entity.level().random.nextDouble() * entity.getBbHeight() * 0.3;
        double z = entity.getZ() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        
        // Dark red particles representing clotted blood
        entity.level().addParticle(
            net.minecraft.core.particles.ParticleTypes.DRIPPING_HONEY,
            x, y, z,
            0.0, -0.05, 0.0
        );
        
        // Additional particles at higher amplifier
        if (amplifier >= 1) {
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.SCULK_SOUL,
                x, y + 0.2, z,
                0.0, 0.02, 0.0
            );
        }
        
        if (amplifier >= 2) {
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.WITCH,
                x, y + 0.1, z,
                (entity.level().random.nextDouble() - 0.5) * 0.1,
                0.03,
                (entity.level().random.nextDouble() - 0.5) * 0.1
            );
        }
    }
    
    /**
     * Determines if coagulation should tick this frame.
     * 
     * @param duration  Remaining duration
     * @param amplifier Effect amplifier
     * @return true if should tick
     */
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // DOT ticks every 40 ticks, but we check more frequently for other effects
        return duration % 10 == 0;
    }
    
    /**
     * Gets the healing reduction percentage for this amplifier.
     * 
     * @param amplifier The effect amplifier
     * @return Healing reduction (0.0 to 1.0)
     */
    public float getHealingReduction(int amplifier) {
        return Math.min(0.9f, HEALING_REDUCTION_PER_LEVEL * amplifier);
    }
    
    /**
     * Calculates damage reflected when entity receives healing.
     * 
     * @param healAmount The amount of healing received
     * @param amplifier  The effect amplifier
     * @return Reflected damage amount
     */
    public float calculateReflectDamage(float healAmount, int amplifier) {
        return healAmount * REFLECT_DAMAGE_PER_HEAL * (1.0f + amplifier * 0.2f);
    }
}
