package com.subspaceparasite.common.entity.ai.misc;

/**
 * Interface for entities that can spawn other entities with ID/data tracking.
 */
public interface EntityCanSpawn {

    /**
     * Get the spawn ID from the entity's current data.
     * Returns -1 if cannot spawn.
     */
    int canSpawnByIDData();

    /**
     * Get the ID to spawn.
     */
    int getIDSpawn();
}
