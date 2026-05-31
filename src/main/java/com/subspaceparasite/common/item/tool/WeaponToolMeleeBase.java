package com.subspaceparasite.common.item.tool;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ToolAction;

/**
 * Base class for all melee weapons with reach and fear mechanics.
 * Faithfully ported from original SRP WeaponToolMeleeBase.
 */
public abstract class WeaponToolMeleeBase extends SwordItem implements IHaveReach {
    private final float addReach;
    private final boolean calling; // fear flag
    protected final byte idTool;

    public WeaponToolMeleeBase(Tier tier, String name, double attackSpeed, float reach, float attackDamage, boolean fear, byte id) {
        super(tier, (int) attackDamage, (float) attackSpeed, new Properties());
        this.addReach = reach;
        this.calling = fear;
        this.idTool = id;
    }

    @Override
    public float getReach() {
        return this.addReach;
    }

    public boolean hasFear() {
        return this.calling;
    }

    public byte getIdTool() {
        return this.idTool;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean flag = super.hurtEnemy(stack, target, attacker);
        if (flag && target.getHealth() <= 0.0f) {
            // Track kills for sentient weapon evolution
            // Implementation will be added when NBT system is ready
        }
        return flag;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        // Sentient weapon evolution logic will be implemented here
    }

    /**
     * Get the next item in evolution chain (for sentient weapons)
     */
    public ItemStack getEvolutionResult() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return false;
    }
}
