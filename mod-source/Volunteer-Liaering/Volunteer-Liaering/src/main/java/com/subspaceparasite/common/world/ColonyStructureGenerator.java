package com.subspaceparasite.common.world;

import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.config.ModConfigSystems;
import com.subspaceparasite.core.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.*;

/**
 * Handles incremental colony structure generation for parasite colonies.
 * <p>
 * Colony structures are built from parasite blocks in progressive levels:
 * <ul>
 *   <li>Level 1 (Outpost): 5x5 biomass pad, 1 beckon, 2-3 tendrils</li>
 *   <li>Level 2 (Nest): 7x7 structure with walls, 1 rooter, multiple spawning pods</li>
 *   <li>Level 3 (Colony): 9x9 structure with rooms, multiple rooters, beckon, defensive deterrents</li>
 *   <li>Level 4 (Fortress): 11x11 with multiple chambers, full defense perimeter</li>
 *   <li>Level 5 (Hive): 13x13 mega-structure, can produce all entity types</li>
 * </ul>
 * <p>
 * Structure generation is incremental — blocks are placed over time via
 * a build queue, not all at once. Beckons and Rooters call into this
 * generator to advance construction.
 */
public class ColonyStructureGenerator {

    // ========== Structure Block Palette ==========

    /** Floor and basic building material. */
    private static final Block BIOMASS = ModBlocks.BIOMASS_BLOCK.get();
    /** Wall and structural material. */
    private static final Block STRUCTURE = ModBlocks.PARASITE_STRUCTURE.get();
    /** Tendril / support / connection material. */
    private static final Block TENDRIL = ModBlocks.PARASITE_THIN.get();
    /** Hive spawning pod material. */
    private static final Block HIVE = ModBlocks.ALVEOLI.get();
    /** Flesh filler material. */
    private static final Block FLESH = ModBlocks.FLESH_PLANKS.get();
    /** Stain / ground cover material. */
    private static final Block STAIN = ModBlocks.PARASITE_STAIN.get();
    /** Solid hive block for advanced structures. */
    private static final Block SOLID_HIVE = ModBlocks.SOLID_ALVEOLI.get();
    /** Parasite rubble for decoration. */
    private static final Block RUBBLE = ModBlocks.PARASITE_RUBBLE.get();
    /** Colony Heart block for colony center. */
    private static final Block COLONY_HEART = ModBlocks.COLONY_HEART.get();
    /** Colony Outpost block for extensions. */
    private static final Block COLONY_OUTPOST = ModBlocks.COLONY_OUTPOST.get();

    // ========== Build Rate ==========

    /** Maximum blocks to place per build tick (limits lag). */
    private static final int MAX_BLOCKS_PER_TICK = 3;

    /** Ticks between build ticks for a given colony. */
    private static final int BUILD_TICK_INTERVAL = 40; // 2 seconds

    // ========== Structure Sizing ==========

    /** Size of structure per level: level 1=5, 2=7, 3=9, 4=11, 5=13. */
    private static int getStructureSize(int level) {
        return 3 + level * 2;
    }

    /** Wall height per level: level 1=1, 2=2, 3=3, 4=4, 5=5. */
    private static int getWallHeight(int level) {
        return level;
    }

    // ========== Build Queue Entry ==========

    /**
     * Represents a single block placement in the build queue.
     */
    public static class BuildEntry {
        public final BlockPos pos;
        public final BlockState state;
        public final int priority; // lower = built first
        public final BuildCategory category;

        public BuildEntry(BlockPos pos, BlockState state, int priority, BuildCategory category) {
            this.pos = pos;
            this.state = state;
            this.priority = priority;
            this.category = category;
        }
    }

    /**
     * Categories for build entries, determining build order.
     */
    public enum BuildCategory {
        FOUNDATION(0),   // Floor blocks
        CORE(1),         // Colony Heart, critical structures
        WALLS(2),        // Walls and supports
        TENDRILS(3),     // Connecting tendrils
        PODS(4),         // Spawning pods / hive blocks
        DECORATION(5),   // Stain, rubble, decoration
        EXTENSION(6);    // Outpost extensions

        public final int sortOrder;

        BuildCategory(int sortOrder) {
            this.sortOrder = sortOrder;
        }
    }

    // ========== Structure Blueprint Generation ==========

