package com.subspaceparasite.core;

import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.config.ModConfigSystems;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Biome registry for the SubspaceParasite mod.
 * Registers custom parasite biomes with proper 1.20.1 conventions.
 * <p>
 * Each parasite biome is registered via {@link DeferredRegister} and
 * created with visual effects, spawn settings, and generation settings.
 * Features are added to biomes via Forge biome modifier JSON files
 * (see {@code data/subspaceparasite/forge/biome_modifier/}).
 * <p>
 * Config values ({@code biomeWeight}, {@code parasiteBiomeEnabled}) are
 * respected by the biome modifier system and the overworld injection handler.
 * When {@code parasiteBiomeEnabled} is false, no parasite biomes will
 * generate in the overworld regardless of weight.
 */
public class ModBiomes {

    public static final DeferredRegister<Biome> BIOMES =
            DeferredRegister.create(ForgeRegistries.BIOMES, SubspaceParasite.MOD_ID);

    /**
     * Shrouded parasite biome - dark, fog-covered biome with infested terrain.
     * Uses very dark blue/purple fog and water colors.
     */
    public static final RegistryObject<Biome> BIOMEPARASITE_SHROUDED = BIOMES.register("biomeparasite_shrouded",
            () -> createParasiteBiome(0x1A1A2E, 0x2D2D44, 0.5f, 0.8f));

    /**
     * Harlequin parasite biome - colorful, dangerous biome with volatile terrain.
     * Uses deep purple fog and water colors.
     */
    public static final RegistryObject<Biome> BIOMEPARASITE_HARLEQUIN = BIOMES.register("biomeparasite_harlequin",
            () -> createParasiteBiome(0x3B1A3B, 0x5A2D5A, 0.7f, 0.6f));

    /**
     * Creates a parasite biome with basic properties.
     * <p>
     * Features are added via biome modifier JSONs rather than inline,
     * following the 1.20.1 data-driven convention. This allows
     * biome modifiers to be disabled at runtime via config.
     * <p>
     * The biome's generation weight in the overworld is controlled by
     * {@link ModConfigSystems#getBiomeWeight()}. A weight of 0 effectively
     * disables biome generation.
     *
     * @param fogColor     the fog color (RGB hex)
     * @param waterColor   the water color (RGB hex)
     * @param temperature  biome temperature (affects weather and crop growth)
     * @param downfall     biome downfall (affects precipitation)
     * @return a new Biome instance with parasite-themed settings
     */
    private static Biome createParasiteBiome(int fogColor, int waterColor, float temperature, float downfall) {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        // Note: No mob spawns are added here by default.
        // Parasite mob spawns are added via biome modifier JSONs
        // (see data/subspaceparasite/forge/biome_modifier/).
        // This allows spawns to be controlled by config at runtime.

        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(temperature)
                .downfall(downfall)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .fogColor(fogColor)
                        .waterColor(waterColor)
                        .waterFogColor(waterColor)
                        .skyColor(0x0A0A14)
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                        .build())
                .mobSpawnSettings(spawnBuilder.build())
                .generationSettings(BiomeGenerationSettings.EMPTY)
                .build();
    }

    /**
     * Returns the effective biome generation weight, respecting the
     * {@code parasiteBiomeEnabled} config. Returns 0 if biomes are disabled.
     *
     * @return the biome weight from config, or 0 if parasite biomes are disabled
     */
    public static double getEffectiveBiomeWeight() {
        if (!ModConfigSystems.isParasiteBiomeEnabled()) {
            return 0.0;
        }
        return ModConfigSystems.getBiomeWeight();
    }

    private ModBiomes() {
        // Utility class - prevent instantiation
    }
}
