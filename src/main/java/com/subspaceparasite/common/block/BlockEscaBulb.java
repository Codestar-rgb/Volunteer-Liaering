package com.subspaceparasite.common.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

/**
 * 发光菌光体方块 - 16 色变体的生物发光方块
 * 基于原版 SRP BlockEscaBulb 移植
 * 
 * 特性:
 * - 低硬度，易破坏
 * - 透明渲染
 * - 未来可添加动态光源
 */
public class BlockEscaBulb extends Block {
    public BlockEscaBulb() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_PURPLE)
                .strength(0.0F)
                .sound(SoundType.GRASS)
                .noOcclusion());
    }
}
