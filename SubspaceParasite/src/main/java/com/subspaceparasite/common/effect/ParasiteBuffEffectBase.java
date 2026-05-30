package com.subspaceparasite.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Abstract base class for all parasite buff effects.
 * <p>
 * Provides specialized functionality for parasite-related beneficial effects,
 * including attribute modifiers, stacking behavior, and parasite-specific logic.
 * All parasite buffs should extend this class for consistent behavior.
 * </p>
 * 
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Automatic attribute modifier management</li>
 *   <li>Parasite-specific stacking rules</li>
 *   <li>Amplifier-based scaling</li>
 *   <li>Performance-optimized tick intervals</li>
 *   <li>Built-in UUID management for modifiers</li>
 * </ul>
 * 
 * @author SRP Port Team
 * @see BaseSRPEffect
 */
public abstract class ParasiteBuffEffectBase extends BaseSRPEffect {
    
    /** Base UUID namespace for parasite buff modifiers */
    protected static final UUID MODIFIER_UUID_BASE = UUID.fromString("d3f8a9b2-1c4e-4a7d-9e2f-8b5c3d1a6e9f");
    
    /** Whether this buff applies attribute modifiers */
    protected final boolean hasAttributeModifiers;
    
    /** Attribute modifier amount per amplifier level */
    protected final double modifierAmountPerLevel;
    
    /** Attribute modifier operation type */
    protected final AttributeModifier.Operation operation;
    
    /**
     * Creates a parasite buff effect with default settings.
     * 
     * @param category The effect category (should be BENEFICIAL for buffs)
     * @param color    The color for the effect icon
     */
    protected ParasiteBuffEffectBase(@NotNull MobEffectCategory category, int color) {
        this(category, color, -1, true, 1, false, 0.0, AttributeModifier.Operation.ADDITION);
    }
    
    /**
     * Creates a parasite buff effect with custom settings.
     * 
     * @param category                The effect category
     * @param color                   The color for the effect icon
     * @param maxAmplifier            Maximum amplifier level (-1 = unlimited)
     * @param canStack                Whether this effect can stack
     * @param tickInterval            Ticks between effect applications
     * @param hasAttributeModifiers   Whether this buff applies attribute modifiers
     * @param modifierAmountPerLevel  Base modifier amount per amplifier level
     * @param operation               Modifier operation type (ADDITION, MULTIPLY_BASE, MULTIPLY_TOTAL)
     */
    protected ParasiteBuffEffectBase(@NotNull MobEffectCategory category,
                                     int color,
                                     int maxAmplifier,
                                     boolean canStack,
                                     int tickInterval,
                                     boolean hasAttributeModifiers,
                                     double modifierAmountPerLevel,
                                     AttributeModifier.Operation operation) {
        super(category, color, maxAmplifier, canStack, tickInterval);
        this.hasAttributeModifiers = hasAttributeModifiers;
        this.modifierAmountPerLevel = modifierAmountPerLevel;
        this.operation = operation;
    }
    
    /**
     * Generates a unique UUID for attribute modifier based on effect and attribute.
     * 
     * @param attribute The attribute being modified
     * @param amplifier Current amplifier level
     * @return Unique UUID for this modifier instance
     */
    protected UUID generateModifierUUID(@NotNull Attribute attribute, int amplifier) {
        long mostSigBits = MODIFIER_UUID_BASE.getMostSignificantBits() ^ attribute.hashCode();
        long leastSigBits = MODIFIER_UUID_BASE.getLeastSignificantBits() ^ (long) amplifier;
        return new UUID(mostSigBits, leastSigBits);
    }
    
    /**
     * Applies attribute modifiers when the effect is added.
     * Override in subclasses to apply specific attributes.
     * 
     * @param entity     The entity receiving the effect
     * @param attribute  The attribute instance
     * @param uuid       The modifier UUID
     * @param amplifier  The effect amplifier
     */
    @Override
    public void addAttributeModifiers(LivingEntity entity,
                                     AttributeInstance attribute,
                                     UUID uuid,
                                     int amplifier) {
        if (!hasAttributeModifiers) {
            return;
        }
        
        double amount = calculateModifierAmount(amplifier);
        AttributeModifier modifier = new AttributeModifier(
            uuid,
            "Parasite Buff: " + getDescriptionId(),
            amount,
            operation
        );
        
        if (!attribute.hasModifier(modifier)) {
            attribute.addTransientModifier(modifier);
        }
    }
    
    /**
     * Removes attribute modifiers when the effect is removed.
     * 
     * @param entity    The entity losing the effect
     * @param attribute The attribute instance
     * @param uuid      The modifier UUID
     */
    @Override
    public void removeAttributeModifiers(LivingEntity entity,
                                        AttributeInstance attribute,
                                        UUID uuid) {
        if (!hasAttributeModifiers) {
            return;
        }
        
        attribute.removeModifier(uuid);
    }
    
    /**
     * Calculates the modifier amount based on amplifier.
     * 
     * @param amplifier The effect amplifier level
     * @return The calculated modifier amount
     */
    protected double calculateModifierAmount(int amplifier) {
        return modifierAmountPerLevel * (amplifier + 1);
    }
    
    /**
     * Checks if this buff can be applied to parasites only.
     * Override in subclasses for different targeting rules.
     * 
     * @param entity The target entity
     * @return true if the buff can be applied
     */
    @Override
    public boolean canApply(@NotNull LivingEntity entity) {
        return entity.isAlive();
    }
    
    /**
     * Returns whether this buff is specifically for parasite entities.
     * Used for filtering and synergy checks.
     * 
     * @return true if this is a parasite-only buff
     */
    public boolean isParasiteOnly() {
        return false;
    }
}
