package com.subspaceparasite.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Placeholder BlockEntity for parasite block entities that don't yet
 * have custom implementations. Will be replaced with specific block
 * entity classes as they are implemented.
 */
public class PlaceholderBlockEntity extends BlockEntity {

    public PlaceholderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
