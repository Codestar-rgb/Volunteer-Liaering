package com.subspaceparasite.core;

import com.subspaceparasite.SubspaceParasite;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

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

    // ==================== Configured Features ====================

    /**
     * DeferredRegister for ConfiguredFeatures.
     * ConfiguredFeatures pair a Feature with its configuration.
     * Required in 1.20.1 for features to be referenced in worldgen.
     */
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES =
            DeferredRegister.create(net.minecraft.core.registries.Registries.CONFIGURED_FEATURE, SubspaceParasite.MOD_ID);

    public static final RegistryObject<ConfiguredFeature<?, ?>> CONFIGURED_PARASITE_COLONY =
            CONFIGURED_FEATURES.register("parasite_colony",
                    () -> new ConfiguredFeature<>(PARASITE_COLONY.get(), NoneFeatureConfiguration.INSTANCE));

    public static final RegistryObject<ConfiguredFeature<?, ?>> CONFIGURED_BIOME_HEART_PLACEMENT =
            CONFIGURED_FEATURES.register("biome_heart_placement",
                    () -> new ConfiguredFeature<>(BIOME_HEART_PLACEMENT.get(), NoneFeatureConfiguration.INSTANCE));

    public static final RegistryObject<ConfiguredFeature<?, ?>> CONFIGURED_INFESTED_TREE =
            CONFIGURED_FEATURES.register("infested_tree",
                    () -> new ConfiguredFeature<>(INFESTED_TREE.get(), NoneFeatureConfiguration.INSTANCE));

    public static final RegistryObject<ConfiguredFeature<?, ?>> CONFIGURED_PARASITE_SPREAD =
            CONFIGURED_FEATURES.register("parasite_spread",
                    () -> new ConfiguredFeature<>(PARASITE_SPREAD.get(), NoneFeatureConfiguration.INSTANCE));

    // ==================== Placed Features ====================

    /**
     * DeferredRegister for PlacedFeatures.
     * PlacedFeatures pair a ConfiguredFeature with placement modifiers
     * (rarity, height, biome filter, etc.). Required in 1.20.1 for
     * features to appear in world generation.
     */
    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES =
            DeferredRegister.create(net.minecraft.core.registries.Registries.PLACED_FEATURE, SubspaceParasite.MOD_ID);

    /** Parasite colony — rare, surface-level placement. */
    public static final RegistryObject<PlacedFeature> PLACED_PARASITE_COLONY =
            PLACED_FEATURES.register("parasite_colony",
                    () -> new PlacedFeature(
                            Holder.direct(CONFIGURED_PARASITE_COLONY.get()),
                            List.of(RarityFilter.onAverageOnceEvery(32),
                                    InSquarePlacement.spread(),
                                    PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                                    BiomeFilter.biome())));

    /** Biome heart placement — very rare. */
    public static final RegistryObject<PlacedFeature> PLACED_BIOME_HEART =
            PLACED_FEATURES.register("biome_heart_placement",
                    () -> new PlacedFeature(
                            Holder.direct(CONFIGURED_BIOME_HEART_PLACEMENT.get()),
                            List.of(RarityFilter.onAverageOnceEvery(64),
                                    InSquarePlacement.spread(),
                                    PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                                    BiomeFilter.biome())));

    /** Infested tree — occasional, surface-level. */
    public static final RegistryObject<PlacedFeature> PLACED_INFESTED_TREE =
            PLACED_FEATURES.register("infested_tree",
                    () -> new PlacedFeature(
                            Holder.direct(CONFIGURED_INFESTED_TREE.get()),
                            List.of(RarityFilter.onAverageOnceEvery(8),
                                    InSquarePlacement.spread(),
                                    PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                                    BiomeFilter.biome())));

    /** Parasite spread — common, surface-level. */
    public static final RegistryObject<PlacedFeature> PLACED_PARASITE_SPREAD =
            PLACED_FEATURES.register("parasite_spread",
                    () -> new PlacedFeature(
                            Holder.direct(CONFIGURED_PARASITE_SPREAD.get()),
                            List.of(RarityFilter.onAverageOnceEvery(4),
                                    InSquarePlacement.spread(),
                                    PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                                    BiomeFilter.biome())));

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
