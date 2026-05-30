package com.subspaceparasite.api.parasite;

import javax.annotation.Nullable;

/**
 * Represents the major evolutionary paths a parasite can follow, ordered by
 * increasing tier level. Each path defines a progression branch within the
 * One Mind evolution system from the original Scape and Run: Parasites mod.
 *
 * @author SubspaceParasite Team
 * @since 1.0.0
 */
public enum EvolutionPath {

    /**
     * Mimic-stage parasites that retain the appearance of their host.
     * Includes both base Infected and Special Infected (Marauder) subtypes.
     */
    INFECTED         ("Infected",         0,  true,  null),

    /**
     * Marauder-type infected — stronger mimic variants.
     * A sub-branch of Infected that can further evolve into Feral.
     */
    SPECIAL_INFECTED ("Special Infected",  1,  true,  null),

    /** Fully transformed host bodies — no longer disguised. */
    FERAL            ("Feral",             2,  true,  null),

    /** Non-standard hosts whose abilities are co-opted. */
    HIJACKED         ("Hijacked",          3,  true,  null),

    /** Parasites born from the colony — not converted hosts. */
    INBORN           ("Inborn",            4,  true,  null),

    /** Early colony structures and immature forms. */
    CRUDE            ("Crude",             5,  true,  null),

    /** First true combat forms; colony defenders. */
    PRIMITIVE        ("Primitive",         6,  true,  null),

    /** Evolved primitives — stronger in every stat. */
    ADAPTED          ("Adapted",           7,  true,  null),

    /** Colony nexus structures (Beckon, Dispatcher, Rooter, etc.). */
    NEXUS            ("Nexus",             8,  true,  null),

    /** Area-denial parasites that debilitate nearby threats. */
    DETERRENT        ("Deterrent",         9,  true,  null),

    /** Fully self-actualised combat parasites. */
    PURE             ("Pure",             10,  true,  null),

    /** Elite combat forms — commanders of lesser parasites. */
    PREEMINENT       ("Preeminent",       11,  true,  null),

    /** Endgame entities — the oldest and most powerful forms. */
    ANCIENT          ("Ancient",          12,  true,  null),

    /** Special evolutionary offshoots. */
    DERIVED          ("Derived",          13,  false, null),

    /** Multi-part boss entities — the ultimate evolution. */
    ABOMINATION      ("Abomination",      14,  false, null);

    /* ================================================================ */

    /** Human-readable display name for localisation. */
    private final String displayName;

    /** Tier level (0 = lowest, 14 = highest). */
    private final int tierLevel;

    /** Whether entities on this path can evolve further. */
    private final boolean canEvolveFurther;

    /** The next evolution path, or {@code null} if this is a terminal path. */
    @Nullable
    private final EvolutionPath nextPath;

    EvolutionPath(String displayName, int tierLevel, boolean canEvolveFurther,
                  @Nullable EvolutionPath nextPath) {
        this.displayName = displayName;
        this.tierLevel = tierLevel;
        this.canEvolveFurther = canEvolveFurther;
        this.nextPath = nextPath;
    }

    // ──────────────── Accessors ────────────────

    public String getDisplayName()       { return displayName; }
    public int getTierLevel()            { return tierLevel; }
    public boolean canEvolveFurther()    { return canEvolveFurther; }

    /**
     * Returns the next evolution path in the progression chain.
     *
     * @return the next path, or {@code null} if this is terminal
     */
    @Nullable
    public EvolutionPath getNextPath()   { return nextPath; }

    /**
     * Checks whether this path is at or above the given tier level.
     *
     * @param minimumTier the tier to compare against
     * @return {@code true} if this path's tier level is &ge; minimumTier
     */
    public boolean isAtLeastTier(int minimumTier) {
        return tierLevel >= minimumTier;
    }

    /**
     * Checks whether this path is a nexus-type path (colony structures).
     *
     * @return {@code true} if this is the NEXUS path
     */
    public boolean isNexusPath() {
        return this == NEXUS;
    }

    /**
     * Checks whether this path represents a boss-level entity.
     *
     * @return {@code true} for ANCIENT, DERIVED, and ABOMINATION paths
     */
    public boolean isBossTier() {
        return this == ANCIENT || this == DERIVED || this == ABOMINATION;
    }
}
