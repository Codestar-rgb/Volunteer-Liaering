package com.subspaceparasite.common.block.entity;

import com.subspaceparasite.core.ModBlocks;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityInfuserFurnace extends BlockEntity implements MenuProvider {
    
    // 槽位定义
    public static final int SLOT_SMELT_IN = 0;
    public static final int SLOT_FUEL = 1;
    public static final int SLOT_SMELT_OUT = 2;
    public static final int SLOT_INFUSE_IN = 3;
    public static final int SLOT_INFUSE_OUT = 4;
    public static final int SLOT_BOTTLE_OUT = 5;
    public static final int NUM_SLOTS = 6;
    
    // 物品处理
    private final ItemStackHandler itemHandler = createHandler();
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    
    // 烧炼系统数据
    private int burnTime = 0;
    private int currentBurnTime = 0;
    private int cookTimeSmelt = 0;
    private int totalCookTimeSmelt = 200;
    
    // 灌注系统数据
    private int cookTimeInfuse = 0;
    private int totalCookTimeInfuse = 200;
    
    // 数据追踪器（用于GUI同步）
    private final ContainerData dataTracker = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> TileEntityInfuserFurnace.this.cookTimeSmelt;
                case 1 -> TileEntityInfuserFurnace.this.totalCookTimeSmelt;
                case 2 -> TileEntityInfuserFurnace.this.burnTime;
                case 3 -> TileEntityInfuserFurnace.this.cookTimeInfuse;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> TileEntityInfuserFurnace.this.cookTimeSmelt = value;
                case 1 -> TileEntityInfuserFurnace.this.totalCookTimeSmelt = value;
                case 2 -> TileEntityInfuserFurnace.this.burnTime = value;
                case 3 -> TileEntityInfuserFurnace.this.cookTimeInfuse = value;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };
    
    public TileEntityInfuserFurnace(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.INFUSER_FURNACE.get(), pos, blockState);
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
                    case SLOT_SMELT_IN -> canSmelt(stack);
                    case SLOT_FUEL -> isFuel(stack);
                    case SLOT_INFUSE_IN -> canInfuse(stack);
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
        return Component.translatable("container.subspaceparasite.infuser_furnace");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new MenuInfuserFurnace(id, playerInventory, this, dataTracker);
    }
    
    // 烧炼相关方法
    private boolean canSmelt(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Item item = stack.getItem();
        // 检查是否有熔炉配方
        return level != null && level.getRecipeManager().getRecipeFor(
            net.minecraft.world.item.crafting.RecipeType.SMELTING, 
            new SimpleContainer(stack), 
            level
        ).isPresent();
    }
    
    private boolean isFuel(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return net.minecraftforge.common.ForgeHooks.getBurnTime(stack) > 0;
    }
    
    private void doSmelt() {
        if (!canSmelt(itemHandler.getStackInSlot(SLOT_SMELT_IN))) return;
        
        ItemStack input = itemHandler.getStackInSlot(SLOT_SMELT_IN);
        var recipeOpt = level.getRecipeManager().getRecipeFor(
            net.minecraft.world.item.crafting.RecipeType.SMELTING,
            new SimpleContainer(input),
            level
        );
        
        if (recipeOpt.isPresent()) {
            var recipe = recipeOpt.get();
            ItemStack result = recipe.assemble(new SimpleContainer(input), level.registryAccess());
            
            if (itemHandler.getStackInSlot(SLOT_SMELT_OUT).isEmpty()) {
                itemHandler.setStackInSlot(SLOT_SMELT_OUT, result.copy());
            } else if (itemHandler.getStackInSlot(SLOT_SMELT_OUT).getItem() == result.getItem()) {
                itemHandler.getStackInSlot(SLOT_SMELT_OUT).grow(result.getCount());
            }
            
            input.shrink(1);
            itemHandler.setStackInSlot(SLOT_SMELT_IN, input);
        }
    }
    
    // 灌注相关方法
    private boolean canInfuse(ItemStack stack) {
        if (stack.isEmpty()) return false;
        // 这里可以添加特定的灌注配方检查
        // 目前允许任何物品作为灌注输入
        return true;
    }
    
    private void doInfuse() {
        if (!canInfuse(itemHandler.getStackInSlot(SLOT_INFUSE_IN))) return;
        
        ItemStack input = itemHandler.getStackInSlot(SLOT_INFUSE_IN);
        ItemStack output = itemHandler.getStackInSlot(SLOT_INFUSE_OUT);
        
        // 简单的灌注逻辑：将输入物品转换为"感染"版本
        // 实际实现需要根据原版SRP的灌注配方系统
        if (output.isEmpty()) {
            // 创建输出物品（这里需要实际的灌注配方）
            ItemStack infused = input.copy();
            // TODO: 应用感染效果或转换
            itemHandler.setStackInSlot(SLOT_INFUSE_OUT, infused);
        } else if (output.getItem() == input.getItem()) {
            output.grow(input.getCount());
            itemHandler.setStackInSlot(SLOT_INFUSE_OUT, output);
        }
        
        input.shrink(1);
        itemHandler.setStackInSlot(SLOT_INFUSE_IN, input);
    }
    
    public static void tick(Level level, BlockPos pos, BlockState state, TileEntityInfuserFurnace furnace) {
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
                } else {
                    furnace.itemHandler.setStackInSlot(SLOT_FUEL, fuel);
                }
                needsUpdate = true;
            }
        }
        
        // 烧炼处理
        if (furnace.burnTime > 0 && furnace.canSmelt(furnace.itemHandler.getStackInSlot(SLOT_SMELT_IN))) {
            furnace.cookTimeSmelt++;
            if (furnace.cookTimeSmelt >= furnace.totalCookTimeSmelt) {
                furnace.doSmelt();
                furnace.cookTimeSmelt = 0;
                needsUpdate = true;
            }
        } else {
            furnace.cookTimeSmelt = 0;
        }
        
        // 灌注处理（需要燃料）
        if (furnace.burnTime > 0 && furnace.canInfuse(furnace.itemHandler.getStackInSlot(SLOT_INFUSE_IN))) {
            furnace.cookTimeInfuse++;
            if (furnace.cookTimeInfuse >= furnace.totalCookTimeInfuse) {
                furnace.doInfuse();
                furnace.cookTimeInfuse = 0;
                needsUpdate = true;
            }
        } else {
            furnace.cookTimeInfuse = 0;
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
        tag.putInt("CookTimeSmelt", cookTimeSmelt);
        tag.putInt("TotalCookTimeSmelt", totalCookTimeSmelt);
        tag.putInt("CookTimeInfuse", cookTimeInfuse);
        tag.putInt("TotalCookTimeInfuse", totalCookTimeInfuse);
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("Items"));
        burnTime = tag.getInt("BurnTime");
        currentBurnTime = tag.getInt("CurrentBurnTime");
        cookTimeSmelt = tag.getInt("CookTimeSmelt");
        totalCookTimeSmelt = tag.getInt("TotalCookTimeSmelt");
        cookTimeInfuse = tag.getInt("CookTimeInfuse");
        totalCookTimeInfuse = tag.getInt("TotalCookTimeInfuse");
    }
    
    @Override
    public void setRemoved() {
        super.setRemoved();
        lazyItemHandler.invalidate();
    }
    
    // 侧面自动化支持
    public int[] getSlotsForFace(Direction side) {
        return switch (side) {
            case DOWN -> new int[]{SLOT_SMELT_OUT, SLOT_INFUSE_OUT, SLOT_BOTTLE_OUT};
            case UP -> new int[]{SLOT_SMELT_IN, SLOT_FUEL, SLOT_INFUSE_IN};
            default -> new int[]{SLOT_SMELT_IN, SLOT_FUEL, SLOT_SMELT_OUT, SLOT_INFUSE_IN, SLOT_INFUSE_OUT, SLOT_BOTTLE_OUT};
        };
    }
    
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
        return switch (index) {
            case SLOT_SMELT_IN, SLOT_INFUSE_IN, SLOT_FUEL -> true;
            default -> false;
        };
    }
    
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return switch (index) {
            case SLOT_SMELT_OUT, SLOT_INFUSE_OUT, SLOT_BOTTLE_OUT -> true;
            default -> false;
        };
    }
}
