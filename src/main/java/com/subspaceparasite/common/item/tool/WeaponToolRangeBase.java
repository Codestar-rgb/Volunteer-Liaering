package com.subspaceparasite.common.item.tool;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Base class for ranged weapons (bows).
 * Faithfully ported from original SRP WeaponToolRangeBase.
 */
public class WeaponToolRangeBase extends BowItem {
    private final boolean calling; // fear flag
    protected final byte idTool;
    private final float damageBonus;
    private final float damageCap;

    public WeaponToolRangeBase(String name, int durability, float damageBonus, float damageCap, float baseDamage, boolean fear, byte id) {
        super(new Properties().durability(durability));
        this.calling = fear;
        this.idTool = id;
        this.damageBonus = damageBonus;
        this.damageCap = damageCap;
    }

    public boolean hasFear() {
        return this.calling;
    }

    public byte getIdTool() {
        return this.idTool;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player player) {
            // Custom arrow shooting logic will be implemented here
            // Original SRP has special projectile behavior with damage scaling
        }
    }
}
