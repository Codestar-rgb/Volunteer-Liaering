package com.subspaceparasite.common.item.tool;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

/**
 * Base class for armor items with special properties.
 * Faithfully ported from original SRP WeaponToolArmorBase.
 */
public class WeaponToolArmorBase extends ArmorItem {
    private final boolean calling; // fear flag
    protected final byte idTool;

    public WeaponToolArmorBase(ArmorMaterial material, String name, int durability, boolean fear, byte id, Type slot) {
        super(material, slot, new Properties());
        this.calling = fear;
        this.idTool = id;
    }

    public boolean hasFear() {
        return this.calling;
    }

    public byte getIdTool() {
        return this.idTool;
    }

    @Override
    public void inventoryTick(ItemStack stack, net.minecraft.world.level.Level level, net.minecraft.world.entity.Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        // Special armor tick logic will be implemented here
    }
}