    /**
     * Generates a complete blueprint (build queue) for a colony structure at the given level.
     * The blueprint is sorted by priority: foundation first, then core, walls, tendrils, pods, etc.
     *
     * @param center the center position of the colony
     * @param level  the colony level (1-5)
     * @return a sorted list of BuildEntry objects representing the full structure
     */
    public static List<BuildEntry> generateBlueprint(BlockPos center, int level) {
        List<BuildEntry> entries = new ArrayList<>();
        int size = getStructureSize(level);
        int halfSize = size / 2;
        int wallHeight = getWallHeight(level);

        // ── Foundation (Floor) ──
        for (int dx = -halfSize; dx <= halfSize; dx++) {
            for (int dz = -halfSize; dz <= halfSize; dz++) {
                BlockPos floorPos = center.offset(dx, -1, dz);
                // Outer edge uses structure block; inner uses biomass
                boolean isEdge = Math.abs(dx) == halfSize || Math.abs(dz) == halfSize;
                Block floorBlock = isEdge ? STRUCTURE : BIOMASS;
                entries.add(new BuildEntry(floorPos, floorBlock.defaultBlockState(),
                        0, BuildCategory.FOUNDATION));
            }
        }

        // ── Core (Colony Heart at center) ──
        entries.add(new BuildEntry(center, COLONY_HEART.defaultBlockState(),
                1, BuildCategory.CORE));

        // ── Walls ──
        if (level >= 2) {
            generateWalls(entries, center, halfSize, wallHeight);
        }

        // ── Interior Pillars/Supports ──
        if (level >= 3) {
            generatePillars(entries, center, halfSize, wallHeight);
        }

        // ── Tendrils (connections from center outward) ──
        generateTendrils(entries, center, halfSize, level);

        // ── Spawning Pods (Hive blocks) ──
        generateSpawningPods(entries, center, halfSize, level);

        // ── Rooms / Chambers ──
        if (level >= 3) {
            generateChambers(entries, center, halfSize, level);
        }

        // ── Defense Perimeter ──
        if (level >= 4) {
            generateDefensePerimeter(entries, center, halfSize + 2, level);
        }

        // ── Outpost Extensions ──
        if (level >= 5) {
            generateOutpostExtensions(entries, center, halfSize);
        }

        // ── Decoration (stain, rubble) ──
        generateDecoration(entries, center, halfSize, level);

        // Sort by category order, then priority
        entries.sort(Comparator.comparingInt((BuildEntry e) -> e.category.sortOrder)
                .thenComparingInt(e -> e.priority));

        return entries;
    }

    /**
     * Generates wall structures around the colony.
     */
    private static void generateWalls(List<BuildEntry> entries, BlockPos center,
                                       int halfSize, int wallHeight) {
        for (int y = 0; y < wallHeight; y++) {
            for (int dx = -halfSize; dx <= halfSize; dx++) {
                for (int dz = -halfSize; dz <= halfSize; dz++) {
                    boolean isWallEdge = (Math.abs(dx) == halfSize || Math.abs(dz) == halfSize);
                    boolean isCorner = (Math.abs(dx) == halfSize && Math.abs(dz) == halfSize);
                    if (!isWallEdge) continue;

                    BlockPos wallPos = center.offset(dx, y, dz);
                    Block wallBlock = isCorner ? STRUCTURE : FLESH;
                    entries.add(new BuildEntry(wallPos, wallBlock.defaultBlockState(),
                            y, BuildCategory.WALLS));
                }
            }
        }
    }

    /**
     * Generates interior pillars for structural support.
     */
    private static void generatePillars(List<BuildEntry> entries, BlockPos center,
                                         int halfSize, int wallHeight) {
        // Place pillars at 1/3 intervals
        int pillarOffset = halfSize / 2;
        if (pillarOffset < 1) pillarOffset = 1;

        for (int dx = -pillarOffset; dx <= pillarOffset; dx += pillarOffset) {
            for (int dz = -pillarOffset; dz <= pillarOffset; dz += pillarOffset) {
                if (dx == 0 && dz == 0) continue; // Skip center (colony heart)
                for (int y = 0; y < wallHeight; y++) {
                    BlockPos pillarPos = center.offset(dx, y, dz);
                    entries.add(new BuildEntry(pillarPos, STRUCTURE.defaultBlockState(),
                            y, BuildCategory.WALLS));
                }
            }
        }
    }

