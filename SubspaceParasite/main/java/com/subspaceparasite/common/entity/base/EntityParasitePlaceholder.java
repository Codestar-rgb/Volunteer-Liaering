package com.subspaceparasite.common.entity.base;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/**
 * Concrete non-abstract subclass of {@link EntityParasiteBase} used as
 * a placeholder for entity type registrations that don't yet have specific
 * implementations. All entity types in ModEntities are registered with
 * this class as their factory until replaced with proper tier entities.
 * <p>
 * Since EntityParasiteBase is declared abstract (despite having no abstract
 * methods), a concrete subclass is required for EntityType registration.
 * This class simply exposes the protected constructor as public.
 */
public class EntityParasitePlaceholder extends EntityParasiteBase {

    public EntityParasitePlaceholder(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }
}
