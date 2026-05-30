package com.subspaceparasite.api;

import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.api.parasite.GeneType;
import com.subspaceparasite.api.parasite.ParasiteType;

/**
 * Interface marking an entity as a parasite within the SubspaceParasite system.
 * <p>
 * All parasite entities must implement this interface. It provides access to
 * the entity's type, phase information, kill count, colony origin, and the
 * gene activation system.
 *
 * @author SubspaceParasite Team
 * @since 1.0.0
 */
public interface IParasite {

    /**
     * Returns the specific parasite type of this entity.
     *
     * @return the parasite type
     */
    ParasiteType getParasiteType();

    /**
     * Returns the evolution phase in which this entity was created.
     *
     * @return the phase at creation time
     */
    EvoPhase getPhaseCreated();

    /**
     * Sets the evolution phase in which this entity was created.
     *
     * @param phase the creation phase
     */
    void setPhaseCreated(EvoPhase phase);

    /**
     * Returns the level (experience level of killed player, or similar) at
     * which this entity was created.
     *
     * @return the creation level
     */
    int getLevelCreated();

    /**
     * Sets the creation level for this entity.
     *
     * @param level the creation level
     */
    void setLevelCreated(int level);

    /**
     * Returns the total kill count accumulated by this entity.
     * <p>
     * In the original SRP mod, the kill count influences scaling and is
     * tracked as a double to allow fractional accumulation.
     *
     * @return the kill count
     */
    double getKillCount();

    /**
     * Sets the total kill count for this entity.
     *
     * @param kills the new kill count
     */
    void setKillCount(double kills);

    /**
     * Returns whether this entity was spawned by a colony structure.
     *
     * @return {@code true} if colony-spawned
     */
    boolean isColonySpawned();

    /**
     * Sets whether this entity was spawned by a colony structure.
     *
     * @param colony {@code true} if colony-spawned
     */
    void setColonySpawned(boolean colony);

    /**
     * Returns an array of all currently active genes on this entity.
     *
     * @return the active genes (may be empty, never null)
     */
    GeneType[] getActiveGenes();

    /**
     * Checks whether this entity has the specified gene activated.
     *
     * @param gene the gene to check
     * @return {@code true} if the gene is active
     */
    boolean hasGene(GeneType gene);

    /**
     * Activates the specified gene on this entity.
     * <p>
     * If the gene is already active, this call is a no-op.
     *
     * @param gene the gene to activate
     */
    void activateGene(GeneType gene);
}
