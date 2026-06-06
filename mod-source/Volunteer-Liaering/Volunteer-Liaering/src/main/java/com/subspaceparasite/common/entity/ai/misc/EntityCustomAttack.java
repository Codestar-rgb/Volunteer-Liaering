package com.subspaceparasite.common.entity.ai.misc;

import net.minecraft.world.entity.Entity;

/**
 * Interface for entities with custom AOE attack capability.
 */
public interface EntityCustomAttack {

    /**
     * Perform an AOE attack against the given entity.
     *
     * @param target The primary target entity
     * @return true if the attack was successfully performed
     */
    boolean attackEntityAsMobAOE(Entity target);
}
