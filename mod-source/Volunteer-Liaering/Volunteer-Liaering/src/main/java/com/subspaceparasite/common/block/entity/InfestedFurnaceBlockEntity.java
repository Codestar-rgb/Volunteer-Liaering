package com.subspaceparasite.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Infested Furnace block entity - a furnace variant used in parasite structures.
 * Functions as a standard furnace but with parasite-themed behavior.
 * Ported from TileEntityInfestedFurnace (1.12) to 1.20.1.
 */
public class InfestedFurnaceBlockEntity extends AbstractFurnaceBlockEntity {

    private static final int[] SLOTS_TOP = new int[]{0};
    private static final int[] SLOTS_BOTTOM = new int[]{2, 1};
    private static final int[] SLOTS_SIDES = new int[]{1};

    public InfestedFurnaceBlockEntity(BlockPos pos, BlockState state) {
        // Use the FURNACE type as a fallback; ModEntities will override with the proper type
        super(null, pos, state, RecipeType.SMELTING);
    }

    public InfestedFurnaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, RecipeType.SMELTING);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.subspaceparasite.infested_furnace");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory playerInventory) {
        return new FurnaceMenu(id, playerInventory, this, this.dataAccess);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            return SLOTS_BOTTOM;
        } else if (side == Direction.UP) {
            return SLOTS_TOP;
        } else {
            return SLOTS_SIDES;
        }
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
        return this.canPlaceItem(index, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        if (direction == Direction.DOWN && index == 1) {
            return stack.getItem() == Items.BUCKET;
        }
        return true;
    }

    /**
     * Static tick method for server-side tick handling.
     */
    public static void serverTick(Level level, BlockPos pos, BlockState state, InfestedFurnaceBlockEntity blockEntity) {
        // Delegate to AbstractFurnaceBlockEntity's tick logic
        AbstractFurnaceBlockEntity.serverTick(level, pos, state, blockEntity);
    }
}
