package com.subspaceparasite.api.parasite;

import java.util.Arrays;

/**
 * Represents the global evolution phases (0–4) that control the parasite
 * ecosystem progression, matching the original Scape and Run: Parasites mod.
 * <p>
 * Each phase modifies entity stats, spawn rates, awareness range,
 * evolution speed, colony growth, infection spread, and atmospheric fog density.
 * <p>
 * The phase system is INDEPENDENT from the difficulty system
 * ({@link com.subspaceparasite.common.world.SRPDifficultySetting}).
 * Phases control spawn tables, system unlocks, and environmental effects.
 * Difficulty controls stat multipliers.
 * <p>
 * The original SRP has 13 phases (-2 to 10). For current implementation scope,
 * we use 6 values:
 * <ul>
 *   <li>NONE (-1) — Sentinel value, no phase active</li>
 *   <li>PHASE_0 (0) — Pre-assimilation: No natural spawns; only colony-spawned parasites</li>
 *   <li>PHASE_1 (1) — Basic infected may spawn naturally; primitive combat forms appear</li>
 *   <li>PHASE_2 (2) — Feral, crude, and adapted types begin appearing</li>
 *   <li>PHASE_3 (3) — Inborn, deterrent, and higher-tier forms spawn</li>
 *   <li>PHASE_4 (4) — Pure and ancient-tier entities emerge. World under full siege</li>
 * </ul>
 *
 * @author SubspaceParasite Team
 * @since 1.0.0
 */
public enum EvoPhase {

    /**
     * No phase active — used as a sentinel value.
     */
    NONE(-1, 0.00f, 0.00f, 0.0f,  0.000f),

    /**
     * Phase 0 — Pre-assimilation / None.
     * No natural spawns; only colony-spawned parasites.
     * Parasites are at their weakest.
     */
    PHASE_0(0,  0.00f, 0.10f, 16.0f, 0.000f),

    /**
     * Phase 1.
     * Parasites are weak, slow evolution, minimal colony activity.
     * Basic infected may spawn naturally; primitive combat forms appear.
     */
    PHASE_1(1,  0.10f, 0.30f, 20.0f, 0.010f),

    /**
     * Phase 2.
     * Moderate evolution speed, some special abilities unlock.
     * Feral, crude, and adapted types begin appearing.
     * Colony growth accelerates.
     */
    PHASE_2(2,  0.25f, 0.60f, 28.0f, 0.030f),

    /**
     * Phase 3.
     * Fast evolution, aggressive colonies, special skills active.
     * Inborn, deterrent, and higher-tier forms spawn.
     * COTH spreading is significant.
     */
    PHASE_3(3,  0.50f, 1.20f, 40.0f, 0.070f),

    /**
     * Phase 4.
     * Maximum escalation, all parasite abilities active, relentless assault.
     * Pure and ancient-tier entities emerge. World under full siege.
     */
    PHASE_4(4,  1.00f, 2.00f, 56.0f, 0.150f);

    /* ================================================================ */

    /** The phase number (0–4), or -1 for {@link #NONE}. */
    private final int phaseNumber;

    /** Percentage bonus applied to entity base stats at this phase. */
    private final float entityStatBonus;

    /** Multiplier applied to parasite natural spawn rates at this phase. */
    private final float spawnRateMultiplier;

    /** Range (in blocks) at which parasites become aware of non-parasite entities. */
    private final float awarenessRange;

    /** Fog density value for atmospheric rendering at this phase. */
    private final float fogDensity;

    EvoPhase(int phaseNumber, float entityStatBonus, float spawnRateMultiplier,
             float awarenessRange, float fogDensity) {
        this.phaseNumber = phaseNumber;
        this.entityStatBonus = entityStatBonus;
        this.spawnRateMultiplier = spawnRateMultiplier;
        this.awarenessRange = awarenessRange;
        this.fogDensity = fogDensity;
    }

    // ──────────────── Accessors ────────────────

    public int getPhaseNumber()              { return phaseNumber; }
    public float getEntityStatBonus()        { return entityStatBonus; }
    public float getSpawnRateMultiplier()    { return spawnRateMultiplier; }
    public float getAwarenessRange()         { return awarenessRange; }
    public float getFogDensity()             { return fogDensity; }

    // ──────────────── Lookup ────────────────

    /**
     * Retrieves the {@code EvoPhase} matching the given phase number.
     *
     * @param number the phase number (-1 to 4)
     * @return the matching phase, or {@link #NONE} if the number is out of range
     */
    public static EvoPhase getByPhaseNumber(int number) {
        return Arrays.stream(values())
                .filter(p -> p.phaseNumber == number)
                .findFirst()
                .orElse(NONE);
    }

    // ──────────────── Derived Calculations ────────────────

    /**
     * Returns the bonus health granted to colony-origin parasites at this phase.
     * Scales linearly: Phase 0 = 0, Phase 1 = 10, Phase 2 = 20, Phase 3 = 30, Phase 4 = 40.
     *
     * @return bonus health for colony-origin entities
     */
    public float getPhaseOriginBonusHealth() {
        if (phaseNumber <= 0) return 0.0f;
        return phaseNumber * 10.0f;
    }

