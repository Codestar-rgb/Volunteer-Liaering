package com.subspaceparasite.common.entity.base;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/**
 * Concrete non-abstract subclass of {@link EntityParasiteBase} used as
 * a placeholder for entity type registrations that don't yet have specific
 * implementations.
 * <p>
 * <b>Deprecated:</b> All entity types in ModEntities now have specific
 * implementations. This class is retained only for backward compatibility
 * with any existing saved-world data that may reference this type.
 * Do not use for new entity registrations.
 *
 * @deprecated Use specific entity subclass implementations instead.
 *             See {@code com.subspaceparasite.common.entity.monster} packages.
 */
@Deprecated
public class EntityParasitePlaceholder extends EntityParasiteBase {

    public EntityParasitePlaceholder(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }
}
