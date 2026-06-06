package com.subspaceparasite.common.block;

import com.subspaceparasite.core.ModBlocks;

import net.minecraft.world.level.block.Block;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Bidirectional mapping system between vanilla and infested/parasite blocks.
 * <p>
 * The original SRP uses this system for:
 * <ul>
 *   <li>Purification: converting infested blocks back to vanilla (via purifier)</li>
 *   <li>Infestation: converting vanilla blocks to parasite equivalents (via COTH spreading)</li>
 *   <li>World generation: replacing terrain blocks in parasite biomes</li>
 * </ul>
 * <p>
 * Mappings are populated from {@link ModBlocks} during {@link #init()},
 * which should be called during commonSetup after all blocks are registered.
 */
public class BlockPurifyMappings {

    /** Maps parasite/infested blocks back to their vanilla originals. */
    private static final Map<Block, Block> parasiticToVanilla = new HashMap<>();

    /** Maps vanilla blocks to their parasite/infested equivalents. */
    private static final Map<Block, Block> vanillaToParasitic = new HashMap<>();

    /** Whether the mappings have been initialized. */
    private static volatile boolean initialized = false;

    /**
     * Populates both mapping tables from ModBlocks.
     * Called during commonSetup after all blocks are registered.
     */
    public static void init() {
        if (initialized) return;
        initialized = true;

        // ── Infested Blocks ──

        addMapping(net.minecraft.world.level.block.Blocks.COBBLESTONE, ModBlocks.INFESTED_COBBLESTONE.get());
        addMapping(net.minecraft.world.level.block.Blocks.OAK_PLANKS, ModBlocks.INFESTED_PLANKS.get());
        addMapping(net.minecraft.world.level.block.Blocks.STONE_BRICKS, ModBlocks.INFESTED_STONE_BRICKS.get());
        addMapping(net.minecraft.world.level.block.Blocks.TERRACOTTA, ModBlocks.INFESTED_TERRACOTTA.get());
        addMapping(net.minecraft.world.level.block.Blocks.SAND, ModBlocks.INFESTED_SAND.get());
        addMapping(net.minecraft.world.level.block.Blocks.SANDSTONE, ModBlocks.INFESTED_SANDSTONE.get());
        addMapping(net.minecraft.world.level.block.Blocks.GLASS, ModBlocks.INFESTED_GLASS.get());
        addMapping(net.minecraft.world.level.block.Blocks.OAK_LEAVES, ModBlocks.INFESTED_LEAVES.get());
        addMapping(net.minecraft.world.level.block.Blocks.OAK_LOG, ModBlocks.INFESTED_TRUNK.get());
        addMapping(net.minecraft.world.level.block.Blocks.OAK_FENCE, ModBlocks.INFESTED_FENCE.get());
        addMapping(net.minecraft.world.level.block.Blocks.ICE, ModBlocks.BLOODY_ICE.get());
        addMapping(net.minecraft.world.level.block.Blocks.GRASS_BLOCK, ModBlocks.HARLEQUINN_GRASS.get());

        // ── Stone variants ──

        addMapping(net.minecraft.world.level.block.Blocks.STONE, ModBlocks.POLISHED_INFECTED_STONE.get());
        addMapping(net.minecraft.world.level.block.Blocks.DIRT, ModBlocks.PARASITE_STAIN.get());

        // ── Stairs ──

        addMapping(net.minecraft.world.level.block.Blocks.STONE_BRICK_STAIRS, ModBlocks.INFESTED_STONE_BRICK_STAIRS.get());
        addMapping(net.minecraft.world.level.block.Blocks.SANDSTONE_STAIRS, ModBlocks.INFESTED_SANDSTONE_STAIRS.get());
        addMapping(net.minecraft.world.level.block.Blocks.OAK_STAIRS, ModBlocks.INFESTED_PLANK_STAIRS.get());

        // ── Slabs ──

        addMapping(net.minecraft.world.level.block.Blocks.STONE_BRICK_SLAB, ModBlocks.INFESTED_STONE_BRICK_SLAB.get());
        addMapping(net.minecraft.world.level.block.Blocks.SANDSTONE_SLAB, ModBlocks.INFESTED_SANDSTONE_SLAB.get());
        addMapping(net.minecraft.world.level.block.Blocks.OAK_SLAB, ModBlocks.INFESTED_PLANK_SLAB.get());

        // ── Walls ──

        addMapping(net.minecraft.world.level.block.Blocks.STONE_BRICK_WALL, ModBlocks.INFESTED_STONE_BRICK_WALL.get());
        addMapping(net.minecraft.world.level.block.Blocks.SANDSTONE_WALL, ModBlocks.INFESTED_SANDSTONE_WALL.get());

        // ── Glass panes ──

        addMapping(net.minecraft.world.level.block.Blocks.GLASS_PANE, ModBlocks.INFESTED_GLASS_PANE.get());

        // ── Workbench ──

        addMapping(net.minecraft.world.level.block.Blocks.CRAFTING_TABLE, ModBlocks.INFESTED_WORKBENCH.get());

        // ── Furnace ──

        addMapping(net.minecraft.world.level.block.Blocks.FURNACE, ModBlocks.INFESTED_FURNACE.get());

        // ── Pumpkin ──

        addMapping(net.minecraft.world.level.block.Blocks.PUMPKIN, ModBlocks.ASSIMILATED_PUMPKIN.get());
        addMapping(net.minecraft.world.level.block.Blocks.JACK_O_LANTERN, ModBlocks.ASSIMILATED_JACK_O_LANTERN.get());
        addMapping(net.minecraft.world.level.block.Blocks.SUGAR_CANE, ModBlocks.ASSIMILATED_SUGAR_CANE.get());

        // ── Wood types ──

        addMapping(net.minecraft.world.level.block.Blocks.OAK_DOOR, ModBlocks.GOTH_DOOR.get());
        addMapping(net.minecraft.world.level.block.Blocks.OAK_TRAPDOOR, ModBlocks.GOTH_TRAPDOOR.get());

        // ── Additional infested sandstone variants ──

        addMapping(net.minecraft.world.level.block.Blocks.CUT_SANDSTONE, ModBlocks.INFESTED_SANDSTONE_2.get());
        addMapping(net.minecraft.world.level.block.Blocks.CHISELED_SANDSTONE, ModBlocks.INFESTED_SANDSTONE_3.get());

        // ── Residue variants (post-purification) ──
        // NOTE: STONE_BRICKS -> INFESTED_STONE_BRICKS is already mapped above;
        // residue is a separate concept from infestation and should not overwrite
        // the infested mapping. Removed duplicate to preserve the infested mapping.
    }

    /**
     * Adds a bidirectional mapping between a vanilla block and its parasite equivalent.
     *
     * @param vanilla  the vanilla block
     * @param parasite the parasite/infested equivalent
     */
    public static void addMapping(Block vanilla, Block parasite) {
        vanillaToParasitic.put(vanilla, parasite);
        parasiticToVanilla.put(parasite, vanilla);
    }

    // ── Lookup ──

    /**
     * Returns the vanilla equivalent of a parasitic block.
     * Used for purification (converting infested blocks back).
     *
     * @param parasitic the parasitic/infested block
     * @return the vanilla equivalent, or null if no mapping exists
     */
    public static Block getPurifiedBlock(Block parasitic) {
        if (!initialized) init();
        return parasiticToVanilla.get(parasitic);
    }

    /**
     * Returns the parasite equivalent of a vanilla block.
     * Used for infestation (converting vanilla blocks to parasite versions).
     *
     * @param vanilla the vanilla block
     * @return the parasite equivalent, or null if no mapping exists
     */
    public static Block getInfestedBlock(Block vanilla) {
        if (!initialized) init();
        return vanillaToParasitic.get(vanilla);
    }

    // ── Existence checks ──

    /**
     * Returns whether a parasitic block has a vanilla (purified) version.
     *
     * @param parasitic the parasitic block to check
     * @return true if a vanilla equivalent exists
     */
    public static boolean hasPurifiedVersion(Block parasitic) {
        if (!initialized) init();
        return parasiticToVanilla.containsKey(parasitic);
    }

    /**
     * Returns whether a vanilla block has an infested (parasite) version.
     *
     * @param vanilla the vanilla block to check
     * @return true if a parasite equivalent exists
     */
    public static boolean hasInfestedVersion(Block vanilla) {
        if (!initialized) init();
        return vanillaToParasitic.containsKey(vanilla);
    }

    // ── Utility ──

    /**
     * Returns an unmodifiable view of the parasitic-to-vanilla map.
     *
     * @return map of parasitic blocks to vanilla equivalents
     */
    public static Map<Block, Block> getParasiticToVanillaMap() {
        if (!initialized) init();
        return Collections.unmodifiableMap(parasiticToVanilla);
    }

    /**
     * Returns an unmodifiable view of the vanilla-to-parasitic map.
     *
     * @return map of vanilla blocks to parasite equivalents
     */
    public static Map<Block, Block> getVanillaToParasiticMap() {
        if (!initialized) init();
        return Collections.unmodifiableMap(vanillaToParasitic);
    }

    /**
     * Returns the total number of registered mappings.
     *
     * @return number of block pairs
     */
    public static int getMappingCount() {
        if (!initialized) init();
        return vanillaToParasitic.size();
    }
}
