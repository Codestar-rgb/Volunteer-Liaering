package com.subspaceparasite.api.parasite;

import java.util.Arrays;

/**
 * Represents dislodgment codes from the original Scape and Run: Parasites mod.
 * <p>
 * Dislodgment codes are bit-flag-like identifiers attached to parasite entities
 * that govern special behaviours on death, damage, or certain events. Each code
 * triggers a specific effect when a parasite with that code is "dislodged"
 * (killed or removed from the world).
 *
 * @author SubspaceParasite Team
 * @since 1.0.0
 */
public enum DislodgmentCode {

    /** No dislodgment behaviour. */
    NONE(0,       "None",              "No special dislodgment behaviour."),

    /** Generic dislodgment — standard death. */
    ONE(1,        "Standard",          "Standard death with no special effects."),

    /** Spawns a Jugg parasite on death. */
    TWO(2,        "Jugg Spawn",        "Spawns a Jugg-type parasite upon death."),

    THREE(3,      "Minor Explosion",   "Small knockback explosion on death."),

    FOUR(4,       "Toxin Cloud",       "Releases a toxin cloud on death."),

    FIVE(5,       "Acid Splash",       "Splashes acid in the surrounding area."),

    /** Breaks items held by the killing entity. */
    SIX(6,        "Break Held Items",  "Breaks items held by the player that killed this parasite."),

    /** Heals nearby parasites on death. */
    SEVEN(7,      "Heal Nearby",       "Heals nearby parasite entities upon death."),

    /** Damages nearby non-parasite entities on death. */
    EIGHT(8,      "Damage Nearby",     "Damages nearby non-parasite entities upon death."),

    /** Drains the hunger bar of nearby players. */
    NINE(9,       "Drain Hunger",      "Drains the hunger bar of nearby players upon death."),

    TEN(10,       "Slow Cloud",        "Releases a slowing effect cloud on death."),

    /** Grants immunity to negative status effects while alive. */
    ELEVEN(11,    "Neg. Effect Immune","This parasite is immune to negative status effects."),

    TWELVE(12,    "Fear Aura",         "Applies fear to nearby non-parasites on death."),

    THIRTEEN(13,  "Web Trap",          "Places cobweb-like traps on death."),

    FOURTEEN(14,  "Scream",            "Emits a scream that alerts nearby parasites."),

    /** Synced flag — dislodgment effect is synchronised to clients. */
    FIFTEEN(15,   "Synced Flag",       "Dislodgment effect is synchronised across all clients."),

    SIXTEEN(16,   "Particle Burst",    "Releases a burst of parasite particles on death."),

    SEVENTEEN(17, "Evolution Burst",   "Grants evolution points to nearby parasites on death."),

    /** Suppresses death animation. */
    EIGHTEEN(18,  "No Death Anim",     "Suppresses the death animation — entity simply vanishes."),

    NINETEEN(19,  "Fire Burst",        "Releases fire in the surrounding area on death."),

    TWENTY(20,    "Gravity Pull",      "Pulls nearby entities toward the death location."),

    /** Damage immunity unless the source is fire or the entity is wet. */
    TWENTY_ONE(21,"Fire/Wet Damage",   "Immune to all damage unless the source is fire or the entity is wet."),

    TWENTY_TWO(22,"Colony Alert",      "Alerts all colony structures in range upon death.");

    /* ================================================================ */

    /** The integer dislodgment code (0–22). */
    private final int code;

    /** Short human-readable name. */
    private final String displayName;

    /** Detailed description of what this code does. */
    private final String description;

    DislodgmentCode(int code, String displayName, String description) {
        this.code = code;
        this.displayName = displayName;
        this.description = description;
    }

    // ──────────────── Accessors ────────────────

    public int getCode()               { return code; }
    public String getDisplayName()     { return displayName; }
    public String getDescription()     { return description; }

    // ──────────────── Lookup ────────────────

    /**
     * Retrieves a {@code DislodgmentCode} by its integer code.
     *
     * @param code the dislodgment code (0–22)
     * @return the matching constant, or {@link #NONE} if the code is out of range
     */
    public static DislodgmentCode getByCode(int code) {
        return Arrays.stream(values())
                .filter(dc -> dc.code == code)
                .findFirst()
                .orElse(NONE);
    }

    // ──────────────── Special Checks ────────────────

    /**
     * Returns whether this code causes an effect that modifies nearby entities.
     *
     * @return {@code true} for codes that affect nearby entities
     */
    public boolean isAreaEffect() {
        return this == SEVEN || this == EIGHT || this == NINE
                || this == TWENTY || this == TWENTY_TWO;
    }

    /**
     * Returns whether this code alters the entity's damage behaviour.
     *
     * @return {@code true} for damage-related codes
     */
    public boolean isDamageModifier() {
        return this == SIX || this == ELEVEN || this == TWENTY_ONE;
    }
}
