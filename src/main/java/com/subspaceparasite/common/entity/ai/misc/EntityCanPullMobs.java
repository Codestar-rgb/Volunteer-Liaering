package com.subspaceparasite.common.entity.ai.misc;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Interface for entities that can pull mobs toward them.
 * Used by deterrent-class parasites with attraction abilities.
 * Supports multi-target tracking (original SRP uses 3 target slots).
 */
public interface EntityCanPullMobs {

    /**
     * Whether this entity currently has a targeted entity for pulling.
     */
    boolean hasTargetedEntity();

    /**
     * Set the entity ID being targeted for pull (primary target slot 0).
     */
    void setTargetedEntity(int entityId);

    /**
     * Get the entity ID being targeted for pull (primary target slot 0).
     * Returns -1 if no target.
     */
    int getTargetedEntity();

    /**
     * Set a specific target slot with an entity.
     * Original SRP uses 3 target slots: TARGET_ENTITY, TARGET_ENTITY1, TARGET_ENTITY2
     * 
     * @param slot The target slot index (0-2)
     * @param entity The entity to target, or null to clear
     */
    void setTargetedEntity(int slot, @Nullable LivingEntity entity);

    /**
     * Get the list of all targeted entities for pulling.
     * Returns up to 3 targets as per original SRP design.
     * 
     * @return List of targeted entities
     */
    List<LivingEntity> getTargetedEntityVictims();

    /**
     * Get a specific target by slot index.
     * 
     * @param slot The target slot index (0-2)
     * @return The entity in that slot, or null if empty
     */
    @Nullable
    LivingEntity getTargetedEntity(int slot);

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

    /**
     * Update and clean invalid targets (dead, out of range, etc.).
     * Should be called periodically to maintain target validity.
     */
    void updateTargets();
}
