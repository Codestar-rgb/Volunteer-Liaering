package com.subspaceparasite.common.world;

import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.config.ModConfigSystems;
import com.subspaceparasite.core.ModBiomes;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * Handles Forge biome modifier registration for the SubspaceParasite mod.
 * <p>
 * In 1.20.1 Forge, biome modifiers are the standard way to add features,
 * spawns, and carvers to biomes. This handler:
 * <ul>
 *   <li>Respects the {@code parasiteBiomeEnabled} and {@code biomeWeight} config values</li>
 * </ul>
 * <p>
 * Note: ConfiguredFeature and PlacedFeature are data-driven in 1.20.1 and
 * cannot be registered via DeferredRegister. They must be provided via JSON
 * data packs. The getParasiteBiomeFeatures() method returns an empty list
 * until proper data pack JSONs are created.
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
     * NOTE: Returns empty list until data-driven PlacedFeature JSONs are
     * created under data/subspaceparasite/worldgen/placed_feature/.
     * DeferredRegister cannot be used for PlacedFeature in 1.20.1.
     *
     * @return list of placed feature holders for parasite biome generation
     */
    public static List<Holder<PlacedFeature>> getParasiteBiomeFeatures() {
        if (!shouldRegisterBiomeFeatures()) {
            return List.of();
        }

        // TODO: Populate this from data-driven PlacedFeature holders once
        // JSON data packs are created for worldgen/placed_feature/.
        return List.of();
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
