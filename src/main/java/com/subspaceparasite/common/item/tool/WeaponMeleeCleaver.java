package com.subspaceparasite.common.item.tool;

import com.subspaceparasite.core.ModToolTiers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;

/**
 * Cleaver weapon with heavy damage.
 * Faithfully ported from original SRP WeaponMeleeCleaver.
 */
public class WeaponMeleeCleaver extends WeaponToolMeleeBase {
    
    public WeaponMeleeCleaver(String name, double attackSpeed, float reach, float attackDamage, boolean fear, byte id) {
        super(ModToolTiers.HIJACKED_IRON, name, attackSpeed, reach, attackDamage, fear, id);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean flag = super.hurtEnemy(stack, target, attacker);
        if (flag && attacker instanceof Player player) {
            // Cleaver special mechanics will be implemented here
        }
        return flag;
    }
}
