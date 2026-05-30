package com.subspaceparasite.core;

import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import com.subspaceparasite.SubspaceParasite;
import net.minecraftforge.registries.RegistryObject;

/**
 * Biome registry for the SubspaceParasite mod.
 * Registers custom parasite biomes.
 */
public class ModBiomes {

    public static final DeferredRegister<Biome> BIOMES =
            DeferredRegister.create(ForgeRegistries.BIOMES, SubspaceParasite.MOD_ID);

    /**
     * Shrouded parasite biome - dark, fog-covered biome with infested terrain.
     */
    public static final RegistryObject<Biome> BIOMEPARASITE_SHROUDED = BIOMES.register("biomeparasite_shrouded",
            () -> createParasiteBiome(0x1A1A2E, 0x2D2D44, 0.5f, 0.8f));

    /**
     * Harlequin parasite biome - colorful, dangerous biome with volatile terrain.
     */
    public static final RegistryObject<Biome> BIOMEPARASITE_HARLEQUIN = BIOMES.register("biomeparasite_harlequin",
            () -> createParasiteBiome(0x3B1A3B, 0x5A2D5A, 0.7f, 0.6f));

    /**
     * Creates a placeholder parasite biome with basic properties.
     * Will be fully configured with features, carvers, and spawns later.
     */
    private static Biome createParasiteBiome(int fogColor, int waterColor, float temperature, float downfall) {
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
                .mobSpawnSettings(new MobSpawnSettings.Builder().build())
                .generationSettings(BiomeGenerationSettings.EMPTY)
                .build();
    }

    private ModBiomes() {
        // Utility class - prevent instantiation
    }
}
