package com.subspaceparasite.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Infuser Furnace block entity - a dual-purpose furnace that can both
 * smelt and infuse items with parasite essence.
 * Has 6 slots: smelt_input(0), fuel(1), smelt_output(2), infuse_input(3), infuse_output(4), bottle_output(5)
 * Ported from TileEntityInfuserFurnace (1.12) to 1.20.1.
 */
public class InfuserFurnaceBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {

    public static final int SLOT_SMELT_IN = 0;
    public static final int SLOT_FUEL = 1;
    public static final int SLOT_SMELT_OUT = 2;
    public static final int SLOT_INFUSE_IN = 3;
    public static final int SLOT_INFUSE_OUT = 4;
    public static final int SLOT_BOTTLE_OUT = 5;

    private static final int COOK_TIME_TOTAL = 200;
    private static final int[] SLOTS_TOP = new int[]{0};
    private static final int[] SLOTS_BOTTOM = new int[]{2, 4, 5};
    private static final int[] SLOTS_SIDES = new int[]{1, 3};

    private final NonNullList<ItemStack> items = NonNullList.withSize(6, ItemStack.EMPTY);
    private int burnTime;
    private int currentBurnTime;
    private int cookTimeSmelt;
    private int cookTimeInfuse;

    public InfuserFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(null, pos, state);
    }

    public InfuserFurnaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // ==================== Tick Logic ====================

    public static void serverTick(Level level, BlockPos pos, BlockState state, InfuserFurnaceBlockEntity blockEntity) {
        boolean wasBurning = blockEntity.isBurning();
        boolean dirty = false;

        if (blockEntity.isBurning()) {
            blockEntity.burnTime--;
        }

        // Try to consume fuel
        ItemStack fuel = blockEntity.items.get(SLOT_FUEL);
        if (!blockEntity.isBurning() && blockEntity.canSmelt() && !fuel.isEmpty()) {
            int fuelBurn = net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.getFuel().getOrDefault(fuel.getItem(), 0);
            if (fuelBurn > 0) {
                blockEntity.burnTime = fuelBurn;
                blockEntity.currentBurnTime = fuelBurn;
                dirty = true;
                Item fuelItem = fuel.getItem();
                fuel.shrink(1);
                if (fuel.isEmpty()) {
                    Item container = fuelItem.getCraftingRemainingItem();
                    blockEntity.items.set(SLOT_FUEL, container != null ? new ItemStack(container) : ItemStack.EMPTY);
                }
            }
        }

        // Smelting
        if (blockEntity.isBurning() && blockEntity.canSmelt()) {
            blockEntity.cookTimeSmelt++;
            if (blockEntity.cookTimeSmelt >= COOK_TIME_TOTAL) {
                blockEntity.cookTimeSmelt = 0;
                blockEntity.doSmelt();
                dirty = true;
            }
        } else {
            blockEntity.cookTimeSmelt = 0;
        }

        // Infusing (placeholder - requires recipe system integration)
        // TODO: Implement infusion recipe lookup when recipe system is ready
        blockEntity.cookTimeInfuse = 0;

        if (wasBurning != blockEntity.isBurning()) {
            dirty = true;
        }

        if (dirty) {
            blockEntity.setChanged();
        }
    }

    public boolean isBurning() {
        return this.burnTime > 0;
    }

    // ==================== Smelting ====================

    private boolean canSmelt() {
        ItemStack input = this.items.get(SLOT_SMELT_IN);
        if (input.isEmpty()) return false;

        if (this.level == null) return false;
        Optional<?> recipe = this.level.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, new SimpleContainer(input), this.level);
        if (recipe.isEmpty()) return false;

        ItemStack result = ((AbstractCookingRecipe) recipe.get()).getResultItem(this.level.registryAccess());
        if (result.isEmpty()) return false;

        ItemStack output = this.items.get(SLOT_SMELT_OUT);
        return canOutputStack(output, result);
    }

    private void doSmelt() {
        if (!this.canSmelt()) return;

        ItemStack input = this.items.get(SLOT_SMELT_IN);
        if (this.level == null) return;

        Optional<? extends AbstractCookingRecipe> recipe = this.level.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, new SimpleContainer(input), this.level);
        if (recipe.isEmpty()) return;

        ItemStack result = recipe.get().getResultItem(this.level.registryAccess()).copy();
        ItemStack output = this.items.get(SLOT_SMELT_OUT);
        this.items.set(SLOT_SMELT_OUT, pushToOutput(output, result));
        input.shrink(1);
    }

    private boolean canOutputStack(ItemStack existing, ItemStack toAdd) {
        if (toAdd.isEmpty()) return false;
        if (existing.isEmpty()) return true;
        if (!ItemStack.isSameItemSameTags(existing, toAdd)) return false;
        int total = existing.getCount() + toAdd.getCount();
        return total <= this.getMaxStackSize() && total <= existing.getMaxStackSize();
    }

    private ItemStack pushToOutput(ItemStack existing, ItemStack toAdd) {
        if (existing.isEmpty()) return toAdd;
        existing.grow(toAdd.getCount());
        return existing;
    }

    // ==================== Container ====================

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.subspaceparasite.infuser_furnace");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory playerInventory) {
        // TODO: Create ContainerInfuserFurnace (custom menu for 6-slot dual-purpose furnace)
        // Cannot use FurnaceMenu — it requires AbstractFurnaceBlockEntity, not BaseContainerBlockEntity.
        // Returning null prevents UI opening; a custom AbstractContainerMenu must be implemented.
        // See: InfuserFurnaceBlockEntity has slots 0-5 (smelt_in, fuel, smelt_out, infuse_in, infuse_out, bottle_out)
        return null;
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
        return ContainerHelper.removeItem(this.items, index, count);
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
        if (index == SLOT_SMELT_OUT || index == SLOT_INFUSE_OUT || index == SLOT_BOTTLE_OUT) {
            return false;
        }
        if (index == SLOT_FUEL) {
            return net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.isFuel(stack);
        }
        return true;
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    // ==================== Sided Inventory ====================

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) return SLOTS_BOTTOM;
        if (side == Direction.UP) return SLOTS_TOP;
        return SLOTS_SIDES;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
        return this.canPlaceItem(index, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return index == SLOT_SMELT_OUT || index == SLOT_INFUSE_OUT || index == SLOT_BOTTLE_OUT;
    }

    // ==================== Data Accessors (for container data sync) ====================

    public int getBurnTime() { return this.burnTime; }
    public int getCurrentBurnTime() { return this.currentBurnTime; }
    public int getCookTimeSmelt() { return this.cookTimeSmelt; }
    public int getCookTimeInfuse() { return this.cookTimeInfuse; }

    public void setBurnTime(int v) { this.burnTime = v; }
    public void setCurrentBurnTime(int v) { this.currentBurnTime = v; }
    public void setCookTimeSmelt(int v) { this.cookTimeSmelt = v; }
    public void setCookTimeInfuse(int v) { this.cookTimeInfuse = v; }

    // ==================== NBT ====================

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.items.clear();
        ListTag list = tag.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag c = list.getCompound(i);
            int slot = c.getByte("Slot") & 0xFF;
            if (slot >= 0 && slot < this.items.size()) {
                this.items.set(slot, ItemStack.of(c));
            }
        }
        this.burnTime = tag.getInt("BurnTime");
        this.currentBurnTime = tag.getInt("CurrentBurnTime");
        this.cookTimeSmelt = tag.getInt("CookSmelt");
        this.cookTimeInfuse = tag.getInt("CookInfuse");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ListTag list = new ListTag();
        for (int i = 0; i < this.items.size(); i++) {
            ItemStack stack = this.items.get(i);
            if (!stack.isEmpty()) {
                CompoundTag c = new CompoundTag();
                c.putByte("Slot", (byte) i);
                stack.save(c);
                list.add(c);
            }
        }
        tag.put("Items", list);
        tag.putInt("BurnTime", this.burnTime);
        tag.putInt("CurrentBurnTime", this.currentBurnTime);
        tag.putInt("CookSmelt", this.cookTimeSmelt);
        tag.putInt("CookInfuse", this.cookTimeInfuse);
    }
}
