package com.subspaceparasite.common.world;

/**
 * SRP-specific difficulty setting that controls parasite stat multipliers.
 * <p>
 * The original SRP mod has exactly 4 difficulty levels that are INDEPENDENT
 * from the Evolution Phase system. Difficulty controls stat multipliers;
 * Phases control spawn tables, system unlocks, and environmental effects.
 * <p>
 * Each difficulty level provides multipliers that scale parasite combat power:
 * <ul>
 *   <li>{@link #EASY} — Halves evolution point gain. Reduces parasite knockback immunity.</li>
 *   <li>{@link #NORMAL} — Baseline, no modifications.</li>
 *   <li>{@link #HARD} — Parasites' attack power, defense, and stamina increase 4×.</li>
 *   <li>{@link #IMPOSSIBLE} — Parasites' health and attack power increase 11×,
 *       defense increases 6×, strong knockback immunity.</li>
 * </ul>
 * <p>
 * Stored per-world via {@link ModWorldData} and configurable via
 * {@code /srp difficulty set <level>}.
 */
public enum SRPDifficultySetting {

    /**
     * Easy difficulty.
     * Halves evolution point gain. Reduces parasite knockback immunity.
     * Good for players who want a mild parasite presence.
     */
    EASY(1, 1.0f, 0.5f, 0.75f, 1.0f, 1.0f, 1.0f, false),

    /**
     * Normal difficulty — the intended default experience.
     * Baseline, no modifications.
     */
    NORMAL(2, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, false),

    /**
     * Hard difficulty.
     * Parasites' attack power, defense, and stamina increase 4×.
     */
    HARD(3, 1.0f, 1.0f, 1.0f, 4.0f, 4.0f, 4.0f, false),

    /**
     * Impossible difficulty.
     * Parasites' health and attack power increase 11×, defense increases 6×,
     * strong knockback immunity. For players who want the ultimate challenge.
     */
    IMPOSSIBLE(4, 1.0f, 1.0f, 1.0f, 11.0f, 11.0f, 6.0f, true);

    /** Numeric ID for serialization. */
    private final int id;

    /** Multiplier applied to parasite natural spawn rates. */
    private final float spawnRateMultiplier;

    /** Multiplier applied to evolution point gain rate. */
    private final float evolutionRateMultiplier;

    /** Multiplier applied to COTH infection spread chance. */
    private final float infectionRateMultiplier;

    /** Multiplier applied to parasite health. */
    private final float healthMultiplier;

    /** Multiplier applied to parasite attack power. */
    private final float attackMultiplier;

    /** Multiplier applied to parasite defense/armor. */
    private final float defenseMultiplier;

    /** Whether parasites can break blocks at this difficulty. */
    private final boolean parasitesBreakBlocks;

    SRPDifficultySetting(int id, float spawnRateMultiplier, float evolutionRateMultiplier,
                         float infectionRateMultiplier, float healthMultiplier,
                         float attackMultiplier, float defenseMultiplier,
                         boolean parasitesBreakBlocks) {
        this.id = id;
        this.spawnRateMultiplier = spawnRateMultiplier;
        this.evolutionRateMultiplier = evolutionRateMultiplier;
        this.infectionRateMultiplier = infectionRateMultiplier;
        this.healthMultiplier = healthMultiplier;
        this.attackMultiplier = attackMultiplier;
        this.defenseMultiplier = defenseMultiplier;
        this.parasitesBreakBlocks = parasitesBreakBlocks;
    }

    // ──────────────── Accessors ────────────────

    public int getId() { return id; }

    public float getSpawnRateMultiplier() { return spawnRateMultiplier; }

    public float getEvolutionRateMultiplier() { return evolutionRateMultiplier; }

    public float getInfectionRateMultiplier() { return infectionRateMultiplier; }

    /** Returns the multiplier applied to parasite max health. */
    public float getHealthMultiplier() { return healthMultiplier; }

    /** Returns the multiplier applied to parasite attack damage. */
    public float getAttackMultiplier() { return attackMultiplier; }

    /** Returns the multiplier applied to parasite defense/armor. */
    public float getDefenseMultiplier() { return defenseMultiplier; }

    public boolean canParasitesBreakBlocks() { return parasitesBreakBlocks; }

