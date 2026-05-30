package com.subspaceparasite.common.entity.ai.misc;

import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

/**
 * Interface for entities that can pull mobs toward them.
 * Used by deterrent-class parasites with attraction abilities.
 */
public interface EntityCanPullMobs {

    /**
     * Whether this entity currently has a targeted entity for pulling.
     */
    boolean hasTargetedEntity();

    /**
     * Set the entity ID being targeted for pull.
     */
    void setTargetedEntity(int entityId);

    /**
     * Get the entity ID being targeted for pull.
     * Returns -1 if no target.
     */
    int getTargetedEntity();

    /**
     * Check if the attack target is valid for pulling.
     */
    boolean checkAttackTarget();

    /**
     * Reset the pull skill state.
     */
    void resetPullSkill();

    /**
     * Get the acceleration factor for pulling.
     */
    double getAcceleration();
}
