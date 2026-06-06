package com.subspaceparasite.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for all custom SRP mob effects.
 * <p>
 * Provides common functionality for effect stacking, duration handling,
 * and standardized tick logic. All custom effects should extend this class
 * to ensure consistent behavior across the mod.
 * </p>
 * 
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Configurable maximum amplifier (stacking limit)</li>
 *   <li>Custom duration tick logic per effect type</li>
 *   <li>Standardized apply/tick methods with null safety</li>
 *   <li>Performance-optimized tick interval checks</li>
 * </ul>
 * 
 * @author SRP Port Team
 * @see MobEffect
 */
public abstract class BaseSRPEffect extends MobEffect {
    
    /** Maximum allowed amplifier level for this effect (-1 = unlimited) */
    protected final int maxAmplifier;
    
    /** Whether this effect can stack multiple times on the same entity */
    protected final boolean canStack;
    
    /** Tick interval for applying effect logic (1 = every tick) */
    protected final int tickInterval;
    
    /**
     * Creates a new base SRP effect with default settings.
     * 
     * @param category The effect category (HARMFUL, BENEFICIAL, or NEUTRAL)
     * @param color    The color for the effect icon (RGB packed int)
     */
    protected BaseSRPEffect(@NotNull MobEffectCategory category, int color) {
        this(category, color, -1, true, 1);
    }
    
    /**
     * Creates a new base SRP effect with custom settings.
     * 
     * @param category      The effect category (HARMFUL, BENEFICIAL, or NEUTRAL)
     * @param color         The color for the effect icon (RGB packed int)
     * @param maxAmplifier  Maximum amplifier level (-1 = unlimited)
     * @param canStack      Whether this effect can stack
     * @param tickInterval  Ticks between effect applications (1 = every tick)
     */
    protected BaseSRPEffect(@NotNull MobEffectCategory category, 
                           int color, 
                           int maxAmplifier, 
                           boolean canStack,
                           int tickInterval) {
        super(category, color);
        this.maxAmplifier = maxAmplifier;
        this.canStack = canStack;
        this.tickInterval = tickInterval;
    }
    
    /**
     * Returns whether this effect can stack with existing instances.
     * 
     * @return true if stacking is allowed
     */
    public boolean canStack() {
        return canStack;
    }
    
    /**
     * Returns the maximum amplifier level for this effect.
     * 
     * @return Maximum amplifier, or -1 for unlimited
     */
    public int getMaxAmplifier() {
        return maxAmplifier;
    }
    
    /**
     * Called when the effect is first applied to an entity.
     * Override to apply instant effects or initialize state.
     * 
     * @param entity     The entity receiving the effect
     * @param amplifier  The effect amplifier level
     */
    @Override
    public void addAttributeModifiers(LivingEntity entity, 
                                     AttributeMap attributeMap, 
                                     int amplifier) {
        // Default implementation - override in subclasses
    }
    
    /**
     * Called every tick while the effect is active.
     * Override to implement continuous effect logic.
     * 
     * @param entity     The entity affected
     * @param amplifier  The effect amplifier level
     */
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        // Default implementation - override in subclasses
    }
    
    /**
     * Determines if the effect should tick this frame.
     * Uses the configured tick interval for performance optimization.
     * 
     * @param duration   Current remaining duration
     * @param amplifier  Effect amplifier level
     * @return true if the effect should tick
     */
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        if (tickInterval <= 1) {
            return true;
        }
        return duration % tickInterval == 0;
    }
    
    /**
     * Called when the effect is removed from an entity.
     * Override to clean up state or apply removal effects.
     * 
     * @param entity The entity losing the effect
     */
    @Override
    public void removeAttributeModifiers(LivingEntity entity,
                                        AttributeMap attributeMap,
                                        int amplifier) {
        // Default implementation - override in subclasses
    }
    
    /**
     * Checks if this effect is ready to be applied based on game state.
     * Can be overridden for conditional effect application.
     * 
     * @param entity The target entity
     * @return true if the effect can be applied
     */
    public boolean canApply(@NotNull LivingEntity entity) {
        return entity.isAlive();
    }
    
    /**
     * Gets the effective amplifier after applying caps and modifiers.
     * 
     * @param baseAmplifier The base amplifier level
     * @return The clamped amplifier value
     */
    protected int getEffectiveAmplifier(int baseAmplifier) {
        if (maxAmplifier >= 0) {
            return Math.min(baseAmplifier, maxAmplifier);
        }
        return baseAmplifier;
    }
    
    /**
     * Applies instant damage to an entity.
     * 
     * @param entity   The target entity
     * @param amount   Base damage amount
     * @param amplifier Current effect amplifier
     */
    protected void applyInstantDamage(@NotNull LivingEntity entity, float amount, int amplifier) {
        float totalDamage = amount * (1.0f + amplifier * 0.5f);
        entity.hurt(entity.damageSources().magic(), totalDamage);
    }
    
    /**
     * Applies instant healing to an entity.
     * 
     * @param entity   The target entity
     * @param amount   Base heal amount
     * @param amplifier Current effect amplifier
     */
    protected void applyInstantHeal(@NotNull LivingEntity entity, float amount, int amplifier) {
        float totalHeal = amount * (1.0f + amplifier * 0.5f);
        entity.heal(totalHeal);
    }
}
