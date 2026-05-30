package com.subspaceparasite.common.capability;

import net.minecraft.nbt.CompoundTag;
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

    /** Maximum infection level before conversion is triggered. */
    public static final int MAX_INFECTION = 100;

    public ParasiteCapability() {
        this.infectionLevel = 0;
        this.infectionResistance = 0.0f;
        this.infectionCooldown = 0;
        this.immune = false;
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

    // ── Tick ──

    /**
     * Called every tick to update infection state.
     * Decrements cooldown and applies passive effects at high infection.
     */
    public void tick() {
        if (infectionCooldown > 0) {
            infectionCooldown--;
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
    }
}