    /**
     * Generates tendril connections from center outward.
     */
    private static void generateTendrils(List<BuildEntry> entries, BlockPos center,
                                          int halfSize, int level) {
        int tendrilCount = 2 + level;
        // Place tendrils in 4 cardinal directions, extending outward
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            int tendrilLength = Math.min(halfSize + 1, 2 + level / 2);
            for (int i = 1; i <= tendrilLength; i++) {
                BlockPos tendrilPos = center.relative(dir, i).below();
                entries.add(new BuildEntry(tendrilPos, TENDRIL.defaultBlockState(),
                        i, BuildCategory.TENDRILS));
                // Place tendril blocks one above too for visual connection
                if (i > 1) {
                    entries.add(new BuildEntry(tendrilPos.above(), TENDRIL.defaultBlockState(),
                            i + 10, BuildCategory.TENDRILS));
                }
            }
        }
    }

    /**
     * Generates spawning pod (hive) blocks within the structure.
     */
    private static void generateSpawningPods(List<BuildEntry> entries, BlockPos center,
                                              int halfSize, int level) {
        int podCount = level;
        // Place pods symmetrically around the center
        int[][] podPositions = getPodPositions(level);
        for (int[] offset : podPositions) {
            BlockPos podPos = center.offset(offset[0], 0, offset[1]);
            Block podBlock = level >= 4 ? SOLID_HIVE : HIVE;
            entries.add(new BuildEntry(podPos, podBlock.defaultBlockState(),
                    0, BuildCategory.PODS));
        }
    }

    /**
     * Returns pod placement offsets for each colony level.
     */
    private static int[][] getPodPositions(int level) {
        return switch (level) {
            case 1 -> new int[][]{{1, 0}, {-1, 0}};
            case 2 -> new int[][]{{2, 0}, {-2, 0}, {0, 2}, {0, -2}};
            case 3 -> new int[][]{{2, 2}, {-2, 2}, {2, -2}, {-2, -2}, {0, 3}, {0, -3}};
            case 4 -> new int[][]{{3, 0}, {-3, 0}, {0, 3}, {0, -3}, {3, 3}, {-3, 3}, {3, -3}, {-3, -3}};
            default -> new int[][]{{4, 0}, {-4, 0}, {0, 4}, {0, -4}, {4, 4}, {-4, 4}, {4, -4}, {-4, -4},
                    {2, 4}, {-2, 4}, {2, -4}, {-2, -4}};
        };
    }

    /**
     * Generates interior chambers for advanced colonies.
     */
    private static void generateChambers(List<BuildEntry> entries, BlockPos center,
                                          int halfSize, int level) {
        // Create chamber walls dividing the interior
        int chamberOffset = halfSize / 2;
        if (chamberOffset < 2) return;

        for (int d = -chamberOffset; d <= chamberOffset; d++) {
            // North-south chamber wall
            BlockPos nsWall = center.offset(d, 0, 0);
            if (Math.abs(d) != chamberOffset && d != 0) {
                entries.add(new BuildEntry(nsWall, FLESH.defaultBlockState(),
                        0, BuildCategory.WALLS));
            }
            // East-west chamber wall
            BlockPos ewWall = center.offset(0, 0, d);
            if (Math.abs(d) != chamberOffset && d != 0) {
                entries.add(new BuildEntry(ewWall, FLESH.defaultBlockState(),
                        0, BuildCategory.WALLS));
            }
        }
    }

    /**
     * Generates a defense perimeter of solid blocks around the colony.
     */
    private static void generateDefensePerimeter(List<BuildEntry> entries, BlockPos center,
                                                  int perimeterHalf, int level) {
        // Outer ring of structure blocks at ground level
        for (int dx = -perimeterHalf; dx <= perimeterHalf; dx++) {
            for (int dz = -perimeterHalf; dz <= perimeterHalf; dz++) {
                boolean isPerimeter = (Math.abs(dx) == perimeterHalf || Math.abs(dz) == perimeterHalf);
                if (!isPerimeter) continue;

                BlockPos perimeterPos = center.offset(dx, -1, dz);
                entries.add(new BuildEntry(perimeterPos, STRUCTURE.defaultBlockState(),
                        0, BuildCategory.EXTENSION));

                // Tendril barriers on perimeter for level 5
                if (level >= 5 && (dx + dz) % 3 == 0) {
                    entries.add(new BuildEntry(perimeterPos.above(), TENDRIL.defaultBlockState(),
                            0, BuildCategory.EXTENSION));
                }
            }
        }
    }

    /**
     * Generates outpost extension blocks for level 5 colonies.
     */
    private static void generateOutpostExtensions(List<BuildEntry> entries, BlockPos center,
                                                   int halfSize) {
        // Place outpost blocks at the cardinal extremes
        Direction[] dirs = Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new);
        for (Direction dir : dirs) {
            BlockPos outpostPos = center.relative(dir, halfSize + 4);
            entries.add(new BuildEntry(outpostPos, COLONY_OUTPOST.defaultBlockState(),
                    0, BuildCategory.EXTENSION));
            // Small pad under outpost
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos padPos = outpostPos.offset(dx, -1, dz);
                    entries.add(new BuildEntry(padPos, BIOMASS.defaultBlockState(),
                            0, BuildCategory.EXTENSION));
                }
            }
        }
    }

    /**
     * Generates decorative elements (stain, rubble) around the colony.
     */
    private static void generateDecoration(List<BuildEntry> entries, BlockPos center,
                                            int halfSize, int level) {
        // Scatter stain and rubble around the structure
        int decorRange = halfSize + 2;
        for (int dx = -decorRange; dx <= decorRange; dx++) {
            for (int dz = -decorRange; dz <= decorRange; dz++) {
                // Only decorate outer ring and some random inner positions
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist < halfSize * 0.5) continue;
                if (dist > decorRange) continue;

                BlockPos decorPos = center.offset(dx, -1, dz);
                // Stain on ground outside the structure
                if (Math.abs(dx) > halfSize || Math.abs(dz) > halfSize) {
                    entries.add(new BuildEntry(decorPos, STAIN.defaultBlockState(),
                            (int) dist, BuildCategory.DECORATION));
                }
                // Occasional rubble
                if ((dx + dz) % 4 == 0) {
                    entries.add(new BuildEntry(decorPos.above(), RUBBLE.defaultBlockState(),
                            (int) dist + 5, BuildCategory.DECORATION));
                }
            }
        }
    }

    // ========== Incremental Building ==========

    /**
     * Processes the build queue for a colony, placing up to MAX_BLOCKS_PER_TICK blocks.
     * Returns the number of blocks actually placed.
     *
     * @param level      the server level
     * @param buildQueue the current build queue (will be modified in place)
     * @return the number of blocks placed this tick
     */
    public static int processBuildQueue(ServerLevel level, LinkedList<BuildEntry> buildQueue) {
        if (!ModConfigSystems.isColonyStructuresEnabled()) return 0;

        int placed = 0;
        Iterator<BuildEntry> it = buildQueue.iterator();

        while (it.hasNext() && placed < MAX_BLOCKS_PER_TICK) {
            BuildEntry entry = it.next();

            // Check if the position is already occupied with a non-air block
            if (!level.getBlockState(entry.pos).isAir()) {
                // If already a matching block, just skip this entry
                if (level.getBlockState(entry.pos).getBlock() == entry.state.getBlock()) {
                    it.remove();
                    continue;
                }
                // Don't overwrite existing non-air blocks (unless replacing dirt/grass for foundation)
                if (entry.category == BuildCategory.FOUNDATION) {
                    Block existing = level.getBlockState(entry.pos).getBlock();
                    if (existing != net.minecraft.world.level.block.Blocks.DIRT &&
                        existing != net.minecraft.world.level.block.Blocks.GRASS_BLOCK &&
                        existing != net.minecraft.world.level.block.Blocks.STONE) {
                        it.remove();
                        continue;
                    }
                } else {
                    it.remove();
                    continue;
                }
            }

            // Place the block
            level.setBlock(entry.pos, entry.state, 3);
            it.remove();
            placed++;
        }

        return placed;
    }

    /**
     * Generates an initial build queue for a new colony at the given position.
     * Adjusts the center position to the ground surface.
     *
     * @param level  the server level
     * @param center the desired center position
     * @param level_ the colony level (1-5)
     * @return the generated build queue, or empty list if structures are disabled
     */
    public static LinkedList<BuildEntry> createInitialBuildQueue(ServerLevel level, BlockPos center, int colonyLevel) {
        if (!ModConfigSystems.isColonyStructuresEnabled()) return new LinkedList<>();

        // Adjust center to ground level
        BlockPos groundCenter = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, center);

        List<BuildEntry> blueprint = generateBlueprint(groundCenter, colonyLevel);
        return new LinkedList<>(blueprint);
    }

    /**
     * Generates additional build entries for a colony level-up.
     * These entries represent the new structures that should be added
     * when a colony advances to a new level.
     *
     * @param center       the colony center
     * @param newLevel     the new colony level
     * @param currentLevel the previous colony level
     * @return list of new build entries for the level-up
     */
    public static List<BuildEntry> generateLevelUpAdditions(BlockPos center, int newLevel, int currentLevel) {
        List<BuildEntry> additions = new ArrayList<>();

        // Add walls if transitioning from level 1 to 2
        if (currentLevel < 2 && newLevel >= 2) {
            int halfSize = getStructureSize(newLevel) / 2;
            int wallHeight = getWallHeight(newLevel);
            generateWalls(additions, center, halfSize, wallHeight);
        }

        // Add additional pods
        for (int lvl = currentLevel + 1; lvl <= newLevel; lvl++) {
            int halfSize = getStructureSize(lvl) / 2;
            generateSpawningPods(additions, center, halfSize, lvl);
        }

        // Add chambers at level 3+
        if (newLevel >= 3 && currentLevel < 3) {
            int halfSize = getStructureSize(newLevel) / 2;
            generateChambers(additions, center, halfSize, newLevel);
        }

        // Add defense perimeter at level 4+
        if (newLevel >= 4 && currentLevel < 4) {
            int halfSize = getStructureSize(newLevel) / 2;
            generateDefensePerimeter(additions, center, halfSize + 2, newLevel);
        }

        // Add outpost extensions at level 5
        if (newLevel >= 5 && currentLevel < 5) {
            int halfSize = getStructureSize(newLevel) / 2;
            generateOutpostExtensions(additions, center, halfSize);
        }

        // Sort additions
        additions.sort(Comparator.comparingInt((BuildEntry e) -> e.category.sortOrder)
                .thenComparingInt(e -> e.priority));

        return additions;
    }

    // ========== Block Regeneration ==========

    /**
     * Regenerates a damaged colony block at the given position.
     * Checks if the position is within the colony's tracked blocks
     * and replaces air/broken blocks with the appropriate parasite block.
     *
     * @param level       the server level
     * @param pos         the position to regenerate
     * @param colonyLevel the colony level (determines block types)
     * @return true if a block was regenerated
     */
    public static boolean regenerateBlock(ServerLevel level, BlockPos pos, int colonyLevel) {
        BlockState currentState = level.getBlockState(pos);
        if (!currentState.isAir()) return false;

        // Determine what block should be here based on position relative to colony
        // For simplicity, regenerate with biomass for floor positions, flesh for walls
        BlockState regenState = BIOMASS.defaultBlockState();

        // If position is above ground level, it's likely a wall or tendril
        BlockPos groundCheck = pos.below();
        if (!level.getBlockState(groundCheck).isAir()) {
            // This might be a wall or support position
            regenState = colonyLevel >= 3 ? STRUCTURE.defaultBlockState() : FLESH.defaultBlockState();
        }

        level.setBlock(pos, regenState, 3);
        return true;
    }

    /**
     * Scans for missing blocks in a colony structure and queues them for regeneration.
     *
     * @param level         the server level
     * @param trackedBlocks the set of blocks that belong to the colony
     * @param regenQueue    the queue to add regeneration entries to
     * @param maxScanPerTick maximum blocks to scan per call
     * @return number of missing blocks found
     */
    public static int scanForMissingBlocks(ServerLevel level, Set<BlockPos> trackedBlocks,
                                            LinkedList<BuildEntry> regenQueue, int maxScanPerTick) {
        int missing = 0;
        int scanned = 0;

        for (BlockPos pos : trackedBlocks) {
            if (scanned >= maxScanPerTick) break;
            scanned++;

            if (level.getBlockState(pos).isAir()) {
                regenQueue.add(new BuildEntry(pos, BIOMASS.defaultBlockState(),
                        0, BuildCategory.FOUNDATION));
                missing++;
            }
        }

        return missing;
    }

    // ========== Utility ==========

    /**
     * Returns the total number of blocks in a blueprint for the given level.
     */
    public static int getBlueprintBlockCount(int level) {
        return generateBlueprint(BlockPos.ZERO, level).size();
    }

    /**
     * Checks whether a block at the given position is a parasite block.
     */
    public static boolean isParasiteBlock(Block block) {
        return block == BIOMASS || block == STRUCTURE || block == TENDRIL ||
               block == HIVE || block == FLESH || block == STAIN ||
               block == SOLID_HIVE || block == RUBBLE || block == COLONY_HEART ||
               block == COLONY_OUTPOST;
    }

    /**
     * Returns the set of all parasite block types used in colony structures.
     */
    public static Set<Block> getColonyBlockTypes() {
        return Set.of(BIOMASS, STRUCTURE, TENDRIL, HIVE, FLESH, STAIN,
                SOLID_HIVE, RUBBLE, COLONY_HEART, COLONY_OUTPOST);
    }
}
