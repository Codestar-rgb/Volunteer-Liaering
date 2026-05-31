package com.subspaceparasite.common.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

/**
 * 固体肺泡方块 - 致密坚固的肺泡变体
 * 基于原版SRP BlockSolidAlveoli移植
 */
public class BlockSolidAlveoli extends Block {
    public BlockSolidAlveoli() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_PURPLE)
                .strength(2.0F, 6.0F)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops());
    }
}
