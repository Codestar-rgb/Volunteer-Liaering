package com.subspaceparasite.api;

import java.util.Locale;

/**
 * Marker interface for parasite entities that possess special abilities.
 * <p>
 * This interface acts as a capability gate: entity implementations check
 * {@link #hasAbility(AbilityType)} to determine whether a given behaviour
 * is available. Specific ability interfaces (e.g. {@code EntityCanClimb},
 * {@code EntityCanFly}) extend or compose with this interface.
 *
 * @author SubspaceParasite Team
 * @since 1.0.0
 */
public interface ICanAbility {

    /**
     * Checks whether this entity has the specified ability.
     *
     * @param type the ability type to check
     * @return {@code true} if the ability is available and active
     */
    boolean hasAbility(AbilityType type);

    /**
     * Enumeration of all ability types that a parasite entity may possess.
     * <p>
     * Each ability corresponds to a distinct behaviour tree branch or
     * AI goal set in the original SRP mod.
     */
    enum AbilityType {

        /** Entity can climb vertical surfaces like a spider. */
        CLIMB,

        /** Entity is linked to a colony structure and can interact with it. */
        COLONY,

        /** Entity can fly or hover in the air. */
        FLY,

        /** Entity has separate body parts (multi-hitbox, like Abomination). */
        HAVE_BODIES,

        /** Entity can melt through blocks or dissolve obstacles. */
        MELT,

        /** Entity can pull nearby mobs toward itself. */
        PULL_MOBS,

        /** Entity can shoot projectiles. */
        SHOOT,

        /** Entity can spawn lesser parasites. */
        SPAWN,

        /** Entity can summon other entities (distinct from SPAWN). */
        SUMMON,

        /** Entity has enhanced swimming or aquatic movement. */
        SWIM,

        /** Entity uses directional vector-based attacks or movement. */
        VECTORS,

        /** Entity has a custom attack pattern beyond standard melee. */
        CUSTOM_ATTACK,

        // ── Combat special abilities (added for complete skill system) ──

        /** Entity can teleport short distances, typically behind a target player.
         *  Used by Enderman-type parasites and high-tier combat forms.
         *  Cooldown: 200-600 ticks based on tier. */
        TELEPORT,

        /** Entity can self-destruct, dealing AOE damage and spreading COTH/infection.
         *  Used by Bomph, Bomboo, and suicide units.
         *  Triggers when health < 25% or when near a player. */
        SELF_DESTRUCT,

        /** Entity can break blocks in its path (wood, stone, or even iron at high phases).
         *  Phase-dependent: higher phases can break harder blocks.
         *  Cooldown: 40-200 ticks. */
        BLOCK_BREAK,

        /** Entity can place parasite blocks (biomass, tendrils, hive structures).
         *  Used by Beckons and Rooter-tier entities. */
        BLOCK_PLACE,

        /** Entity can perform an AOE slam attack knocking back nearby entities.
         *  Used by Adapted-tier and above with SPECIAL_MOVE gene. */
        AOE_SLAM,

        /** Entity emits a passive healing aura that heals nearby parasites.
         *  Used by Inborn-tier and Beckons with MOB_HEALING gene. */
        HEAL_AURA,

        /** Entity emits a passive infection aura that applies COTH/Virulence
         *  to nearby non-parasite entities. Used by high-tier parasites. */
        INFECT_AURA;

        /**
         * Returns the serialisation name for this ability type.
         *
         * @return lowercase, underscore-separated name
         */
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
