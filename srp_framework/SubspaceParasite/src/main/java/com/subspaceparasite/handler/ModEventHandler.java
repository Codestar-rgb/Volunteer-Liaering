package com.subspaceparasite.handler;

import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.common.entity.base.EntityParasitePlaceholder;
import com.subspaceparasite.core.ModEntities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Mod event handler registered to the modEventBus.
 * Handles entity registration events including attribute creation,
 * attribute modification, and spawn placement registration.
 *
 * These events fire during mod loading and cannot be handled on the
 * Forge event bus.
 */
@Mod.EventBusSubscriber(modid = SubspaceParasite.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventHandler {

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Collect all registered parasite entity types from ModEntities via reflection.
     * This avoids having to manually list every single entity type field.
     */
    @SuppressWarnings("unchecked")
    private static List<EntityType<EntityParasitePlaceholder>> collectEntityTypes() {
        List<EntityType<EntityParasitePlaceholder>> types = new ArrayList<>();
        try {
            for (Field field : ModEntities.class.getDeclaredFields()) {
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) &&
                    field.getType().equals(net.minecraftforge.registries.RegistryObject.class)) {
                    field.setAccessible(true);
                    Object obj = field.get(null);
                    if (obj instanceof net.minecraftforge.registries.RegistryObject<?> ro) {
                        if (ro.get() instanceof EntityType<?> et) {
                            // Only register parasite entities (MONSTER category), skip projectiles (MISC) and block entities
                            if (et.getCategory() == MobCategory.MONSTER) {
                                try {
                                    types.add((EntityType<EntityParasitePlaceholder>) (EntityType<?>) et);
                                } catch (ClassCastException e) {
                                    // Skip non-EntityParasitePlaceholder types
                                }
                            }
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            LOGGER.error("Failed to collect entity types from ModEntities", e);
        }
        return types;
    }

    /**
     * Register attributes for ALL parasite entity types.
     * Each parasite entity gets the base attributes from
     * {@link EntityParasiteBase#createAttributes()}.
     */
    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        AttributeSupplier.Builder attributes = EntityParasiteBase.createAttributes();

        List<EntityType<EntityParasitePlaceholder>> types = collectEntityTypes();
        for (EntityType<EntityParasitePlaceholder> type : types) {
            event.put(type, attributes.build());
        }

        LOGGER.debug("Registered attributes for {} parasite entity types", types.size());
    }

    /**
     * Modify vanilla entity attributes if needed.
     * Can add follow range, knockback resistance, etc. to vanilla
     * entities that parasites interact with.
     */
    @SubscribeEvent
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        // Add follow range to entities that parasites may chase
        // This allows parasites to detect targets at greater distances

        // Example: Add extended follow range to Iron Golems
        // so they can aggro on parasites from further away
        // event.add(EntityType.IRON_GOLEM, Attributes.FOLLOW_RANGE, 40.0);

        LOGGER.debug("EntityAttributeModificationEvent processed for SubspaceParasite");
    }

    /**
     * Register spawn placement rules for ALL parasite entity types.
     * Uses {@link EntityParasiteBase#checkSpawnRules} for validation.
     *
     * All parasite types use the same spawn placement logic:
     * - Must be on the ground (Heightmap.Types.MOTION_BLOCKING)
     * - Must pass EntityParasiteBase.checkSpawnRules() validation
     */
    @SubscribeEvent
    public static void onSpawnPlacementRegister(SpawnPlacementRegisterEvent event) {
        List<EntityType<EntityParasitePlaceholder>> types = collectEntityTypes();
        for (EntityType<EntityParasitePlaceholder> type : types) {
            event.register(
                    type,
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING,
                    EntityParasiteBase::checkSpawnRules,
                    SpawnPlacementRegisterEvent.Operation.OR
            );
        }

        LOGGER.debug("Registered spawn placements for {} parasite entity types", types.size());
    }
}
