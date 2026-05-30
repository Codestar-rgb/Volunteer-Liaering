package com.subspaceparasite.common.entity.ai.misc;

/**
 * Interface for colony-based parasite entities.
 */
public interface EntityCanColony {

    /**
     * Whether this entity should only spawn inside colony structures.
     */
    boolean onlySpawnInside();
}