    /**
     * Returns a penalty value applied to non-parasite entities at this phase.
     * Only applies at Phase 3 and above — parasites have become
     * dominant enough to actively suppress non-parasite life.
     *
     * @return penalty value (0.0 for phases 0-2, increasing at phases 3-4)
     */
    public float getPhaseOriginPenalty() {
        if (phaseNumber < 3) return 0.0f;
        return (phaseNumber - 2) * 0.1f;  // Phase 3 = 0.1, Phase 4 = 0.2
    }

    /**
     * Returns the bonus COTH spread rate at this phase.
     * Higher phases increase the rate at which Call of the Hive spreads.
     * Phase 0 = 0.0, Phase 1 = 0.02, ..., Phase 4 = 0.08
     *
     * @return COTH spread rate bonus
     */
    public float getCOTHSpreadRateBonus() {
        return phaseNumber * 0.02f;
    }

    /**
     * Returns the infection rate bonus at this phase.
     * Higher phases increase the probability of successful infection.
     * Phase 0 = 0.0, Phase 1 = 0.025, ..., Phase 4 = 0.10
     *
     * @return infection rate bonus
     */
    public float getInfectionRateBonus() {
        return phaseNumber * 0.025f;
    }

    /**
     * Returns the evolution point gain rate bonus at this phase.
     * Higher phases accelerate evolution point accumulation.
     * Phase 0 = 1.0x, Phase 1 = 1.25x, ..., Phase 4 = 2.0x
     *
     * @return evolution rate bonus multiplier
     */
    public float getEvolutionRateBonus() {
        return 1.0f + phaseNumber * 0.25f;
    }

    /**
     * Returns the merge probability bonus at this phase.
     * Higher phases increase the chance of parasites merging.
     * Phase 0 = 0.0, Phase 1 = 0.01, ..., Phase 4 = 0.04
     *
     * @return merge probability bonus
     */
    public float getMergeProbabilityBonus() {
        return phaseNumber * 0.01f;
    }

    /**
     * Returns the colony growth rate bonus at this phase.
     * Higher phases accelerate colony expansion and building.
     * Phase 0 = 1.0x, Phase 1 = 1.2x, ..., Phase 4 = 1.8x
     *
     * @return colony growth rate bonus
     */
    public float getColonyGrowthRateBonus() {
        return 1.0f + phaseNumber * 0.2f;
    }

    /**
     * Returns the awareness range bonus at this phase.
     * Parasites become more perceptive at higher phases.
     * Phase 0 = 0, Phase 1 = 4, ..., Phase 4 = 16
     *
     * @return additional awareness range in blocks
     */
    public float getAwarenessRangeBonus() {
        return phaseNumber * 4.0f;
    }

    /**
     * Returns the spawn rate bonus at this phase.
     * Convenience method that returns the same value as spawnRateMultiplier.
     *
     * @return spawn rate multiplier bonus
     */
    public float getSpawnRateBonus() {
        return spawnRateMultiplier;
    }

    /**
     * Returns whether special skills are available at this phase.
     * In the original SRP, special skills (AOE slam, leap, etc.)
     * only unlock at Phase 2 and above.
     *
     * @return true if special skills are available
     */
    public boolean areSpecialSkillsAvailable() {
        return phaseNumber >= 2;
    }

    /**
     * Returns whether all parasite abilities are active at this phase.
     * In the original SRP, all abilities only activate at Phase 4.
     *
     * @return true if all abilities are active
     */
    public boolean areAllAbilitiesActive() {
        return phaseNumber >= 4;
    }

    /**
     * Checks whether this phase is at or above the given phase.
     *
     * @param other the phase to compare against
     * @return {@code true} if this phase number &ge; other's phase number
     */
    public boolean isAtLeast(EvoPhase other) {
        return this.phaseNumber >= other.phaseNumber;
    }

    /**
     * Checks whether this phase is strictly before (lower than) the given phase.
     *
     * @param other the phase to compare against
     * @return {@code true} if this phase number &lt; other's phase number
     */
    public boolean isBefore(EvoPhase other) {
        return this.phaseNumber < other.phaseNumber;
    }

    /**
     * Returns the next phase in the sequence, or this phase if already at maximum.
     *
     * @return the next phase, or {@code this} if at PHASE_4
     */
    public EvoPhase next() {
        if (this == PHASE_4 || this == NONE) return this;
        return getByPhaseNumber(phaseNumber + 1);
    }

    /**
     * Returns the previous phase in the sequence, or this phase if at NONE/PHASE_0.
     *
     * @return the previous phase, or {@code this} if at NONE or PHASE_0
     */
    public EvoPhase previous() {
        if (this == NONE || this == PHASE_0) return this;
        return getByPhaseNumber(phaseNumber - 1);
    }

    /**
     * Returns the human-readable stage name for this phase.
     * The original SRP phases don't have special names — they're just
     * numbered phases.
     *
     * @return stage name matching the original SRP
     */
    public String getStageName() {
        return switch (phaseNumber) {
            case -1 -> "None";
            case 0  -> "Phase 0";
            case 1  -> "Phase 1";
            case 2  -> "Phase 2";
            case 3  -> "Phase 3";
            case 4  -> "Phase 4";
            default -> "Unknown";
        };
    }

    /**
     * Returns the maximum phase number.
     *
     * @return 4 (the maximum phase)
     */
    public static int getMaxPhaseNumber() {
        return 4;
    }
}
