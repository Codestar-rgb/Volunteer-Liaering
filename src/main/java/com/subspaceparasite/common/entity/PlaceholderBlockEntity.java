package com.subspaceparasite.common.entity;

import com.subspaceparasite.core.ModEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Placeholder BlockEntity for parasite block entities that don't yet
 * have custom implementations. Will be replaced with specific block
 * entity classes as they are implemented.
 * <p>
 * <b>Deprecated:</b> Most block entities now have specific implementations
 * using the {@code typedBlockEntity()} helper in ModEntities. This class
 * is retained only for block entity registrations that still use the
 * legacy {@code blockEntity()} helper method.
 *
 * @deprecated Use specific BlockEntity subclass implementations instead.
 *             See {@code com.subspaceparasite.common.block.entity} package.
 */
@Deprecated
public class PlaceholderBlockEntity extends BlockEntity {

    public PlaceholderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
