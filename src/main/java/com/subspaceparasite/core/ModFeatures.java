package com.subspaceparasite.core;

import com.subspaceparasite.SubspaceParasite;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Feature registry for the SubspaceParasite mod.
 * Contains world generation features for parasite biomes and structures.
 */
public class ModFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(ForgeRegistries.FEATURES, SubspaceParasite.MOD_ID);

    // Parasite colony structure feature — places a biome heart with surrounding biome blocks
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> PARASITE_COLONY = FEATURES.register(
            "parasite_colony",
            () -> new ParasiteColonyFeature(NoneFeatureConfiguration.CODEC)
    );

    // Biome heart placement feature — places a single biome heart block
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> BIOME_HEART_PLACEMENT = FEATURES.register(
            "biome_heart_placement",
            () -> new BiomeHeartPlacementFeature(NoneFeatureConfiguration.CODEC)
    );

    // Infested tree feature — places an infested variant of a tree
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> INFESTED_TREE = FEATURES.register(
            "infested_tree",
            () -> new InfestedTreeFeature(NoneFeatureConfiguration.CODEC)
    );

    // Parasite spread feature — places small patches of spreading parasite blocks
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> PARASITE_SPREAD = FEATURES.register(
            "parasite_spread",
            () -> new ParasiteSpreadFeature(NoneFeatureConfiguration.CODEC)
    );

    // ==================== Configured & Placed Features ====================
    // NOTE: In Forge 1.20.1, ConfiguredFeature and PlacedFeature are data-driven
    // registries. They cannot be registered via DeferredRegister — they must be
    // provided via JSON data packs under:
    //   data/<modid>/worldgen/configured_feature/<name>.json
    //   data/<modid>/worldgen/placed_feature/<name>.json
    // The DeferredRegister approach causes "Unable to find registry" crashes.
    // For now, these are disabled until proper data pack JSONs are created.

    private ModFeatures() {
        // Utility class - prevent instantiation
    }

    // ==================== Feature Implementations ====================

    /**
     * Feature that generates a small parasite colony structure.
     */
    private static class ParasiteColonyFeature extends Feature<NoneFeatureConfiguration> {
        ParasiteColonyFeature(com.mojang.serialization.Codec<NoneFeatureConfiguration> codec) {
            super(codec);
        }

        @Override
        public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
            WorldGenLevel level = context.level();
            BlockPos origin = context.origin();
            RandomSource random = context.random();

            // Place a small patch of parasite biome blocks
            BlockPos groundPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, origin);
            if (level.getBlockState(groundPos.below()).isAir()) return false;

            // Place a 3x3 patch of biomass around the origin
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos target = groundPos.offset(dx, 0, dz);
                    if (level.getBlockState(target).isAir() && random.nextFloat() < 0.7f) {
                        level.setBlock(target, ModBlocks.BIOMASS_BLOCK.get().defaultBlockState(), 3);
                    }
                }
            }
            return true;
        }
    }

    /**
     * Feature that places a single biome heart block.
     */
    private static class BiomeHeartPlacementFeature extends Feature<NoneFeatureConfiguration> {
        BiomeHeartPlacementFeature(com.mojang.serialization.Codec<NoneFeatureConfiguration> codec) {
            super(codec);
        }

        @Override
        public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
            WorldGenLevel level = context.level();
            BlockPos origin = context.origin();

            BlockPos groundPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, origin);
            if (level.getBlockState(groundPos.below()).isAir()) return false;

            // Place biome heart block
            level.setBlock(groundPos, ModBlocks.BIOME_HEART.get().defaultBlockState(), 3);
            return true;
        }
    }

    /**
     * Feature that places an infested tree.
     */
    private static class InfestedTreeFeature extends Feature<NoneFeatureConfiguration> {
        InfestedTreeFeature(com.mojang.serialization.Codec<NoneFeatureConfiguration> codec) {
            super(codec);
        }

        @Override
        public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
            WorldGenLevel level = context.level();
            BlockPos origin = context.origin();
            RandomSource random = context.random();

            BlockPos groundPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, origin);
            if (level.getBlockState(groundPos.below()).isAir()) return false;

            // Place a simple infested trunk (3-5 blocks tall)
            int height = 3 + random.nextInt(3);
            for (int y = 0; y < height; y++) {
                BlockPos trunkPos = groundPos.above(y);
                if (level.getBlockState(trunkPos).isAir() || level.getBlockState(trunkPos).canBeReplaced()) {
                    level.setBlock(trunkPos, ModBlocks.INFESTED_TRUNK.get().defaultBlockState(), 3);
                }
            }
            return true;
        }
    }

    /**
     * Feature that places small patches of spreading parasite blocks.
     */
    private static class ParasiteSpreadFeature extends Feature<NoneFeatureConfiguration> {
        ParasiteSpreadFeature(com.mojang.serialization.Codec<NoneFeatureConfiguration> codec) {
            super(codec);
        }

        @Override
        public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
            WorldGenLevel level = context.level();
            BlockPos origin = context.origin();
            RandomSource random = context.random();

            BlockPos groundPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, origin);
            if (level.getBlockState(groundPos.below()).isAir()) return false;

            // Place scattered spreading blocks
            int count = 2 + random.nextInt(4);
            for (int i = 0; i < count; i++) {
                int dx = random.nextInt(8) - 4;
                int dz = random.nextInt(8) - 4;
                BlockPos target = groundPos.offset(dx, 0, dz);
                if (level.getBlockState(target).isAir() &&
                        level.getBlockState(target.below()).isSolidRender(level, target.below())) {
                    level.setBlock(target, ModBlocks.HARLEQUINN_GRASS.get().defaultBlockState(), 3);
                }
            }
            return true;
        }
    }
}
