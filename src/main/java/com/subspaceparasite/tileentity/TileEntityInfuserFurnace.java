package com.subspaceparasite.tileentity;

import com.subspaceparasite.core.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * TileEntity for the Infuser Furnace - supports both smelting and infusing recipes.
 * Ported from original SRP TileEntityInfuserFurnace (397 lines) to Minecraft 1.20.1.
 * 
 * Features:
 * - 6 inventory slots: SMELT_IN(0), FUEL(1), SMELT_OUT(2), INFUSE_IN(3), INFUSE_OUT(4), BOTTLE_OUT(5)
 * - Dual cooking system: smelting + infusing
 * - Fuel burning with burn time tracking
 * - NBT persistence for all state
 * - Sided inventory support for automation
 */
public class TileEntityInfuserFurnace extends BaseContainerBlockEntity implements MenuProvider {
    
    public static final int SLOT_SMELT_IN = 0;
    public static final int SLOT_FUEL = 1;
    public static final int SLOT_SMELT_OUT = 2;
    public static final int SLOT_INFUSE_IN = 3;
    public static final int SLOT_INFUSE_OUT = 4;
    public static final int SLOT_BOTTLE_OUT = 5;
    
    private static final int[] SLOTS_TOP = new int[]{SLOT_SMELT_IN};
    private static final int[] SLOTS_BOTTOM = new int[]{SLOT_SMELT_OUT, SLOT_INFUSE_OUT, SLOT_BOTTLE_OUT};
    private static final int[] SLOTS_SIDES = new int[]{SLOT_FUEL, SLOT_INFUSE_IN};
    
    private static final int COOK_TIME_TOTAL = 200;
    
    // Inventory handler for Forge capabilities
    private final ItemStackHandler itemHandler = createItemHandler();
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    
    // State fields
    private int burnTime;
    private int currentBurnTime;
    private int cookTimeSmelt;
    private int cookTimeInfuse;
    
