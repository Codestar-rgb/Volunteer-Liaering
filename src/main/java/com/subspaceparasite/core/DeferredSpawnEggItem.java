package com.subspaceparasite.core;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

/**
 * A spawn egg item that defers EntityType resolution to prevent
 * "Registry Object not present" crashes during mod loading.
 * <p>
 * In Forge 1.20.1, items and entities are registered in separate
 * RegisterEvent dispatches. When the ITEMS DeferredRegister fires,
 * the ENTITIES RegistryObjects may not yet be populated, causing
 * {@code ModEntities.XXX.get()} to throw
 * "Registry Object not present".
 * <p>
 * This class stores a {@code Supplier<EntityType<?>>} and resolves
 * it lazily on the first call to {@link #getType()}, which happens
 * after all registries are fully populated.
 */
public class DeferredSpawnEggItem extends SpawnEggItem {

    private final Supplier<? extends EntityType<?>> entityTypeSupplier;

    /**
     * We pass a dummy EntityType (PIG) to the super constructor to satisfy
     * the non-null requirement, but override getType() to return the
     * actual deferred entity type. The dummy value is never used at runtime
     * because getType() is always called instead.
     */
    @SuppressWarnings("unchecked")
    public DeferredSpawnEggItem(Supplier<? extends EntityType<?>> entityTypeSupplier,
                                int backgroundColor, int highlightColor,
                                Item.Properties properties) {
        // Use a safe dummy — PIG is always available in vanilla.
        // This value is never returned at runtime; getType() is overridden.
        super((EntityType<?>) net.minecraft.world.entity.EntityType.PIG,
              backgroundColor, highlightColor, properties);
        this.entityTypeSupplier = entityTypeSupplier;
    }

    /**
     * Returns the actual EntityType supplied lazily.
     * This is called by Minecraft when the egg is used.
     */
    @Override
    public EntityType<?> getType() {
        return entityTypeSupplier.get();
    }
}
