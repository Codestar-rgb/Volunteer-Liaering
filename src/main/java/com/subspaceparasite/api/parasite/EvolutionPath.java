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
     * Chain: INFECTED → SPECIAL_INFECTED
     */
    INFECTED         ("Infected",         0,  true,  "SPECIAL_INFECTED"),

    /**
     * Marauder-type infected — stronger mimic variants.
     * A sub-branch of Infected that can further evolve into Feral.
     * Chain: SPECIAL_INFECTED → FERAL
     */
    SPECIAL_INFECTED ("Special Infected",  1,  true,  "FERAL"),

    /** Fully transformed host bodies — no longer disguised.
     * Chain: FERAL → HIJACKED (for non-standard hosts)
     */
    FERAL            ("Feral",             2,  true,  "HIJACKED"),

    /** Non-standard hosts whose abilities are co-opted.
     * Chain: HIJACKED → PRIMITIVE (becomes a colony combat form)
     */
    HIJACKED         ("Hijacked",          3,  true,  "PRIMITIVE"),

    /** Parasites born from the colony — not converted hosts.
     * Chain: INBORN → CRUDE
     */
    INBORN           ("Inborn",            4,  true,  "CRUDE"),

    /** Early colony structures and immature forms.
     * Chain: CRUDE → PRIMITIVE
     */
    CRUDE            ("Crude",             5,  true,  "PRIMITIVE"),

    /** First true combat forms; colony defenders.
     * Chain: PRIMITIVE → ADAPTED
     */
    PRIMITIVE        ("Primitive",         6,  true,  "ADAPTED"),

    /** Evolved primitives — stronger in every stat.
     * Chain: ADAPTED → PURE
     */
    ADAPTED          ("Adapted",           7,  true,  "PURE"),

    /** Colony nexus structures (Beckon, Dispatcher, Rooter, etc.).
     * Chain: NEXUS → DETERRENT
     */
    NEXUS            ("Nexus",             8,  true,  "DETERRENT"),

    /** Area-denial parasites that debilitate nearby threats.
     * Chain: DETERRENT → PURE
     */
    DETERRENT        ("Deterrent",         9,  true,  "PURE"),

    /** Fully self-actualised combat parasites.
     * Chain: PURE → PREEMINENT
     */
    PURE             ("Pure",             10,  true,  "PREEMINENT"),

    /** Elite combat forms — commanders of lesser parasites.
     * Chain: PREEMINENT → ANCIENT
     */
    PREEMINENT       ("Preeminent",       11,  true,  "ANCIENT"),

    /** Endgame entities — the oldest and most powerful forms.
     * Chain: ANCIENT → DERIVED (special offshoot path)
     */
    ANCIENT          ("Ancient",          12,  true,  "DERIVED"),

    /** Special evolutionary offshoots.
     * Chain: DERIVED → ABOMINATION (ultimate form)
     */
    DERIVED          ("Derived",          13,  true,  "ABOMINATION"),

    /** Multi-part boss entities — the ultimate evolution. Terminal path. */
    ABOMINATION      ("Abomination",      14,  false, null);

    /* ================================================================ */

    /** Human-readable display name for localisation. */
    private final String displayName;

    /** Tier level (0 = lowest, 14 = highest). */
    private final int tierLevel;

    /** Whether entities on this path can evolve further. */
    private final boolean canEvolveFurther;

    /** The next evolution path name, or {@code null} if this is a terminal path. Lazily resolved. */
    @Nullable
    private final String nextPathName;

    EvolutionPath(String displayName, int tierLevel, boolean canEvolveFurther,
                  @Nullable String nextPathName) {
        this.displayName = displayName;
        this.tierLevel = tierLevel;
        this.canEvolveFurther = canEvolveFurther;
        this.nextPathName = nextPathName;
    }

    // ──────────────── Accessors ────────────────

    public String getDisplayName()       { return displayName; }
    public int getTierLevel()            { return tierLevel; }
    public boolean canEvolveFurther()    { return canEvolveFurther; }

    /**
     * Returns the next evolution path in the progression chain.
     * Lazily resolves the path by name to avoid illegal forward references
     * in enum constructors.
     *
     * @return the next path, or {@code null} if this is terminal
     */
    @Nullable
    public EvolutionPath getNextPath() {
        if (nextPathName == null) return null;
        try {
            return EvolutionPath.valueOf(nextPathName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

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
