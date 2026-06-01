package com.subspaceparasite.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Parasite Loot block entity - a 27-slot container dropped by parasites
 * or found in parasite structures. Provides fullness tracking for
 * display purposes.
 * Ported from TileEntityParasiteLoot (1.12) to 1.20.1.
 */
public class ParasiteLootBlockEntity extends BaseContainerBlockEntity {

    private static final int INV_SIZE = 27;
    private final NonNullList<ItemStack> items = NonNullList.withSize(INV_SIZE, ItemStack.EMPTY);

    public ParasiteLootBlockEntity(BlockPos pos, BlockState state) {
        super(null, pos, state);
    }

    public ParasiteLootBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // ==================== Fullness Tracking ====================

    /**
     * Get the fullness as a 0.0-1.0 fraction.
     */
    public float getFullness() {
        int total = getTotalSlots();
        if (total <= 0) return 0.0f;
        return (float) getUsedSlotCount() / (float) total;
    }

    /**
     * Get the fullness as an integer 0-1000 (for container data sync).
     */
    public int getFullnessField() {
        return (int) Math.round(getFullness() * 1000.0);
    }

    public int getTotalSlots() {
        return this.items.size();
    }

    public int getFreeSlotCount() {
        int free = 0;
        for (ItemStack stack : this.items) {
            if (stack.isEmpty()) free++;
        }
        return free;
    }

    public int getUsedSlotCount() {
        return getTotalSlots() - getFreeSlotCount();
    }

    // ==================== Container ====================

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.subspaceparasite.parasite_loot");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory playerInventory) {
        return ChestMenu.threeRows(id, playerInventory, this);
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return this.items.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack result = ContainerHelper.removeItem(this.items, index, count);
        if (!result.isEmpty()) {
            this.setChanged();
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(this.items, index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        this.items.set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.level == null) return false;
        if (this.level.getBlockEntity(this.worldPosition) != this) return false;
        return player.distanceToSqr(
                this.worldPosition.getX() + 0.5,
                this.worldPosition.getY() + 0.5,
                this.worldPosition.getZ() + 0.5
        ) <= 64.0;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        return true;
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    // ==================== NBT ====================

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.items.clear();
        ContainerHelper.loadAllItems(tag, this.items);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.items);
    }
}
