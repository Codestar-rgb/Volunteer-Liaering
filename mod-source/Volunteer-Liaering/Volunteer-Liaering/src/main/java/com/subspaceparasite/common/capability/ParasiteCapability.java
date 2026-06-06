package com.subspaceparasite.common.capability;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.config.ModConfigSystems;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

/**
 * Capability that tracks parasite infection data on ANY LivingEntity.
 * <p>
 * This allows vanilla mobs and players to carry infection state independently
 * of the IInfectable interface (which is only implemented by EntityParasiteBase).
 * The capability is attached to all LivingEntity instances via
 * {@link ParasiteCapabilityEvents}.
 * <p>
 * Infection progresses from 0 to 100. When max is reached, the entity is
 * considered fully infected and will convert on the next conversion check.
 * Resistance reduces the amount of infection gained per application.
 * Cooldown prevents rapid re-infection after purging.
 * <p>
 * Infection level-based effects (matching original SRP):
 * <ul>
 *   <li>Level 20+: Nausea (Confusion)</li>
 *   <li>Level 40+: Weakness</li>
 *   <li>Level 60+: Slowness</li>
 *   <li>Level 80+: Wither</li>
 *   <li>Level 100: Full conversion to parasite</li>
 * </ul>
 */
public class ParasiteCapability {

    /** The capability instance, obtained via CapabilityToken. */
    public static final Capability<ParasiteCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    // ── Infection state ──

    /** Current infection level (0 = uninfected, 100 = fully infected). */
    private int infectionLevel;

    /** Resistance factor that reduces infection gain (0.0 = no resistance, 1.0 = immune). */
    private float infectionResistance;

    /** Cooldown ticks before the entity can be infected again (e.g. after purging). */
    private int infectionCooldown;

    /** Whether this entity has been marked as permanently immune. */
    private boolean immune;

    /** Extra data tag for per-entity state storage used by effects. */
    private CompoundTag extraData;

    /** Maximum infection level before conversion is triggered. */
    public static final int MAX_INFECTION = 100;

    public ParasiteCapability() {
        this.infectionLevel = 0;
        this.infectionResistance = 0.0f;
        this.infectionCooldown = 0;
        this.immune = false;
        this.extraData = new CompoundTag();
    }

    // ── Infection methods ──

    /**
     * Adds infection to this entity, respecting resistance and immunity.
     *
     * @param amount the raw amount of infection to add
     */
    public void addInfection(int amount) {
        if (immune || infectionCooldown > 0) return;
        float effective = amount * (1.0f - infectionResistance);
        infectionLevel = Math.min(MAX_INFECTION, infectionLevel + Math.max(0, (int) effective));
    }

    /**
     * Returns the current infection level.
     *
     * @return infection level (0–100)
     */
    public int getInfectionLevel() {
        return infectionLevel;
    }

    /**
     * Sets the infection level directly, clamped to [0, MAX_INFECTION].
     *
     * @param level the new infection level
     */
    public void setInfectionLevel(int level) {
        this.infectionLevel = Math.max(0, Math.min(MAX_INFECTION, level));
    }

    /**
     * Returns the infection resistance factor.
     *
     * @return resistance in the range [0.0, 1.0]
     */
    public float getInfectionResistance() {
        return infectionResistance;
    }

    /**
     * Sets the infection resistance factor.
     *
     * @param resistance resistance in the range [0.0, 1.0]
     */
    public void setInfectionResistance(float resistance) {
        this.infectionResistance = Math.max(0.0f, Math.min(1.0f, resistance));
    }

    /**
     * Returns whether this entity is completely immune to parasite infection.
     *
     * @return true if the entity cannot be infected
     */
    public boolean isImmune() {
        return immune;
    }

    /**
     * Sets whether this entity is permanently immune.
     *
     * @param immune true to make immune
     */
    public void setImmune(boolean immune) {
        this.immune = immune;
    }

    /**
     * Returns whether the infection has reached the maximum level,
     * meaning the entity is ready for conversion.
     *
     * @return true if infection is at max
     */
    public boolean isFullyInfected() {
        return infectionLevel >= MAX_INFECTION;
    }

    /**
     * Reduces infection (e.g. from curative effects or purifiers).
     *
     * @param amount the amount to reduce
     */
    public void reduceInfection(int amount) {
        infectionLevel = Math.max(0, infectionLevel - amount);
    }

    /**
     * Completely resets infection state.
     */
    public void clearInfection() {
        infectionLevel = 0;
    }

    // ── Cooldown ──

    /**
     * Returns the remaining infection cooldown ticks.
     *
     * @return cooldown ticks remaining
     */
    public int getInfectionCooldown() {
        return infectionCooldown;
    }

    /**
     * Sets the infection cooldown.
     *
     * @param ticks cooldown in ticks
     */
    public void setInfectionCooldown(int ticks) {
        this.infectionCooldown = Math.max(0, ticks);
    }

    // ── Infection effect thresholds ──

