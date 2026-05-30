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
        CUSTOM_ATTACK;

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
