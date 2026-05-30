package com.subspaceparasite.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.client.model.entity.ModelParasiteBiped;
import com.subspaceparasite.client.overlay.InfectionOverlayHandler;
import com.subspaceparasite.client.particle.ModParticles;
import com.subspaceparasite.client.particle.ParasiteParticle;
import com.subspaceparasite.client.renderer.entity.RenderParasiteBase;
import com.subspaceparasite.common.entity.base.EntityParasitePlaceholder;
import com.subspaceparasite.core.ModEntities;

import net.minecraft.client.KeyMapping;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

import org.lwjgl.glfw.GLFW;

/**
 * Client-side setup and registration for the SubspaceParasite mod.
 * Handles particle factories, entity renderers, key bindings, GUI overlays, and fog.
 */
@Mod.EventBusSubscriber(modid = SubspaceParasite.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    // Key bindings
    public static final String KEY_CATEGORY = "key.categories.subspaceparasite";
    public static KeyMapping SCAN_KEY;
    public static KeyMapping BESTIARY_KEY;

    /**
     * Register particle factories.
     */
    @SubscribeEvent
    public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        // Register all mod particle factories
        event.registerSpriteSet(ModParticles.SPORE.get(), ParasiteParticle.Factory::new);
        event.registerSpriteSet(ModParticles.CORRUPTION.get(), ParasiteParticle.Factory::new);
        event.registerSpriteSet(ModParticles.BILE.get(), ParasiteParticle.Factory::new);
        event.registerSpriteSet(ModParticles.EVOLUTION.get(), ParasiteParticle.Factory::new);
        event.registerSpriteSet(ModParticles.INFESTATION.get(), ParasiteParticle.Factory::new);
        event.registerSpriteSet(ModParticles.DISSOLVE.get(), ParasiteParticle.Factory::new);
        event.registerSpriteSet(ModParticles.VIRAL.get(), ParasiteParticle.Factory::new);
        event.registerSpriteSet(ModParticles.PRIMORDIAL.get(), ParasiteParticle.Factory::new);
        event.registerSpriteSet(ModParticles.ANCIENT.get(), ParasiteParticle.Factory::new);
        event.registerSpriteSet(ModParticles.NEXUS.get(), ParasiteParticle.Factory::new);
    }

    /**
     * Register entity renderers.
     * Called during EntityRenderersEvent.RegisterRenderers on the MOD bus.
     * Registers the base parasite renderer for ALL registered parasite entity types
     * using reflection to avoid maintaining a manual list of entity types.
     */
    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Use reflection to register renderers for all entity types in ModEntities,
        // similar to how ModEventHandler collects entity types for attribute registration.
        try {
            for (java.lang.reflect.Field field : ModEntities.class.getDeclaredFields()) {
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) &&
                    field.getType().equals(RegistryObject.class)) {
                    field.setAccessible(true);
                    Object obj = field.get(null);
                    if (obj instanceof RegistryObject<?> ro) {
                        if (ro.get() instanceof EntityType<?> et) {
                            // Only register for parasite entity types (EntityParasitePlaceholder)
                            // Skip block entity types
                            try {
                                EntityType<EntityParasitePlaceholder> placeholderType =
                                        (EntityType<EntityParasitePlaceholder>) (EntityType<?>) et;
                                event.registerEntityRenderer(placeholderType, RenderParasiteBase::new);
                            } catch (ClassCastException e) {
                                // Skip non-parasite entity types (block entities, etc.)
                            }
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            SubspaceParasite.LOGGER.error("Failed to register entity renderers via reflection", e);
        }
    }

    /**
     * Register key bindings.
     */
    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        SCAN_KEY = new KeyMapping(
                "key.subspaceparasite.scan",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                KEY_CATEGORY
        );
        BESTIARY_KEY = new KeyMapping(
                "key.subspaceparasite.bestiary",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                KEY_CATEGORY
        );

        event.register(SCAN_KEY);
        event.register(BESTIARY_KEY);
    }

    /**
     * Register GUI overlays.
     */
    @SubscribeEvent
    public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll(
                SubspaceParasite.MOD_ID + ":infection_overlay",
                InfectionOverlayHandler.INSTANCE
        );
    }

    /**
     * Register fog color handler and other client-side event listeners.
     * Called during FMLClientSetupEvent.
     */
    public static void registerClientEventHandlers() {
        // FogHandler and InfectionOverlayHandler are registered via @SubscribeEvent
        // on their respective classes with the Forge event bus
    }

    /**
     * Register model layer locations.
     * Called during EntityRenderersEvent.RegisterLayerDefinitions on the MOD bus.
     */
    @SubscribeEvent
    public static void registerModelLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModelParasiteBiped.LAYER_LOCATION, ModelParasiteBiped::createBodyLayer);
    }
}
