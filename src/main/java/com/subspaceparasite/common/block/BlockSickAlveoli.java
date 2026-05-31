package com.subspaceparasite.common.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

/**
 * 病变肺泡方块 - 肺泡的病变变体
 * 基于原版SRP BlockSickAlveoli移植
 */
public class BlockSickAlveoli extends Block {
    public BlockSickAlveoli() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_GREEN)
                .strength(1.0F, 3.0F)
                .sound(SoundType.WOOL)
                .noOcclusion());
    }
}
