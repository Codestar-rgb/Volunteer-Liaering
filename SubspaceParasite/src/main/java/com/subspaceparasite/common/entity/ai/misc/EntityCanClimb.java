package com.subspaceparasite.common.entity.ai.misc;

/**
 * Interface for entities that can climb walls.
 */
public interface EntityCanClimb {

    /**
     * Returns the current climb status as a byte flag.
     * 0 = not climbing, 1 = climbing, 2+ = climbing with modifier
     */
    byte climbStatus();
}
