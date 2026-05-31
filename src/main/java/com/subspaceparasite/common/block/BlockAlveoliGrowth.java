package com.subspaceparasite.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/**
 * 生长型肺泡方块 - 具有主动扩散能力的肺泡变体
 * 基于原版SRP BlockAlveoliGrowth移植
 * 
 * 特性:
 * - 随机向周围扩散
 * - 可以转化为其他肺泡类型
 */
public class BlockAlveoliGrowth extends Block {
    public BlockAlveoliGrowth() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_PURPLE)
                .strength(1.0F, 3.0F)
                .sound(SoundType.WOOL)
                .noOcclusion()
                .randomTicks());
    }

    @Override
    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
        // 尝试向周围扩散
        this.trySpread(worldIn, pos, random);
        
        // 小概率转化为固体肺泡
        if (random.nextInt(40) == 0) {
            worldIn.setBlock(pos, getSolidAlveoliBlock().defaultBlockState(), 2);
        }
    }

    private void trySpread(ServerLevel worldIn, BlockPos pos, RandomSource random) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        
        for (int i = 0; i < 6; i++) {
            int dx = random.nextInt(5) - 2;
            int dy = random.nextInt(3) - 1;
            int dz = random.nextInt(5) - 2;
            mutable.setWithOffset(pos, dx, dy, dz);
            
            if (worldIn.isEmptyBlock(mutable)) {
                BlockState belowState = worldIn.getBlockState(mutable.below());
                // 可以在泥土、石头等方块上生长
                if (isGrowOnBlock(belowState)) {
                    if (random.nextInt(8) == 0) {
                        worldIn.setBlock(mutable, this.defaultBlockState(), 2);
                    }
                }
            }
        }
    }

    private boolean isGrowOnBlock(BlockState state) {
        return state.is(net.minecraft.world.level.block.Blocks.DIRT) ||
               state.is(net.minecraft.world.level.block.Blocks.GRASS_BLOCK) ||
               state.is(net.minecraft.world.level.block.Blocks.STONE) ||
               state.is(net.minecraft.world.level.block.Blocks.COBBLESTONE);
    }

    private Block getSolidAlveoliBlock() {
        try {
            Class<?> modBlocksClass = Class.forName("com.subspaceparasite.core.ModBlocks");
            Object registryObject = modBlocksClass.getDeclaredField("SOLID_ALVEOLI").get(null);
            Class<?> registryObjectClass = Class.forName("net.minecraftforge.registries.RegistryObject");
            return (Block) registryObjectClass.getDeclaredMethod("get").invoke(registryObject);
        } catch (Exception e) {
            return net.minecraft.world.level.block.Blocks.AMETHYST_BLOCK; // Fallback
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }
}
