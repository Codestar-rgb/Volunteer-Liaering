package com.subspaceparasite.common.block.entity;

import com.subspaceparasite.core.ModBlocks;
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

/**
 * Block entity for the Colony Outpost — a colony extension structure.
 * <p>
 * Tracks a parent Colony Heart position, its own outpost level,
 * and validates the parent connection each tick. Extends the effective
 * colony radius when linked to a valid parent.
 */
public class ColonyOutpostBlockEntity extends BlockEntity {

    private BlockPos parentHeartPos = null;
    private int outpostLevel = 1;
    private int validationCooldown = 0;
    private static final int VALIDATION_INTERVAL = 200; // ticks between parent checks
    private static final float RADIUS_EXTENSION_PER_LEVEL = 8.0f;

    public ColonyOutpostBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // ==================== Tick ====================

    public static void serverTick(Level level, BlockPos pos, BlockState state, ColonyOutpostBlockEntity be) {
        if (level.isClientSide) return;

        // Periodically validate parent connection
        be.validationCooldown--;
        if (be.validationCooldown <= 0) {
            be.validationCooldown = VALIDATION_INTERVAL;
            be.validateParentConnection(level);
        }
    }

    /**
     * Validates that the parent Colony Heart still exists and is intact.
     * If the parent is destroyed, this outpost becomes orphaned.
     */
    private void validateParentConnection(Level level) {
        if (parentHeartPos == null) return;

        if (!level.isLoaded(parentHeartPos)) return;

        BlockEntity be = level.getBlockEntity(parentHeartPos);
        if (!(be instanceof ColonyHeartBlockEntity)) {
            // Parent destroyed — orphan this outpost
            parentHeartPos = null;
            setChanged();
            return;
        }

        // Parent exists — extend its effective radius
        ColonyHeartBlockEntity heart = (ColonyHeartBlockEntity) be;
        float extension = RADIUS_EXTENSION_PER_LEVEL * outpostLevel;
        float currentRadius = heart.getColonyRadius();
        // The outpost extends the colony radius if within range
        double dist = worldPosition.distManhattan(parentHeartPos);
        if (dist <= currentRadius + extension) {
            // Outpost is within effective range — radius is implicitly extended
            // via the outpost's RADIUS_EXTENSION_PER_LEVEL being added by ColonyHeart logic
        }
    }

    /**
     * Returns the radius extension this outpost contributes.
     */
    public float getRadiusExtension() {
        return RADIUS_EXTENSION_PER_LEVEL * outpostLevel;
    }

    // ==================== NBT ====================

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (parentHeartPos != null) {
            tag.put("ParentHeartPos", NbtUtils.writeBlockPos(parentHeartPos));
        }
        tag.putInt("OutpostLevel", outpostLevel);
        tag.putInt("ValidationCooldown", validationCooldown);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("ParentHeartPos")) {
            parentHeartPos = NbtUtils.readBlockPos(tag.getCompound("ParentHeartPos"));
        }
        if (tag.contains("OutpostLevel")) outpostLevel = tag.getInt("OutpostLevel");
        if (tag.contains("ValidationCooldown")) validationCooldown = tag.getInt("ValidationCooldown");
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    // ==================== Accessors ====================

    public BlockPos getParentHeartPos() { return parentHeartPos; }
    public void setParentHeartPos(BlockPos pos) { this.parentHeartPos = pos; setChanged(); }

    public int getOutpostLevel() { return outpostLevel; }
    public void setOutpostLevel(int level) { this.outpostLevel = level; setChanged(); }
}
