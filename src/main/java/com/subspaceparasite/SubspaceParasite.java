package com.subspaceparasite;

import com.subspaceparasite.common.block.BlockPurifyMappings;
import com.subspaceparasite.common.entity.base.COTHMapping;
import com.subspaceparasite.common.entity.base.EvolutionDispatcher;
import com.subspaceparasite.common.world.CelestialManager;
import com.subspaceparasite.common.world.ModSaveData;
import com.subspaceparasite.common.world.biome.BiomeParasiteBase;
import com.subspaceparasite.core.ModBiomes;
import com.subspaceparasite.core.ModBlocks;
import com.subspaceparasite.core.ModCreativeTabs;
import com.subspaceparasite.core.ModEffects;
import com.subspaceparasite.core.ModEntities;
import com.subspaceparasite.core.ModFeatures;
import com.subspaceparasite.core.ModFluids;
import com.subspaceparasite.core.ModItems;
import com.subspaceparasite.core.ModParticleTypes;
import com.subspaceparasite.core.ModSounds;
import com.subspaceparasite.network.ModNetwork;

import com.subspaceparasite.config.ModConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

@Mod(SubspaceParasite.MOD_ID)
public class SubspaceParasite {

    public static final String MOD_ID = "subspaceparasite";
    public static final String MODID = MOD_ID; // Alias for client code compatibility
    public static final String MOD_NAME = "Scape and Run: Parasites";
    public static final String VERSION = "1.0.0";

    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    /** Shared Random instance for use across the mod (e.g. DifficultySystem, spawning). */
    public static final Random RANDOM = new Random();

    public static SubspaceParasite INSTANCE;

    private final IEventBus modEventBus;

    public SubspaceParasite() {
        INSTANCE = this;
        modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register all DeferredRegister entries on the mod event bus
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModEntities.BLOCK_ENTITIES.register(modEventBus);
        ModEffects.EFFECTS.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);
        ModFluids.FLUID_TYPES.register(modEventBus);
        ModBiomes.BIOMES.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);
        ModFeatures.FEATURES.register(modEventBus);
        // NOTE: ConfiguredFeature and PlacedFeature cannot use DeferredRegister
        // in Forge 1.20.1 — they are data-driven registries. See ModFeatures.java.
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModParticleTypes.PARTICLE_TYPES.register(modEventBus);

        // Register config with Forge so it is loaded/saved properly
        ModLoadingContext.get().registerConfig(Type.COMMON, ModConfig.SPEC, "subspaceparasite-common.toml");

        // Register mod lifecycle event listeners
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events
        MinecraftForge.EVENT_BUS.register(this);

        // Note: ParasiteCapabilityEvents is auto-registered on FORGE bus via @Mod.EventBusSubscriber
        // Note: ModCommands is auto-registered on FORGE bus via @Mod.EventBusSubscriber for /srp commands

        LOGGER.info("{} v{} initialized", MOD_NAME, VERSION);
    }

    private void commonSetup(final net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // Register network channel and packets
            ModNetwork.register();

            // Wire entity blocks to their BlockEntityTypes and tickers.
            // This MUST happen after both ModBlocks and ModEntities are registered
            // to avoid circular class-loading issues.
            ModBlocks.wireEntityTypes();

            // Initialize COTH entity conversion mapping
            COTHMapping.init();

            // Initialize block conversion mappings
            BlockPurifyMappings.init();
            BiomeParasiteBase.init();

            LOGGER.info("{} common setup complete", MOD_NAME);
        });
    }

    /**
     * Server tick handler for ModSaveData and CelestialManager.
     * Registered on the FORGE event bus.
     * <p>
     * NOTE: ModWorldData ticking is handled exclusively by
     * {@link com.subspaceparasite.handler.ForgeEventHandler#onServerTick}
     * to avoid double-ticking. Do NOT tick ModWorldData here.
     */
    @net.minecraftforge.eventbus.api.SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        for (ServerLevel serverLevel : event.getServer().getAllLevels()) {
            // Tick ModSaveData per-dimension (evolution points, phase tracking)
            ModSaveData.get(serverLevel).tick(serverLevel);

            // Tick CelestialManager per-dimension (celestial night events)
            CelestialManager.get(serverLevel).tick(serverLevel);

            // Tick EvolutionDispatcher singleton
            EvolutionDispatcher.getInstance().tick(serverLevel);
        }
    }

    /**
     * Utility method to create a ResourceLocation with the mod's namespace.
     *
     * @param path the path component of the resource location
     * @return a new ResourceLocation with the mod's namespace
     */
    public static ResourceLocation rl(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
