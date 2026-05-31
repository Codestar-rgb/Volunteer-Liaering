package com.subspaceparasite.common.menu;

import com.subspaceparasite.common.block.entity.TileEntityInfuserFurnace;
import com.subspaceparasite.core.ModMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MenuInfuserFurnace extends AbstractContainerMenu {
    public final TileEntityInfuserFurnace furnace;
    private final ContainerData dataTracker;
    private final Level level;

    public MenuInfuserFurnace(int id, Inventory playerInventory) {
        this(id, playerInventory, null, new ContainerData() {
            @Override public int get(int index) { return 0; }
            @Override public void set(int index, int value) {}
            @Override public int getCount() { return 4; }
        });
    }

    public MenuInfuserFurnace(int id, Inventory playerInventory, TileEntityInfuserFurnace furnace, ContainerData dataTracker) {
        super(ModMenuTypes.INFUSER_FURNACE_MENU.get(), id);
        this.furnace = furnace;
        this.dataTracker = dataTracker;
        this.level = furnace != null ? furnace.getLevel() : playerInventory.player.level();

        // 添加熔炉槽位
        if (furnace != null) {
            // 烧炼输入 (0)
            this.addSlot(new Slot(furnace.itemHandler, TileEntityInfuserFurnace.SLOT_SMELT_IN, 56, 17) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return furnace.canSmelt(stack);
                }
            });
            // 燃料 (1)
            this.addSlot(new Slot(furnace.itemHandler, TileEntityInfuserFurnace.SLOT_FUEL, 56, 53) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return furnace.isFuel(stack);
                }
            });
            // 烧炼输出 (2)
            this.addSlot(new Slot(furnace.itemHandler, TileEntityInfuserFurnace.SLOT_SMELT_OUT, 116, 35) {
                @Override
                public boolean mayPickup(Player player) {
                    return !this.getItem().isEmpty();
                }
            });
            // 灌注输入 (3)
            this.addSlot(new Slot(furnace.itemHandler, TileEntityInfuserFurnace.SLOT_INFUSE_IN, 56, 89) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return furnace.canInfuse(stack);
                }
            });
            // 灌注输出 (4)
            this.addSlot(new Slot(furnace.itemHandler, TileEntityInfuserFurnace.SLOT_INFUSE_OUT, 116, 89) {
                @Override
                public boolean mayPickup(Player player) {
                    return !this.getItem().isEmpty();
                }
            });
            // 瓶子输出 (5)
            this.addSlot(new Slot(furnace.itemHandler, TileEntityInfuserFurnace.SLOT_BOTTLE_OUT, 143, 89) {
                @Override
                public boolean mayPickup(Player player) {
                    return !this.getItem().isEmpty();
                }
            });
        }

        // 添加玩家背包槽位
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 142 + row * 18));
            }
        }

        // 添加玩家快捷栏槽位
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 200));
        }

        this.addDataSlots(dataTracker);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index < 6) {
                // 从熔炉槽位移到玩家背包
                if (!this.moveItemStackTo(stackInSlot, 6, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 从玩家背包移到熔炉槽位
                if (furnace.canSmelt(stackInSlot)) {
                    if (!this.moveItemStackTo(stackInSlot, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (furnace.isFuel(stackInSlot)) {
                    if (!this.moveItemStackTo(stackInSlot, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (furnace.canInfuse(stackInSlot)) {
                    if (!this.moveItemStackTo(stackInSlot, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 6 && index < 33) {
                    if (!this.moveItemStackTo(stackInSlot, 33, this.slots.size(), false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 33 && index < 42) {
                    if (!this.moveItemStackTo(stackInSlot, 6, 33, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.furnace == null) return true;
        BlockEntity blockEntity = this.level.getBlockEntity(this.furnace.getBlockPos());
        return blockEntity == this.furnace;
    }

    public int getCookProgress() {
        return this.dataTracker.get(0);
    }

    public int getTotalCookTime() {
        return this.dataTracker.get(1);
    }

    public int getBurnTime() {
        return this.dataTracker.get(2);
    }

    public int getInfuseProgress() {
        return this.dataTracker.get(3);
    }
}