    /**
     * Returns whether parasites have strong knockback immunity at this difficulty.
     * Only IMPOSSIBLE difficulty grants strong knockback immunity.
     *
     * @return true if parasites have strong knockback immunity
     */
    public boolean hasKnockbackImmunity() {
        return this == IMPOSSIBLE;
    }

    // ──────────────── Backward-Compatible Rate Accessors ────────────────
    // These return 1.0 for all difficulties because the original SRP does not
    // scale merge/colony/celestial/meteor rates by difficulty — those are
    // controlled by the Evolution Phase system instead.

    /**
     * Returns the merge rate multiplier (always 1.0; merge rate is phase-controlled).
     */
    public float getMergeRateMultiplier() { return 1.0f; }

    /**
     * Returns the colony growth rate multiplier (always 1.0; colony growth is phase-controlled).
     */
    public float getColonyGrowthRateMultiplier() { return 1.0f; }

    /**
     * Returns the celestial event frequency multiplier (always 1.0; celestial frequency is phase-controlled).
     */
    public float getCelestialFrequencyMultiplier() { return 1.0f; }

    /**
     * Returns the meteor frequency multiplier (always 1.0; meteor frequency is phase-controlled).
     */
    public float getMeteorFrequencyMultiplier() { return 1.0f; }

    // ──────────────── Queries ────────────────

    /**
     * Returns whether natural parasite spawning is allowed at this difficulty.
     * All SRP difficulties allow natural spawning.
     *
     * @return true (always, since all 4 SRP difficulties allow spawning)
     */
    public boolean allowsNaturalSpawning() {
        return true;
    }

    /**
     * Returns the maximum meteor size allowed at this difficulty.
     * <ul>
     *   <li>EASY: small (1) only</li>
     *   <li>NORMAL: up to medium (2)</li>
     *   <li>HARD: up to large (3)</li>
     *   <li>IMPOSSIBLE: up to large (3)</li>
     * </ul>
     *
     * @return maximum meteor size (1=small, 2=medium, 3=large)
     */
    public int getMaxMeteorSize() {
        return switch (this) {
            case EASY -> 1;
            case NORMAL -> 2;
            case HARD, IMPOSSIBLE -> 3;
        };
    }

    /**
     * Returns the minimum evolution phase required for meteors to fall.
     * Higher difficulty = meteors can start falling earlier.
     *
     * @return minimum phase number for meteor events
     */
    public int getMinMeteorPhase() {
        return switch (this) {
            case IMPOSSIBLE -> 1;  // Phase 1 — very early
            case HARD       -> 2;  // Phase 2
            case NORMAL     -> 3;  // Phase 3
            case EASY       -> 4;  // Phase 4 — late
        };
    }

    /**
     * Returns the base meteor spawn chance per tick (before phase/difficulty scaling).
     *
     * @return base chance per tick for a meteor to spawn
     */
    public float getBaseMeteorChance() {
        return switch (this) {
            case EASY       -> 0.00001f;
            case NORMAL     -> 0.00003f;
            case HARD       -> 0.0001f;
            case IMPOSSIBLE -> 0.0003f;
        };
    }

    /**
     * Returns whether large meteors create permanent infestation zones.
     * Only at HARD and IMPOSSIBLE difficulty.
     *
     * @return true if large meteors create permanent infestation
     */
    public boolean largeMeteorsCreatePermanentInfestation() {
        return this == HARD || this == IMPOSSIBLE;
    }

    // ──────────────── Lookup ────────────────

    /**
     * Retrieves the difficulty setting matching the given ID.
     *
     * @param id the numeric ID (1–4)
     * @return the matching setting, or {@link #NORMAL} if out of range
     */
    public static SRPDifficultySetting getById(int id) {
        for (SRPDifficultySetting setting : values()) {
            if (setting.id == id) return setting;
        }
        return NORMAL;
    }

    /**
     * Retrieves a difficulty setting by name (case-insensitive).
     *
     * @param name the setting name
     * @return the matching setting, or {@link #NORMAL} if not found
     */
    public static SRPDifficultySetting getByName(String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NORMAL;
        }
    }

    /**
     * Returns the display name for translation keys.
     *
     * @return the lowercase name for use in translation keys
     */
    public String getTranslationKey() {
        return "subspaceparasite.difficulty." + name().toLowerCase();
    }
}
