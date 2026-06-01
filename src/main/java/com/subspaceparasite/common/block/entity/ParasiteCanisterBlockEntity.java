package com.subspaceparasite.common.block.entity;

import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.core.ModParticleTypes;
import com.subspaceparasite.core.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

/**
 * Block entity for the Parasite Canister — stores a parasite entity.
 * <p>
 * Tracks the stored entity type, parasite type, and fill level (0-100).
 * Visual animation is based on the fill level.
 */
public class ParasiteCanisterBlockEntity extends BlockEntity {

    @Nullable
    private ResourceLocation storedEntityType = null;
    @Nullable
    private ParasiteType storedParasiteType = null;
    private int fillLevel = 0; // 0-100

    private int animationTick = 0;

    public ParasiteCanisterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // ==================== Tick ====================

    public static void serverTick(Level level, BlockPos pos, BlockState state, ParasiteCanisterBlockEntity be) {
        if (level.isClientSide) return;

        // Visual animation based on fill level
        be.animationTick++;
        if (be.fillLevel > 0 && be.animationTick % (40 - be.fillLevel / 4) == 0) {
            ((ServerLevel) level).sendParticles(ParticleTypes.ITEM_SLIME,
                    pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 0.4,
                    pos.getY() + 0.3 + level.random.nextDouble() * 0.4,
                    pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 0.4,
                    1, 0, 0.02, 0, 0);
        }
    }

    // ==================== Storage API ====================

    /**
     * Stores a parasite in the canister.
     *
     * @param entityType the entity type ResourceLocation
     * @param parasiteType the ParasiteType classification
     * @param fill the fill level (0-100)
     */
    public void storeParasite(ResourceLocation entityType, ParasiteType parasiteType, int fill) {
        this.storedEntityType = entityType;
        this.storedParasiteType = parasiteType;
        this.fillLevel = Math.max(0, Math.min(100, fill));
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    /**
     * Empties the canister, returning the stored parasite info.
     */
    public void emptyCanister() {
        this.storedEntityType = null;
        this.storedParasiteType = null;
        this.fillLevel = 0;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public boolean hasStoredParasite() {
        return storedEntityType != null && fillLevel > 0;
    }

    // ==================== NBT ====================

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (storedEntityType != null) {
            tag.putString("StoredEntityType", storedEntityType.toString());
        }
        if (storedParasiteType != null) {
            tag.putString("StoredParasiteType", storedParasiteType.getSerializedName());
        }
        tag.putInt("FillLevel", fillLevel);
        tag.putInt("AnimationTick", animationTick);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("StoredEntityType")) {
            storedEntityType = new ResourceLocation(tag.getString("StoredEntityType"));
        } else {
            storedEntityType = null;
        }
        if (tag.contains("StoredParasiteType")) {
            String name = tag.getString("StoredParasiteType");
            try {
                storedParasiteType = ParasiteType.valueOf(name.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Try by serial name
                for (ParasiteType pt : ParasiteType.values()) {
                    if (pt.getSerializedName().equals(name)) {
                        storedParasiteType = pt;
                        break;
                    }
                }
            }
        } else {
            storedParasiteType = null;
        }
        fillLevel = tag.contains("FillLevel") ? tag.getInt("FillLevel") : 0;
        animationTick = tag.contains("AnimationTick") ? tag.getInt("AnimationTick") : 0;
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    // ==================== Accessors ====================

    @Nullable
    public ResourceLocation getStoredEntityType() { return storedEntityType; }
    @Nullable
    public ParasiteType getStoredParasiteType() { return storedParasiteType; }
    public int getFillLevel() { return fillLevel; }
    public void setFillLevel(int level) { this.fillLevel = Math.max(0, Math.min(100, level)); setChanged(); }
}
