package com.subspaceparasite.common.entity.ai.misc;

import net.minecraft.world.entity.Entity;

/**
 * Interface for entities that can shoot projectiles.
 */
public interface EntityCanShoot {

    /**
     * Create a projectile entity with the given velocity.
     *
     * @param d1 X velocity component
     * @param d3 Y velocity component
     * @param d5 Z velocity component
     * @return The created projectile entity, or null if creation failed
     */
    Entity getProj(double d1, double d3, double d5);

    /**
     * Play the sound effect when firing a projectile.
     */
    void playProjSound();
}
