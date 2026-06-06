package com.subspaceparasite.common.world.biome;

import com.subspaceparasite.core.ModBlocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for parasite biomes.
 * <p>
 * The original SRP has 5 parasite biomes, each with unique terrain
 * composed of parasite-themed blocks. This base class provides:
 * <ul>
 *   <li>A static block conversion mapping (vanilla → infested equivalents)</li>
 *   <li>Accessors for common parasite terrain blocks (dirt, stone)</li>
 * </ul>
 * <p>
 * Subclasses should extend this to define biome-specific generation
 * rules, spawn lists, and visual properties.
 */
public class BiomeParasiteBase {

    // ── Block conversion map ──

    /**
     * Maps vanilla blocks to their infested/parasite equivalents.
     * Used during biome terrain generation to replace vanilla blocks
     * with parasite-themed versions.
     */
    private static final Map<Block, Block> VANILLA_TO_PARASITE = new HashMap<>();

    /** Whether the conversion map has been initialized. */
    private static volatile boolean initialized = false;

    /**
     * Initializes the block conversion map.
     * Called during mod commonSetup after ModBlocks is populated.
     */
    public static void init() {
        if (initialized) return;
        initialized = true;

        // Map vanilla blocks to their parasite equivalents
        VANILLA_TO_PARASITE.put(net.minecraft.world.level.block.Blocks.GRASS_BLOCK, ModBlocks.HARLEQUINN_GRASS.get());
        VANILLA_TO_PARASITE.put(net.minecraft.world.level.block.Blocks.DIRT, getDirt());
        VANILLA_TO_PARASITE.put(net.minecraft.world.level.block.Blocks.STONE, getStone());
        VANILLA_TO_PARASITE.put(net.minecraft.world.level.block.Blocks.COBBLESTONE, ModBlocks.INFESTED_COBBLESTONE.get());
        VANILLA_TO_PARASITE.put(net.minecraft.world.level.block.Blocks.OAK_PLANKS, ModBlocks.INFESTED_PLANKS.get());
        VANILLA_TO_PARASITE.put(net.minecraft.world.level.block.Blocks.STONE_BRICKS, ModBlocks.INFESTED_STONE_BRICKS.get());
        VANILLA_TO_PARASITE.put(net.minecraft.world.level.block.Blocks.SAND, ModBlocks.INFESTED_SAND.get());
        VANILLA_TO_PARASITE.put(net.minecraft.world.level.block.Blocks.SANDSTONE, ModBlocks.INFESTED_SANDSTONE.get());
        VANILLA_TO_PARASITE.put(net.minecraft.world.level.block.Blocks.TERRACOTTA, ModBlocks.INFESTED_TERRACOTTA.get());
        VANILLA_TO_PARASITE.put(net.minecraft.world.level.block.Blocks.GLASS, ModBlocks.INFESTED_GLASS.get());
        VANILLA_TO_PARASITE.put(net.minecraft.world.level.block.Blocks.OAK_LEAVES, ModBlocks.INFESTED_LEAVES.get());
        VANILLA_TO_PARASITE.put(net.minecraft.world.level.block.Blocks.OAK_LOG, ModBlocks.INFESTED_TRUNK.get());
        VANILLA_TO_PARASITE.put(net.minecraft.world.level.block.Blocks.OAK_FENCE, ModBlocks.INFESTED_FENCE.get());
        VANILLA_TO_PARASITE.put(net.minecraft.world.level.block.Blocks.SANDSTONE_STAIRS, ModBlocks.INFESTED_SANDSTONE_STAIRS.get());
        VANILLA_TO_PARASITE.put(net.minecraft.world.level.block.Blocks.SANDSTONE_SLAB, ModBlocks.INFESTED_SANDSTONE_SLAB.get());
        VANILLA_TO_PARASITE.put(net.minecraft.world.level.block.Blocks.SANDSTONE_WALL, ModBlocks.INFESTED_SANDSTONE_WALL.get());
        VANILLA_TO_PARASITE.put(net.minecraft.world.level.block.Blocks.STONE_BRICK_STAIRS, ModBlocks.INFESTED_STONE_BRICK_STAIRS.get());
        VANILLA_TO_PARASITE.put(net.minecraft.world.level.block.Blocks.STONE_BRICK_SLAB, ModBlocks.INFESTED_STONE_BRICK_SLAB.get());
        VANILLA_TO_PARASITE.put(net.minecraft.world.level.block.Blocks.STONE_BRICK_WALL, ModBlocks.INFESTED_STONE_BRICK_WALL.get());
        VANILLA_TO_PARASITE.put(net.minecraft.world.level.block.Blocks.OAK_STAIRS, ModBlocks.INFESTED_PLANK_STAIRS.get());
        VANILLA_TO_PARASITE.put(net.minecraft.world.level.block.Blocks.OAK_SLAB, ModBlocks.INFESTED_PLANK_SLAB.get());
        VANILLA_TO_PARASITE.put(net.minecraft.world.level.block.Blocks.ICE, ModBlocks.BLOODY_ICE.get());
    }

    // ── Block accessors ──

    /**
     * Returns the parasite dirt block.
     * This is the primary terrain block for parasite biomes.
     *
     * @return the parasite dirt block
     */
    public static Block getDirt() {
        return ModBlocks.PARASITE_STAIN.get();
    }

    /**
     * Returns the parasite stone block.
     * This replaces vanilla stone in parasite biome terrain.
     *
     * @return the parasite stone block
     */
    public static Block getStone() {
        return ModBlocks.POLISHED_INFECTED_STONE.get();
    }

    // ── Block conversion ──

    /**
     * Converts a vanilla BlockState to its parasite equivalent.
     * If no mapping exists, returns the original BlockState.
     * <p>
     * The conversion preserves properties where possible (e.g. stair
     * facing, slab type) by creating a new state from the mapped block
     * with default properties. Full property transfer should be handled
     * by the caller when needed.
     *
     * @param original the original vanilla BlockState
     * @return the converted BlockState, or the original if no mapping exists
     */
    public static BlockState convertBlock(BlockState original) {
        if (!initialized) init();
        Block replacement = VANILLA_TO_PARASITE.get(original.getBlock());
        if (replacement != null) {
            return replacement.defaultBlockState();
        }
        return original;
    }

    /**
     * Returns whether a given block has a parasite equivalent.
     *
     * @param block the vanilla block to check
     * @return true if a mapping exists
     */
    public static boolean hasParasiteEquivalent(Block block) {
        if (!initialized) init();
        return VANILLA_TO_PARASITE.containsKey(block);
    }

    /**
     * Returns the unmodifiable conversion map.
     *
     * @return map of vanilla blocks to parasite equivalents
     */
    public static Map<Block, Block> getConversionMap() {
        if (!initialized) init();
        return Map.copyOf(VANILLA_TO_PARASITE);
    }
}
