package com.subspaceparasite.api.parasite;

import java.util.Arrays;

/**
 * Represents the global evolution phases (0–10) that control the parasite
 * ecosystem progression. Each phase modifies entity stats, spawn rates,
 * awareness range, and atmospheric fog density.
 * <p>
 * The phase value {@code NONE (-1)} indicates that no phase system is active.
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
     * Phase 0 — Initial state. No natural spawns; only colony-spawned parasites.
     */
    ZERO(0,  0.00f, 0.10f, 16.0f, 0.000f),

    /**
     * Phase 1 — Low activity. Basic infected may spawn naturally.
     */
    ONE(1,   0.05f, 0.25f, 20.0f, 0.005f),

    /**
     * Phase 2 — Mild escalation. Feral and inborn types begin appearing.
     */
    TWO(2,   0.10f, 0.50f, 24.0f, 0.010f),

    /**
     * Phase 3 — Moderate escalation. Crude and primitive forms spawn.
     */
    THREE(3, 0.15f, 0.75f, 28.0f, 0.020f),

    /**
     * Phase 4 — Significant escalation. Adapted forms begin appearing.
     */
    FOUR(4,  0.20f, 1.00f, 32.0f, 0.035f),

    /**
     * Phase 5 — High escalation. Pure types and nexus structures expand.
     */
    FIVE(5,  0.30f, 1.50f, 40.0f, 0.050f),

    /**
     * Phase 6 — Severe escalation. Preeminent types appear.
     */
    SIX(6,   0.40f, 2.00f, 48.0f, 0.075f),

    /**
     * Phase 7 — Critical escalation. Ancient-tier entities may emerge.
     */
    SEVEN(7, 0.50f, 3.00f, 56.0f, 0.100f),

    /**
     * Phase 8 — Extreme escalation. Derived and special forms appear.
     */
    EIGHT(8, 0.65f, 4.00f, 64.0f, 0.130f),

    /**
     * Phase 9 — Near-maximum. Abomination precursors spawn.
     */
    NINE(9,  0.80f, 5.00f, 72.0f, 0.165f),

    /**
     * Phase 10 — Maximum escalation. Abominations; world under full siege.
     */
    TEN(10,  1.00f, 7.00f, 80.0f, 0.200f);

    /* ================================================================ */

    /** The phase number (0–10), or -1 for {@link #NONE}. */
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
     * @param number the phase number (-1 to 10)
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
     * <p>
     * This scales linearly with the phase number, starting at 0 for phase 0
     * and reaching 100 at phase 10.
     *
     * @return bonus health for colony-origin entities
     */
    public float getPhaseOriginBonusHealth() {
        if (phaseNumber <= 0) return 0.0f;
        return phaseNumber * 10.0f;
    }

    /**
     * Returns a penalty value applied to non-parasite entities at this phase.
     * <p>
     * This is used to reduce the effectiveness of non-parasite mobs
     * as the parasite ecosystem grows stronger.
     *
     * @return penalty value (0.0 for low phases, increasing at higher phases)
     */
    public float getPhaseOriginPenalty() {
        if (phaseNumber < 4) return 0.0f;
        return (phaseNumber - 3) * 0.05f;
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
     * Returns the next phase in the sequence, or this phase if already at maximum.
     *
     * @return the next phase, or {@code this} if at TEN
     */
    public EvoPhase next() {
        if (this == TEN || this == NONE) return this;
        return getByPhaseNumber(phaseNumber + 1);
    }

    /**
     * Returns the previous phase in the sequence, or this phase if at NONE/ZERO.
     *
     * @return the previous phase, or {@code this} if at NONE or ZERO
     */
    public EvoPhase previous() {
        if (this == NONE || this == ZERO) return this;
        return getByPhaseNumber(phaseNumber - 1);
    }
}
