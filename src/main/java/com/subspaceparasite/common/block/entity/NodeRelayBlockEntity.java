package com.subspaceparasite.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Node Relay block entity - a relay node that connects to a RelayController.
 * Stores the position of its parent controller and provides relay
 * functionality for the scanner system.
 * Ported from TileEntityNodeRelay (1.12) to 1.20.1.
 */
public class NodeRelayBlockEntity extends BlockEntity {

    @Nullable
    private BlockPos controllerPos;

    public NodeRelayBlockEntity(BlockPos pos, BlockState state) {
        super(null, pos, state);
    }

    public NodeRelayBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // ==================== Controller Link ====================

    public void setControllerPos(@Nullable BlockPos pos) {
        this.controllerPos = pos;
        this.setChanged();
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(
                    pos == null ? this.worldPosition : this.worldPosition,
                    this.level.getBlockState(this.worldPosition),
                    this.level.getBlockState(this.worldPosition),
                    3
            );
        }
    }

    @Nullable
    public BlockPos getControllerPos() {
        return this.controllerPos;
    }

    /**
     * Check if this relay is linked to a controller.
     */
    public boolean isLinked() {
        return this.controllerPos != null;
    }

    // ==================== NBT ====================

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("ControllerPos", Tag.TAG_COMPOUND)) {
            this.controllerPos = NbtUtils.readBlockPos(tag.getCompound("ControllerPos"));
        } else {
            this.controllerPos = null;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.controllerPos != null) {
            tag.put("ControllerPos", NbtUtils.writeBlockPos(this.controllerPos));
        }
    }

    // ==================== Sync Packets ====================

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // ==================== Tick ====================

    public static void serverTick(Level level, BlockPos pos, BlockState state, NodeRelayBlockEntity blockEntity) {
        // Node relays are passive - their tick is handled by the controller
        // Occasional validation that controller still exists
        if (blockEntity.controllerPos != null && level.getGameTime() % 200 == 0) {
            BlockEntity be = level.getBlockEntity(blockEntity.controllerPos);
            if (!(be instanceof RelayControllerBlockEntity)) {
                blockEntity.setControllerPos(null);
            }
        }
    }
}
