package com.subspaceparasite.core;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import com.subspaceparasite.SubspaceParasite;
import net.minecraftforge.registries.RegistryObject;

/**
 * Feature registry for the SubspaceParasite mod.
 * Placeholder - actual world generation features will be added later.
 */
public class ModFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(ForgeRegistries.FEATURES, SubspaceParasite.MOD_ID);

    // Placeholder features - actual implementations will be added during world-gen phase

    // Parasite colony structure feature
    // public static final RegistryObject<Feature<>> PARASITE_COLONY = ...

    // Biome heart placement feature
    // public static final RegistryObject<Feature<>> BIOME_HEART_PLACEMENT = ...

    // Infested tree feature
    // public static final RegistryObject<Feature<>> INFESTED_TREE = ...

    // Parasite spread feature
    // public static final RegistryObject<Feature<>> PARASITE_SPREAD = ...

    private ModFeatures() {
        // Utility class - prevent instantiation
    }
}
