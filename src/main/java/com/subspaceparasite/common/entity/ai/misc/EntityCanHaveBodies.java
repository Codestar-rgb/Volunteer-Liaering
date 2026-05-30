package com.subspaceparasite.common.entity.ai.misc;

import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

/**
 * Interface for entities that can have multiple body segments/parts.
 * Used by worm-like or serpentine parasites.
 */
public interface EntityCanHaveBodies {

    /**
     * Set whether this entity is following its parent body.
     */
    void setFollowing(boolean following);

    /**
     * Whether this entity is currently following.
     */
    boolean isFollowing();

    /**
     * Get the total length of the body chain.
     */
    int getBodyLength();

    /**
     * Get another body part in the chain by index offset.
     */
    @Nullable
    Entity getAnotherBody(int offset);

    /**
     * Get this body part's number in the chain.
     */
    int getBodyNumber();

    /**
     * Set this body part's number in the chain.
     */
    void setBodyNumber(int number);

    /**
     * Get the head/lead entity of the body chain.
     */
    @Nullable
    Entity getHeadEntity();

    /**
     * Set the head/lead entity of the body chain.
     */
    void setHeadEntity(@Nullable Entity head);

    /**
     * Update body chain positioning.
     */
    void updateBodyChain();
}
