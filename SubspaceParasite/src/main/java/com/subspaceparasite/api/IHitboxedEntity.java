package com.subspaceparasite.api;

import net.minecraft.world.entity.Entity;

/**
 * Interface for multi-part hitbox entities in the SubspaceParasite system.
 * <p>
 * In Minecraft 1.20.1, multi-part entities use {@link net.minecraft.world.entity.PartEntity}
 * rather than the legacy {@code IEntityMultiPart} from 1.12.2. Implementations of this
 * interface represent individual hitbox parts that belong to a parent parasite entity
 * (such as the Abomination's body and head).
 * <p>
 * Each part delegates damage and interaction events back to its parent entity.
 *
 * @author SubspaceParasite Team
 * @since 1.0.0
 */
public interface IHitboxedEntity {

    /**
     * Returns the parent parasite entity that owns this hitbox part.
     * <p>
     * The parent is responsible for processing damage, health changes,
     * and death logic for all of its hitbox parts.
     *
     * @param <T> the concrete parent entity type
     * @return the parent entity, never {@code null}
     */
    <T extends Entity> T getParent();
}
