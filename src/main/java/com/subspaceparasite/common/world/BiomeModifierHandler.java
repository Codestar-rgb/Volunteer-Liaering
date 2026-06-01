package com.subspaceparasite.common.world;

import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.config.ModConfigSystems;
import com.subspaceparasite.core.ModBiomes;
import com.subspaceparasite.core.ModFeatures;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;

import java.util.List;
import java.util.function.Supplier;

/**
 * Handles Forge biome modifier registration for the SubspaceParasite mod.
 * <p>
 * In 1.20.1 Forge, biome modifiers are the standard way to add features,
 * spawns, and carvers to biomes. This handler:
 * <ul>
 *   <li>Adds parasite features (infested trees, spread patches, etc.) to parasite biomes</li>
 *   <li>Respects the {@code parasiteBiomeEnabled} and {@code biomeWeight} config values</li>
 * </ul>
 * <p>
 * Note: Adding custom biomes to the overworld's biome source requires
 * data-driven multi_noise_biome_source_parameter_list entries. See the
 * accompanying JSON files in {@code data/subspaceparasite/forge/biome_modifier/}.
 */
@Mod.EventBusSubscriber(modid = SubspaceParasite.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BiomeModifierHandler {

    private BiomeModifierHandler() {}

    /**
     * Returns whether parasite biome features should be registered,
     * based on the {@code parasiteBiomeEnabled} config.
     */
    public static boolean shouldRegisterBiomeFeatures() {
        return ModConfigSystems.isParasiteBiomeEnabled();
    }

    /**
     * Gets the list of placed features to add to parasite biomes.
     * <p>
     * These features are only included when the biome system is enabled
     * in the mod config.
     *
     * @return list of placed feature holders for parasite biome generation
     */
    public static List<Holder<PlacedFeature>> getParasiteBiomeFeatures() {
        if (!shouldRegisterBiomeFeatures()) {
            return List.of();
        }

        return List.of(
                ModFeatures.PLACED_PARASITE_SPREAD.getHolder().orElseThrow(),
                ModFeatures.PLACED_INFESTED_TREE.getHolder().orElseThrow(),
                ModFeatures.PLACED_PARASITE_COLONY.getHolder().orElseThrow(),
                ModFeatures.PLACED_BIOME_HEART.getHolder().orElseThrow()
        );
    }

    /**
     * Gets the HolderSet of parasite biomes to add features to.
     * Uses direct holders since parasite biomes don't use tags.
     *
     * @return HolderSet containing all registered parasite biomes
     */
    public static HolderSet<Biome> getParasiteBiomeHolders() {
        return HolderSet.direct(
                ModBiomes.BIOMEPARASITE_SHROUDED.getHolder().orElseThrow(),
                ModBiomes.BIOMEPARASITE_HARLEQUIN.getHolder().orElseThrow()
        );
    }
}
