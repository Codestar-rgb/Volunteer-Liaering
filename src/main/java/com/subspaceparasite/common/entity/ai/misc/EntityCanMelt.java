package com.subspaceparasite.common.entity.ai.misc;

/**
 * Interface for entities that can melt/phase through blocks.
 */
public interface EntityCanMelt {

    /**
     * Begin melting phase.
     */
    void melt();

    /**
     * Whether this entity is currently melting.
     */
    boolean isMelting();

    /**
     * Get the target height when melting (for collision adjustment).
     */
    float getTHeight();

    /**
     * Get the adjusted size when melting.
     */
    float getASize();
}
