package com.subspaceparasite.api.parasite;

/**
 * Represents gene types that can be activated on a parasite entity.
 * <p>
 * Genes are split into two categories:
 * <ul>
 *   <li><b>Boolean genes</b> — simple on/off flags (indices 0–5)</li>
 *   <li><b>Float genes</b> — continuous values with a default (indices 0–7)</li>
 * </ul>
 * The boolean and float gene arrays are independent; the index values refer
 * to positions within their respective arrays.
 *
 * @author SubspaceParasite Team
 * @since 1.0.0
 */
public enum GeneType {

    // ────────────────────────────────────────────────────────────────
    //  Boolean genes (index 0–5)
    // ────────────────────────────────────────────────────────────────

    /** Minimum damage threshold — the entity always deals at least some damage. */
    MIN_DAMAGE     (0, "Min Damage",      true,  1.0f),

    /** Damage cap — limits maximum outgoing damage. */
    DAMAGE_CAP     (1, "Damage Cap",       true,  1.0f),

    /** Look-wall — entity can detect players through walls by line-of-sight. */
    LOOK_WALL      (2, "Look Wall",        true,  0.0f),

    /** Sprinting — entity can sprint toward targets. */
    SPRINTING      (3, "Sprinting",        true,  0.0f),

    /** Water leap — entity can leap out of water. */
    WATER_LEAP     (4, "Water Leap",       true,  0.0f),

    /** Special move — entity has a unique special attack ability. */
    SPECIAL_MOVE   (5, "Special Move",     true,  0.0f),

    // ────────────────────────────────────────────────────────────────
    //  Float genes (index 0–7)
    // ────────────────────────────────────────────────────────────────

    /** Poison healing — percentage of damage taken that is healed instead when poisoned. */
    POISON_HEALING (0, "Poison Healing",   false, 0.0f),

    /** Mob healing — passive health regeneration rate from consuming mobs. */
    MOB_HEALING    (1, "Mob Healing",      false, 0.0f),

    /** Attack speed — multiplier on base attack speed. */
    ATTACK_SPEED   (2, "Attack Speed",     false, 1.0f),

    /** Regeneration rate — passive health regeneration per tick. */
    REGEN_RATE     (3, "Regen Rate",       false, 0.0f),

    /** Anti-knockback — percentage reduction of knockback taken (0–1). */
    ANTI_KNOCKBACK (4, "Anti Knockback",   false, 0.0f),

    /** Leap power — multiplier on leap distance/height. */
    LEAP_POWER     (5, "Leap Power",       false, 0.0f),

    /** Projectile speed — multiplier on launched projectile velocity. */
    PROJECTILE_SPEED(6, "Projectile Speed",false, 1.0f),

    /** Infectiousness — probability of spreading infection on attack (0–1). */
    INFECTIOUSNESS (7, "Infectiousness",   false, 0.0f);

    /* ================================================================ */

    /** Index within the boolean or float gene array. */
    private final int index;

    /** Human-readable display name. */
    private final String displayName;

    /** Whether this is a boolean (on/off) gene rather than a float gene. */
    private final boolean isBoolean;

    /** Default value: 1.0f = enabled for boolean; the default float for float genes. */
    private final float defaultValue;

    GeneType(int index, String displayName, boolean isBoolean, float defaultValue) {
        this.index = index;
        this.displayName = displayName;
        this.isBoolean = isBoolean;
        this.defaultValue = defaultValue;
    }

    // ──────────────── Accessors ────────────────

    public int getIndex()                { return index; }
    public String getDisplayName()       { return displayName; }
    public boolean isBoolean()           { return isBoolean; }
    public float getDefaultValue()       { return defaultValue; }

    /**
     * Returns the default boolean value for a boolean gene.
     *
     * @return {@code true} if the default value is &ge; 1.0
     * @throws IllegalStateException if this is not a boolean gene
     */
    public boolean getDefaultBoolean() {
        if (!isBoolean) {
            throw new IllegalStateException(displayName + " is not a boolean gene");
        }
        return defaultValue >= 1.0f;
    }

    /**
     * Returns the default float value for a float gene.
     *
     * @return the default float value
     * @throws IllegalStateException if this is not a float gene
     */
    public float getDefaultFloat() {
        if (isBoolean) {
            throw new IllegalStateException(displayName + " is not a float gene");
        }
        return defaultValue;
    }

    /**
     * Returns the total number of boolean gene slots.
     *
     * @return 6
     */
    public static int booleanGeneCount() {
        return 6;
    }

    /**
     * Returns the total number of float gene slots.
     *
     * @return 8
     */
    public static int floatGeneCount() {
        return 8;
    }
}
