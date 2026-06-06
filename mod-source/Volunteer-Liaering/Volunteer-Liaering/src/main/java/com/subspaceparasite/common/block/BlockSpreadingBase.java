package com.subspaceparasite.common.block;

import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.common.world.ModWorldData;
import com.subspaceparasite.config.ModConfigSystems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Base class for phase-aware multi-spreading blocks.
 * <p>
 * These blocks spread to adjacent blocks via BlockPurifyMappings,
 * converting vanilla blocks to their infested equivalents. The spread
 * rate and range are influenced by the current evolution phase:
 * <ul>
 *   <li>Phase 0-1: Spread to adjacent faces only, slow rate</li>
 *   <li>Phase 2-3: Spread to diagonal neighbors as well</li>
 *   <li>Phase 4+: Spread faster with higher chance</li>
 * </ul>
 * <p>
 * Used for spreading blocks like HARLEQUINN_GRASS, HARLESKINN_BLOCK,
 * and POLAND_SKIN_BLOCK.
 */
public class BlockSpreadingBase extends Block {

    /** Base chance per random tick to attempt spreading. */
    private static final float BASE_SPREAD_CHANCE = 0.05F;

    /** Additional chance per phase level. */
    private static final float CHANCE_PER_PHASE = 0.02F;

    /** Maximum spread chance cap. */
    private static final float MAX_SPREAD_CHANCE = 0.30F;

    public BlockSpreadingBase(Properties properties) {
        super(properties);
    }

    /**
     * Random tick: attempts to spread to adjacent blocks.
     * Only runs on the server side.
     */
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!ModConfigSystems.isInfectionEnabled()) return;
        BlockPurifyMappings.init();

        EvoPhase phase = ModWorldData.get(level).getCurrentPhase();
        float chance = Math.min(MAX_SPREAD_CHANCE,
                BASE_SPREAD_CHANCE + phase.getPhaseNumber() * CHANCE_PER_PHASE);

        if (random.nextFloat() > chance) return;

        // Try to spread to a random adjacent block
        BlockPos targetPos = getRandomSpreadTarget(pos, random, phase);
        if (targetPos == null) return;

        BlockState targetState = level.getBlockState(targetPos);
        Block infestedBlock = BlockPurifyMappings.getInfestedBlock(targetState.getBlock());
        if (infestedBlock != null) {
            level.setBlockAndUpdate(targetPos, infestedBlock.defaultBlockState());
        }
    }

    /**
     * Returns a random position to attempt spreading to.
     * At higher phases, diagonal positions are also considered.
     */
    protected BlockPos getRandomSpreadTarget(BlockPos pos, RandomSource random, EvoPhase phase) {
        // Always try adjacent faces (6 directions)
        Direction direction = Direction.getRandom(random);
        BlockPos adjacent = pos.relative(direction);

        // At phase 2+, also try diagonal positions
        if (phase.getPhaseNumber() >= 2 && random.nextFloat() < 0.3F) {
            int dx = random.nextInt(3) - 1;
            int dy = random.nextInt(3) - 1;
            int dz = random.nextInt(3) - 1;
            if (dx != 0 || dy != 0 || dz != 0) {
                return pos.offset(dx, dy, dz);
            }
        }

        return adjacent;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return ModConfigSystems.isInfectionEnabled();
    }
}
