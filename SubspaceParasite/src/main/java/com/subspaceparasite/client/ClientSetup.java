package com.subspaceparasite.client;

import com.subspaceparasite.client.renderer.entity.*;
import com.subspaceparasite.common.entity.base.EntityParasitePlaceholder;
import com.subspaceparasite.common.entity.monster.primitive.*;
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
     * Registers specific GeckoLib renderers for implemented entities,
     * and fallback base renderer for placeholder entities.
     */
    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Register specific GeckoLib renderers for Primitive stage entities
        event.registerEntityRenderer(ModEntities.PRIMITIVE_BANO.get(), RenderBanoGeo::new);
        event.registerEntityRenderer(ModEntities.PRIMITIVE_CANRA.get(), RenderCanraGeo::new);
        event.registerEntityRenderer(ModEntities.PRIMITIVE_EMANA.get(), RenderEmanaGeo::new);
        event.registerEntityRenderer(ModEntities.PRIMITIVE_GIM.get(), RenderGimGeo::new);
        event.registerEntityRenderer(ModEntities.PRIMITIVE_HULL.get(), RenderHullGeo::new);
        event.registerEntityRenderer(ModEntities.PRIMITIVE_IKI.get(), RenderIkiGeo::new);
        event.registerEntityRenderer(ModEntities.PRIMITIVE_LUM.get(), RenderLumGeo::new);
        
        // Use reflection to register fallback renderers for all other parasite entity types
        try {
            for (java.lang.reflect.Field field : ModEntities.class.getDeclaredFields()) {
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) &&
                    field.getType().equals(RegistryObject.class)) {
                    field.setAccessible(true);
                    Object obj = field.get(null);
                    if (obj instanceof RegistryObject<?> ro) {
                        if (ro.get() instanceof EntityType<?> et) {
                            // Skip already registered entities
                            if (et == ModEntities.PRIMITIVE_BANO.get() ||
                                et == ModEntities.PRIMITIVE_CANRA.get() ||
                                et == ModEntities.PRIMITIVE_EMANA.get() ||
                                et == ModEntities.PRIMITIVE_GIM.get() ||
                                et == ModEntities.PRIMITIVE_HULL.get() ||
                                et == ModEntities.PRIMITIVE_IKI.get() ||
                                et == ModEntities.PRIMITIVE_LUM.get()) {
                                continue;
                            }
                            
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
