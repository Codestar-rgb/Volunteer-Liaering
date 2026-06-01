package com.subspaceparasite.core;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
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
 * This class stores a {@code Supplier<EntityType<? extends Mob>>} and
 * resolves it lazily when the egg is actually used in-game.
 */
public class DeferredSpawnEggItem extends SpawnEggItem {

    private final Supplier<? extends EntityType<? extends Mob>> entityTypeSupplier;

    /**
     * Creates a deferred spawn egg. The EntityType is resolved lazily
     * via the supplier when {@link #getType()} is called at runtime.
     *
     * @param entityTypeSupplier supplier for the entity type (e.g. () -> ModEntities.XXX.get())
     * @param backgroundColor    background color of the egg
     * @param highlightColor     highlight color of the egg
     * @param properties         item properties
     */
    public DeferredSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> entityTypeSupplier,
                                int backgroundColor, int highlightColor,
                                Item.Properties properties) {
        // Pass a safe dummy EntityType to the super constructor.
        // EntityType.PIG is always available in vanilla and satisfies
        // the non-null requirement. getType() is overridden below
        // to return the actual deferred entity type at runtime.
        super(EntityType.PIG, backgroundColor, highlightColor, properties);
        this.entityTypeSupplier = entityTypeSupplier;
    }

    /**
     * Returns the actual EntityType supplied lazily.
     * Called by Minecraft when the spawn egg is used.
     */
    @Override
    public EntityType<? extends Mob> getType() {
        return entityTypeSupplier.get();
    }
}