    // Data tracker for GUI synchronization
    private final ContainerData dataTracker = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> burnTime;
                case 1 -> currentBurnTime;
                case 2 -> cookTimeSmelt;
                case 3 -> cookTimeInfuse;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> burnTime = value;
                case 1 -> currentBurnTime = value;
                case 2 -> cookTimeSmelt = value;
                case 3 -> cookTimeInfuse = value;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };
    
    public TileEntityInfuserFurnace(BlockPos pos, BlockState state) {
        super(ModEntities.INFUSER_FURNACE_BE.get(), pos, state);
    }
    
    private ItemStackHandler createItemHandler() {
        return new ItemStackHandler(6) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
    }
    
    @Override
    public Component getDisplayName() {
        return Component.translatable("container.subspaceparasite.infuser_furnace");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new com.subspaceparasite.common.menu.MenuInfuserFurnace(containerId, playerInventory, this, dataTracker);
    }
    
    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.subspaceparasite.infuser_furnace");
    }
    
    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory playerInventory) {
        return new com.subspaceparasite.common.menu.MenuInfuserFurnace(containerId, playerInventory, this, dataTracker);
    }
    
    @Override
    public int getContainerSize() {
        return 6;
    }
    
    @Override
    public boolean isEmpty() {
        for (int i = 0; i < 6; i++) {
            if (!getItem(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public ItemStack getItem(int index) {
        return itemHandler.getStackInSlot(index);
    }
    
    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack result = itemHandler.extractItem(index, count, false);
        if (!result.isEmpty()) {
            setChanged();
        }
        return result;
    }
    
    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack result = itemHandler.getStackInSlot(index);
        itemHandler.setStackInSlot(index, ItemStack.EMPTY);
        return result;
    }
    
    @Override
    public void setItem(int index, ItemStack stack) {
        itemHandler.setStackInSlot(index, stack);
        setChanged();
    }
    
    @Override
    public boolean stillValid(Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) {
            return false;
        }
        return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0;
    }
    
    /**
     * Server-side tick logic - handles smelting and infusing
     */
    public static void tick(Level level, BlockPos pos, BlockState state, TileEntityInfuserFurnace furnace) {
        boolean wasBurning = furnace.isBurning();
        boolean dirty = false;
        
        // Decrease burn time if burning
        if (furnace.isBurning()) {
            furnace.burnTime--;
        }
        
        // Get current infuse recipe
        Optional<CampfireCookingRecipe> infuseRecipe = furnace.getCurrentInfuseRecipe();
        
        // Try to start burning if not burning and can cook
        if (!furnace.isBurning() && (furnace.canSmelt() || furnace.canInfuse(infuseRecipe))) {
            ItemStack fuelStack = furnace.getItem(SLOT_FUEL);
            int fuelBurn = getBurnTime(fuelStack);
            
            if (fuelBurn > 0) {
                furnace.burnTime = fuelBurn;
                furnace.currentBurnTime = fuelBurn;
                dirty = true;
                
                // Consume fuel
                fuelStack.shrink(1);
                if (fuelStack.isEmpty()) {
                    Item container = fuelStack.getItem().getCraftingRemainingItem();
                    if (container != null) {
                        furnace.setItem(SLOT_FUEL, new ItemStack(container));
                    }
                }
            }
        }
        
        // Smelting progress
        if (furnace.isBurning() && furnace.canSmelt()) {
            furnace.cookTimeSmelt++;
            if (furnace.cookTimeSmelt >= COOK_TIME_TOTAL) {
                furnace.cookTimeSmelt = 0;
                furnace.doSmelt();
                dirty = true;
            }
        } else {
            furnace.cookTimeSmelt = 0;
        }
        
        // Infusing progress
        if (furnace.isBurning() && furnace.canInfuse(infuseRecipe)) {
            furnace.cookTimeInfuse++;
            int cookTime = infuseRecipe.map(r -> r.getCookingTime()).orElse(COOK_TIME_TOTAL);
            if (furnace.cookTimeInfuse >= cookTime) {
                furnace.cookTimeInfuse = 0;
                furnace.doInfuse(infuseRecipe.orElse(null));
                dirty = true;
            }
        } else {
            furnace.cookTimeInfuse = 0;
        }
        
        // Check if burning state changed
        if (wasBurning != furnace.isBurning()) {
            dirty = true;
        }
        
        if (dirty) {
            furnace.setChanged();
        }
    }
    
    public boolean isBurning() {
        return burnTime > 0;
    }
    
    private boolean canSmelt() {
        ItemStack input = getItem(SLOT_SMELT_IN);
        if (input.isEmpty()) {
            return false;
        }
        
        // Check vanilla furnace recipes
        RecipeManager recipeManager = level.getRecipeManager();
        Optional<net.minecraft.world.item.crafting.SmokingRecipe> recipe = recipeManager.getRecipeFor(RecipeType.SMOKING, input, level);
        if (recipe.isEmpty()) {
            recipe = recipeManager.getRecipeFor(RecipeType.BLASTING, input, level);
        }
        if (recipe.isEmpty()) {
            recipe = recipeManager.getRecipeFor(RecipeType.SMELTING, input, level);
        }
        
        if (recipe.isEmpty()) {
            return false;
        }
        
        ItemStack result = recipe.get().getResultItem(level.registryAccess());
        return canOutputStack(getItem(SLOT_SMELT_OUT), result);
    }
    
    private void doSmelt() {
        if (!canSmelt()) return;
        
        ItemStack input = getItem(SLOT_SMELT_IN);
        RecipeManager recipeManager = level.getRecipeManager();
        Optional<net.minecraft.world.item.crafting.SmokingRecipe> recipe = recipeManager.getRecipeFor(RecipeType.SMOKING, input, level);
        if (recipe.isEmpty()) {
            recipe = recipeManager.getRecipeFor(RecipeType.BLASTING, input, level);
        }
        if (recipe.isEmpty()) {
            recipe = recipeManager.getRecipeFor(RecipeType.SMELTING, input, level);
        }
        
        if (recipe.isPresent()) {
            ItemStack result = recipe.get().getResultItem(level.registryAccess()).copy();
            ItemStack output = getItem(SLOT_SMELT_OUT);
            
            if (output.isEmpty()) {
                setItem(SLOT_SMELT_OUT, result);
            } else if (ItemStack.isSameItemSameTags(output, result)) {
                output.grow(result.getCount());
                setItem(SLOT_SMELT_OUT, output);
            }
            
            input.shrink(1);
            setItem(SLOT_SMELT_IN, input);
        }
    }
    
    private Optional<CampfireCookingRecipe> getCurrentInfuseRecipe() {
        ItemStack smeltInput = getItem(SLOT_SMELT_IN);
        ItemStack infuseInput = getItem(SLOT_INFUSE_IN);
        
        if (smeltInput.isEmpty() || infuseInput.isEmpty()) {
            return Optional.empty();
        }
        
        // For now, return empty - actual infuse recipes need custom recipe type
        // This will be implemented with custom InfuserFurnaceRecipe system
        return Optional.empty();
    }
    
    private boolean canInfuse(Optional<CampfireCookingRecipe> recipe) {
        if (recipe.isEmpty()) {
            return false;
        }
        
        CampfireCookingRecipe r = recipe.get();
        ItemStack infusedOut = r.getResultItem(level.registryAccess());
        ItemStack bottleOut = ItemStack.EMPTY; // Placeholder for bottle output
        
        return canMerge(getItem(SLOT_INFUSE_OUT), infusedOut) && 
               canMerge(getItem(SLOT_BOTTLE_OUT), bottleOut);
    }
    
    private void doInfuse(@Nullable CampfireCookingRecipe recipe) {
        if (recipe == null) return;
        
        getItem(SLOT_SMELT_IN).shrink(1);
        getItem(SLOT_INFUSE_IN).shrink(1);
        
        ItemStack infusedOut = recipe.getResultItem(level.registryAccess()).copy();
        ItemStack bottleOut = ItemStack.EMPTY; // Placeholder
        
        mergeInto(SLOT_INFUSE_OUT, infusedOut);
        mergeInto(SLOT_BOTTLE_OUT, bottleOut);
        
        setChanged();
    }
    
    private boolean canOutputStack(ItemStack existing, ItemStack toAdd) {
        if (toAdd.isEmpty()) {
            return false;
        }
        if (existing.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameTags(existing, toAdd)) {
            return false;
        }
        int total = existing.getCount() + toAdd.getCount();
        return total <= existing.getMaxStackSize() && total <= getContainerSize();
    }
    
    private static boolean canMerge(ItemStack slotStack, ItemStack add) {
        if (add.isEmpty()) {
            return true;
        }
        if (slotStack.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameTags(slotStack, add)) {
            return false;
        }
        return slotStack.getCount() + add.getCount() <= slotStack.getMaxStackSize();
    }
    
    private void mergeInto(int slot, ItemStack add) {
        if (add.isEmpty()) return;
        
        ItemStack cur = getItem(slot);
        if (cur.isEmpty()) {
            setItem(slot, add.copy());
        } else {
            cur.grow(add.getCount());
            setItem(slot, cur);
        }
    }
    
    private static int getBurnTime(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        return net.minecraftforge.common.ForgeHooks.getBurnTime(stack, null) * 200 / 16000;
    }
    
    @Override
    public int[] getSlotsForFace(Direction side) {
        return switch (side) {
            case DOWN -> SLOTS_BOTTOM;
            case UP -> SLOTS_TOP;
            default -> SLOTS_SIDES;
        };
    }
    
    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        return index == SLOT_SMELT_IN || index == SLOT_INFUSE_IN || index == SLOT_FUEL;
    }
    
    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return index == SLOT_SMELT_OUT || index == SLOT_INFUSE_OUT || index == SLOT_BOTTLE_OUT;
    }
    
    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        
        // Save items
        compound.put("Items", itemHandler.serializeNBT());
        
        // Save state
        compound.putInt("BurnTime", burnTime);
        compound.putInt("CurrentBurnTime", currentBurnTime);
        compound.putInt("CookSmelt", cookTimeSmelt);
        compound.putInt("CookInfuse", cookTimeInfuse);
    }
    
    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        
        // Load items
        if (compound.contains("Items")) {
            CompoundTag itemsTag = compound.getCompound("Items");
            for (int i = 0; i < 6; i++) {
                String key = "Slot" + i;
                if (itemsTag.contains(key)) {
                    itemHandler.setStackInSlot(i, ItemStack.of(itemsTag.getCompound(key)));
                }
            }
        }
        
        // Load state
        burnTime = compound.getInt("BurnTime");
        currentBurnTime = compound.getInt("CurrentBurnTime");
        cookTimeSmelt = compound.getInt("CookSmelt");
        cookTimeInfuse = compound.getInt("CookInfuse");
    }
    
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }
    
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }
    
    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }
    
    public ContainerData getDataTracker() {
        return dataTracker;
    }
}
