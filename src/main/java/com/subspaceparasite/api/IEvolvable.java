package com.subspaceparasite.api;

import com.subspaceparasite.api.parasite.EvolutionPath;
import com.subspaceparasite.api.parasite.ParasiteType;

/**
 * Interface for entities that can evolve through the One Mind evolution system.
 * <p>
 * Evolution points are accumulated via kills and contributions to the colony.
 * Once enough points are gathered, an entity may transition to a higher
 * {@link ParasiteType} along its {@link EvolutionPath}.
 *
 * @author SubspaceParasite Team
 * @since 1.0.0
 */
public interface IEvolvable {

    /**
     * Returns the current evolution point total for this entity.
     *
     * @return the accumulated evolution points
     */
    int getEvolutionPoints();

    /**
     * Adds evolution points to this entity's total.
     *
     * @param points the number of evolution points to add (must be &ge; 0)
     */
    void addEvolutionPoints(int points);

    /**
     * Checks whether this entity can currently evolve to the given type.
     * <p>
     * This checks evolution point sufficiency, path compatibility,
     * and any additional requirements the specific entity may have.
     *
     * @param type the target parasite type
     * @return {@code true} if evolution to the given type is possible
     */
    boolean canEvolveTo(ParasiteType type);

    /**
     * Returns the evolution point threshold required to evolve to the given type.
     *
     * @param type the target parasite type
     * @return the number of evolution points needed
     */
    float getEvolutionThreshold(ParasiteType type);

    /**
     * Returns the current evolution path for this entity.
     *
     * @return the evolution path
     */
    EvolutionPath getEvolutionPath();

    /**
     * Sets the evolution path for this entity.
     * <p>
     * Changing the path may affect which evolution targets are available.
     *
     * @param path the new evolution path
     */
    void setEvolutionPath(EvolutionPath path);

    /**
     * Gets the evolution phase at which this entity was created/spawned.
     *
     * @return the creation phase
     */
    com.subspaceparasite.api.parasite.EvoPhase getPhaseCreated();

    /**
     * Sets the evolution phase at which this entity was created/spawned.
     *
     * @param phase the new creation phase
     */
    void setPhaseCreated(com.subspaceparasite.api.parasite.EvoPhase phase);
}
