package com.subspaceparasite.common.entity.ai.misc;

import net.minecraft.world.damagesource.DamageSource;

/**
 * Interface for multi-part entities with individually damageable body parts.
 * Uses PartEntity in 1.20.1 (not MultiPartEntityPart from 1.12).
 */
public interface EntityBodyParts {

    /**
     * Attack a specific body part.
     *
     * @param source    The damage source
     * @param amount    The damage amount
     * @param partIndex The body part index
     * @param bypass    Whether to bypass armor
     * @return true if the attack succeeded
     */
    boolean attackEntityBodyFrom(DamageSource source, float amount, int partIndex, boolean bypass);

    /**
     * Mark a body part as dead.
     *
     * @param partIndex The body part index to kill
     */
    void setBodyPartDead(int partIndex);

    /**
     * Get the number of body parts.
     */
    int getBodyPartCount();

    /**
     * Check if a specific body part is alive.
     */
    boolean isBodyPartAlive(int partIndex);
}
