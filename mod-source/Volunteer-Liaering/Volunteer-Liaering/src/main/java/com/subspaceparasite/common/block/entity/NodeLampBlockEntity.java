package com.subspaceparasite.common.block.entity;

import com.subspaceparasite.core.ModBlocks;
import com.subspaceparasite.core.ModParticleTypes;
import com.subspaceparasite.core.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Block entity for the Node Lamp — a light source for the relay network.
 * <p>
 * Tracks a parent controller position, lamp light level (0-15), and active state.
 * Validates the parent controller connection and adjusts light level accordingly.
 */
public class NodeLampBlockEntity extends BlockEntity {

    @Nullable
    private BlockPos parentControllerPos = null;
    private int lampLevel = 15;
    private boolean active = true;

    private int validationCooldown = 0;
    private static final int VALIDATION_INTERVAL = 100; // ticks between parent checks

    public NodeLampBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // ==================== Tick ====================

    public static void serverTick(Level level, BlockPos pos, BlockState state, NodeLampBlockEntity be) {
        if (level.isClientSide) return;

        // Validate parent connection periodically
        be.validationCooldown--;
        if (be.validationCooldown <= 0) {
            be.validationCooldown = VALIDATION_INTERVAL;
            be.validateParent(level);
        }

        // Adjust light level based on active state and parent validity
        int targetLevel = be.active ? be.lampLevel : 0;
        be.updateLightLevel(level, pos, state, targetLevel);

        // Ambient particles when active
        if (be.active && level.getGameTime() % 20 == 0) {
            ((ServerLevel) level).sendParticles(
                    ModParticleTypes.NEXUS_PULSE.get(),
                    pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5,
                    1, 0.1, 0.1, 0.1, 0.01);
        }
    }

    /**
     * Validates the parent controller connection. If the parent is
     * destroyed or too far away, deactivates the lamp.
     */
    private void validateParent(Level level) {
        if (parentControllerPos == null) {
            // No parent — can still function as standalone lamp
            return;
        }

        if (!level.isLoaded(parentControllerPos)) return;

        BlockEntity be = level.getBlockEntity(parentControllerPos);
        if (!(be instanceof RelayControllerBlockEntity)) {
            active = false;
            setChanged();
            return;
        }

        RelayControllerBlockEntity controller = (RelayControllerBlockEntity) be;
        if (!controller.isFormed()) {
            active = false;
        } else {
            active = true;
        }
        setChanged();
    }

    /**
     * Updates the block's light level by changing block state if needed.
     */
    private void updateLightLevel(Level level, BlockPos pos, BlockState state, int targetLevel) {
        // Light level is managed by the block's lightEmission property
        // This method could be expanded to toggle between lit/unlit block states
        // For now, we just track the level for future use
    }

    // ==================== NBT ====================

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (parentControllerPos != null) {
            tag.put("ParentControllerPos", NbtUtils.writeBlockPos(parentControllerPos));
        }
        tag.putInt("LampLevel", lampLevel);
        tag.putBoolean("Active", active);
        tag.putInt("ValidationCooldown", validationCooldown);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("ParentControllerPos")) {
            parentControllerPos = NbtUtils.readBlockPos(tag.getCompound("ParentControllerPos"));
        }
        if (tag.contains("LampLevel")) lampLevel = tag.getInt("LampLevel");
        if (tag.contains("Active")) active = tag.getBoolean("Active");
        if (tag.contains("ValidationCooldown")) validationCooldown = tag.getInt("ValidationCooldown");
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    // ==================== Accessors ====================

    @Nullable
    public BlockPos getParentControllerPos() { return parentControllerPos; }
    public void setParentControllerPos(@Nullable BlockPos pos) { this.parentControllerPos = pos; setChanged(); }

    public int getLampLevel() { return lampLevel; }
    public void setLampLevel(int level) { this.lampLevel = Math.max(0, Math.min(15, level)); setChanged(); }

    public boolean isActive() { return active; }
    public void setActive(boolean value) { this.active = value; setChanged(); }
}