    /** Infection level at which Nausea is applied. */
    public static final int NAUSEA_THRESHOLD = 20;
    /** Infection level at which Weakness is applied. */
    public static final int WEAKNESS_THRESHOLD = 40;
    /** Infection level at which Slowness is applied. */
    public static final int SLOWNESS_THRESHOLD = 60;
    /** Infection level at which Wither is applied. */
    public static final int WITHER_THRESHOLD = 80;
    /** Infection level at which full conversion occurs. */
    public static final int CONVERSION_THRESHOLD = MAX_INFECTION;

    /** Duration for infection debuff effects (in ticks). */
    private static final int EFFECT_DURATION = 200;
    /** Tick interval for applying infection effects (every 2 seconds). */
    private static final int EFFECT_TICK_INTERVAL = 40;

    // ── Tick ──

    /**
     * Called every tick to update infection state.
     * Decrements cooldown only. Use {@link #tickEffects(LivingEntity)}
     * to apply infection level-based effects.
     */
    public void tick() {
        if (infectionCooldown > 0) {
            infectionCooldown--;
        }
    }

    /**
     * Called every tick to apply infection level-based effects to the entity.
     * Should be called from a server tick handler with the associated entity.
     * <p>
     * Effects are applied on a 40-tick interval to avoid constant re-application:
     * <ul>
     *   <li>Level 20+: Nausea (Confusion)</li>
     *   <li>Level 40+: Weakness</li>
     *   <li>Level 60+: Slowness</li>
     *   <li>Level 80+: Wither</li>
     *   <li>Level 100: Marked for full conversion (handled by conversion system)</li>
     * </ul>
     *
     * @param entity the entity this capability belongs to
     */
    public void tickEffects(LivingEntity entity) {
        if (entity.level().isClientSide) return;
        if (infectionLevel <= 0 || immune) return;
        if (entity instanceof EntityParasiteBase) return; // Parasites don't get debuffed
        if (entity.tickCount % EFFECT_TICK_INTERVAL != 0) return;

        // Level 20+: Nausea (Confusion)
        if (infectionLevel >= NAUSEA_THRESHOLD) {
            applyInfectionEffect(entity, MobEffects.CONFUSION, 0);
        }

        // Level 40+: Weakness
        if (infectionLevel >= WEAKNESS_THRESHOLD) {
            int weaknessAmp = (infectionLevel - WEAKNESS_THRESHOLD) / 20;
            applyInfectionEffect(entity, MobEffects.WEAKNESS, Math.min(weaknessAmp, 2));
        }

        // Level 60+: Slowness
        if (infectionLevel >= SLOWNESS_THRESHOLD) {
            int slownessAmp = (infectionLevel - SLOWNESS_THRESHOLD) / 15;
            applyInfectionEffect(entity, MobEffects.MOVEMENT_SLOWDOWN, Math.min(slownessAmp, 3));
        }

        // Level 80+: Wither
        if (infectionLevel >= WITHER_THRESHOLD) {
            int witherAmp = (infectionLevel - WITHER_THRESHOLD) / 10;
            applyInfectionEffect(entity, MobEffects.WITHER, Math.min(witherAmp, 2));
        }

        // Level 100: Full conversion marker — actual conversion is handled
        // by InfectionComponent.checkConversion() when it detects a withered entity
    }

    /**
     * Applies an infection effect to the entity if not already present at
     * the same or higher amplifier.
     */
    private void applyInfectionEffect(LivingEntity entity,
                                       net.minecraft.world.effect.MobEffect effect, int amplifier) {
        MobEffectInstance existing = entity.getEffect(effect);
        if (existing == null || existing.getAmplifier() < amplifier || existing.getDuration() < EFFECT_DURATION / 2) {
            entity.addEffect(new MobEffectInstance(effect, EFFECT_DURATION, amplifier));
        }
    }

    // ── NBT serialization ──

    /**
     * Saves this capability's data to NBT.
     *
     * @return serialized data
     */
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("InfectionLevel", infectionLevel);
        tag.putFloat("InfectionResistance", infectionResistance);
        tag.putInt("InfectionCooldown", infectionCooldown);
        tag.putBoolean("Immune", immune);
        if (extraData != null && !extraData.isEmpty()) {
            tag.put("ExtraData", extraData);
        }
        return tag;
    }

    /**
     * Loads this capability's data from NBT.
     *
     * @param tag the serialized data
     */
    public void deserializeNBT(CompoundTag tag) {
        infectionLevel = tag.getInt("InfectionLevel");
        infectionResistance = tag.getFloat("InfectionResistance");
        infectionCooldown = tag.getInt("InfectionCooldown");
        immune = tag.getBoolean("Immune");
        extraData = tag.contains("ExtraData") ? tag.getCompound("ExtraData") : new CompoundTag();
    }
    
    /**
     * Returns the extra data CompoundTag for per-entity state storage.
     * Effects can use this to store entity-specific state (e.g., Derivation progress)
     * without needing to add fields to the capability class.
     *
     * @return mutable extra data tag
     */
    public CompoundTag getExtraData() {
        if (extraData == null) {
            extraData = new CompoundTag();
        }
        return extraData;
    }

    /**
     * Marks this capability as dirty to trigger synchronization.
     * Should be called after any state change that needs to be synced.
     */
    public void markDirty() {
        // This method is a placeholder for capability sync logic
        // Actual implementation depends on the capability attachment system
        // The capability provider should handle synchronization
    }
}
