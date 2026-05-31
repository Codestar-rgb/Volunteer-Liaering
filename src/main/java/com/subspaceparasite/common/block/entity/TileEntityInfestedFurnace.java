package com.subspaceparasite.common.block.entity;

import com.subspaceparasite.core.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Infested Furnace Block Entity - 感染熔炉
 * 基于原版SRP TileEntityInfestedFurnace移植到1.20.1
 * 
 * 功能:
 * - 标准熔炉烧炼功能
 * - 感染物品处理
 */
public class TileEntityInfestedFurnace extends BlockEntity implements MenuProvider {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_FUEL = 1;
    public static final int SLOT_OUTPUT = 2;
    public static final int NUM_SLOTS = 3;

    private final ItemStackHandler itemHandler = createHandler();
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    private int burnTime = 0;
    private int currentBurnTime = 0;
    private int cookTime = 0;
    private int totalCookTime = 200;

    private final ContainerData dataTracker = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> TileEntityInfestedFurnace.this.cookTime;
                case 1 -> TileEntityInfestedFurnace.this.totalCookTime;
                case 2 -> TileEntityInfestedFurnace.this.burnTime;
                case 3 -> TileEntityInfestedFurnace.this.currentBurnTime;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> TileEntityInfestedFurnace.this.cookTime = value;
                case 1 -> TileEntityInfestedFurnace.this.totalCookTime = value;
                case 2 -> TileEntityInfestedFurnace.this.burnTime = value;
                case 3 -> TileEntityInfestedFurnace.this.currentBurnTime = value;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public TileEntityInfestedFurnace(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.INFESTED_FURNACE.get(), pos, blockState);
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(NUM_SLOTS) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                if (!level.isClientSide) {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                }
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return switch (slot) {
                    case SLOT_INPUT -> canSmelt(stack);
                    case SLOT_FUEL -> isFuel(stack);
                    default -> true;
                };
            }
        };
    }

    @Override
    public <T> @Nonnull LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.subspaceparasite.infested_furnace");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new com.subspaceparasite.common.menu.MenuInfestedFurnace(id, playerInventory, this, dataTracker);
    }

    private boolean canSmelt(ItemStack stack) {
        if (stack.isEmpty() || level == null) return false;
        SimpleContainer container = new SimpleContainer(stack);
        Optional<SmeltingRecipe> recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, container, level);
        return recipe.isPresent();
    }

    private boolean isFuel(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return net.minecraftforge.common.ForgeHooks.getBurnTime(stack) > 0;
    }

    private void doSmelt() {
        if (level == null) return;
        ItemStack input = itemHandler.getStackInSlot(SLOT_INPUT);
        SimpleContainer container = new SimpleContainer(input);
        Optional<SmeltingRecipe> recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, container, level);
        
        if (recipe.isPresent()) {
            SmeltingRecipe smeltingRecipe = recipe.get();
            ItemStack result = smeltingRecipe.assemble(container, level.registryAccess());
            ItemStack output = itemHandler.getStackInSlot(SLOT_OUTPUT);

            if (output.isEmpty()) {
                itemHandler.setStackInSlot(SLOT_OUTPUT, result.copy());
            } else if (output.getItem() == result.getItem() && output.getCount() + result.getCount() <= output.getMaxStackSize()) {
                output.grow(result.getCount());
            }

            input.shrink(1);
            itemHandler.setStackInSlot(SLOT_INPUT, input);
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TileEntityInfestedFurnace furnace) {
        if (level.isClientSide) return;

        boolean needsUpdate = false;

        // 燃料处理
        if (furnace.burnTime <= 0) {
            ItemStack fuel = furnace.itemHandler.getStackInSlot(SLOT_FUEL);
            if (!fuel.isEmpty() && furnace.isFuel(fuel)) {
                furnace.currentBurnTime = net.minecraftforge.common.ForgeHooks.getBurnTime(fuel);
                furnace.burnTime = furnace.currentBurnTime;
                fuel.shrink(1);
                if (fuel.isEmpty()) {
                    furnace.itemHandler.setStackInSlot(SLOT_FUEL, fuel.getCraftingRemainingItem());
                }
                needsUpdate = true;
            }
        }

        // 烧炼处理
        if (furnace.burnTime > 0 && furnace.canSmelt(furnace.itemHandler.getStackInSlot(SLOT_INPUT))) {
            furnace.cookTime++;
            if (furnace.cookTime >= furnace.totalCookTime) {
                furnace.doSmelt();
                furnace.cookTime = 0;
                needsUpdate = true;
            }
        } else {
            furnace.cookTime = 0;
        }

        // 燃料消耗
        if (furnace.burnTime > 0) {
            furnace.burnTime--;
        }

        if (needsUpdate) {
            furnace.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Items", itemHandler.serializeNBT());
        tag.putInt("BurnTime", burnTime);
        tag.putInt("CurrentBurnTime", currentBurnTime);
        tag.putInt("CookTime", cookTime);
        tag.putInt("TotalCookTime", totalCookTime);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("Items"));
        burnTime = tag.getInt("BurnTime");
        currentBurnTime = tag.getInt("CurrentBurnTime");
        cookTime = tag.getInt("CookTime");
        totalCookTime = tag.getInt("TotalCookTime");
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        lazyItemHandler.invalidate();
    }
}
