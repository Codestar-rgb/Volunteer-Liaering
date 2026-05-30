package com.subspaceparasite.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

/**
 * Base class for all custom mob effects in the SubspaceParasite mod.
 * <p>
 * Provides common functionality for effect ticking, amplification limits,
 * and stacking behavior. All SRP custom effects should extend this class.
 * </p>
 * 
 * @author SubspaceParasite Team
 * @since 1.0.0
 */
public abstract class CustomMobEffect extends MobEffect {
    
    /** Maximum amplifier level for this effect (0 = level I, 1 = level II, etc.) */
    protected final int maxAmplifier;
    
    /** Whether this effect can stack with itself */
    protected final boolean canStack;
    
    /** Default duration in ticks (1 second = 20 ticks) */
    protected static final int DEFAULT_DURATION = 200;
    
    /**
     * Creates a new custom mob effect.
     * 
     * @param category the effect category (HARMFUL, BENEFICIAL, or NEUTRAL)
     * @param color the RGB color for the effect icon (format: 0xRRGGBB)
     */
    protected CustomMobEffect(MobEffectCategory category, int color) {
        this(category, color, 255, true);
    }
    
    /**
     * Creates a new custom mob effect with custom parameters.
     * 
     * @param category the effect category (HARMFUL, BENEFICIAL, or NEUTRAL)
     * @param color the RGB color for the effect icon (format: 0xRRGGBB)
     * @param maxAmplifier maximum amplifier level (0-255)
     * @param canStack whether this effect can stack with itself
     */
    protected CustomMobEffect(MobEffectCategory category, int color, int maxAmplifier, boolean canStack) {
        super(category, color);
        this.maxAmplifier = maxAmplifier;
        this.canStack = canStack;
    }
    
    /**
     * Called every tick while the effect is active.
     * <p>
     * Override this method to implement custom effect behavior.
     * Use {@link #isDurationEffectTick(int, int)} to control tick frequency.
     * </p>
     * 
     * @param entity the entity affected by this effect
     * @param amplifier the effect amplifier (0 = level I, 1 = level II, etc.)
     */
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // Default implementation - override in subclasses
    }
    
    /**
     * Determines if the effect should tick on the current game tick.
     * <p>
     * Override this method to control how frequently the effect applies.
     * For example, return {@code true} only every 20 ticks for once-per-second behavior.
     * </p>
     * 
     * @param duration the remaining duration of the effect in ticks
     * @param amplifier the effect amplifier (0 = level I, 1 = level II, etc.)
     * @return true if the effect should tick this game tick
     */
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // Default: tick every game tick
        return true;
    }
    
    /**
     * Called when the effect is first applied to an entity.
     * <p>
     * Override this method to apply instant effects or initial conditions.
     * </p>
     * 
     * @param entity the entity the effect was applied to
     * @param amplifier the effect amplifier (0 = level I, 1 = level II, etc.)
     */
    @Override
    public void applyInstantenousEffect(net.minecraft.world.entity.Entity source, 
                                        net.minecraft.world.entity.Entity indirectSource, 
                                        LivingEntity entity, 
                                        int amplifier, 
                                        double health) {
        // Default implementation - override for instant effects
    }
    
    /**
     * Checks if this effect can stack with an existing effect instance.
     * 
     * @param existing the existing effect instance
     * @return true if this effect can stack with the existing one
     */
    public boolean canStackWith(net.minecraft.world.effect.MobEffectInstance existing) {
        return this.canStack && existing.getEffect() == this;
    }
    
    /**
     * Gets the maximum amplifier level for this effect.
     * 
     * @return the maximum amplifier (0 = level I, 1 = level II, etc.)
     */
    public int getMaxAmplifier() {
        return this.maxAmplifier;
    }
    
    /**
     * Gets the effective amplifier, clamped to the maximum.
     * 
     * @param amplifier the requested amplifier
     * @return the clamped amplifier
     */
    protected int getEffectiveAmplifier(int amplifier) {
        return Math.min(amplifier, this.maxAmplifier);
    }
    
    /**
     * Helper method to calculate damage based on amplifier.
     * 
     * @param baseDamage the base damage value
     * @param amplifier the effect amplifier
     * @param multiplierPerLevel damage multiplier per amplifier level
     * @return the calculated damage
     */
    protected float calculateScaledDamage(float baseDamage, int amplifier, float multiplierPerLevel) {
        return baseDamage * (1.0F + amplifier * multiplierPerLevel);
    }
    
    /**
     * Helper method to calculate duration modifier based on amplifier.
     * 
     * @param baseDuration the base duration in ticks
     * @param amplifier the effect amplifier
     * @param extraTicksPerLevel extra ticks per amplifier level
     * @return the modified duration
     */
    protected int calculateScaledDuration(int baseDuration, int amplifier, int extraTicksPerLevel) {
        return baseDuration + (amplifier * extraTicksPerLevel);
    }
}
