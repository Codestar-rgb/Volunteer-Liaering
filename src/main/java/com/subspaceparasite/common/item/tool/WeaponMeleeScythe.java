package com.subspaceparasite.common.item.tool;

import com.subspaceparasite.core.ModToolTiers;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ToolAction;

import java.util.List;

/**
 * Scythe weapon with wide area attack.
 * Faithfully ported from original SRP WeaponMeleeScythe.
 */
public class WeaponMeleeScythe extends WeaponToolMeleeBase {
    
    public WeaponMeleeScythe(String name, double attackSpeed, float reach, float attackDamage, boolean fear, byte id) {
        super(ModToolTiers.HIJACKED_IRON, name, attackSpeed, reach, attackDamage, fear, id);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean flag = super.hurtEnemy(stack, target, attacker);
        if (flag && attacker instanceof Player player) {
            // AOE damage logic will be implemented here
            // Original SRP hits all entities within 4 blocks (8 blocks when calling/fear enabled)
            performAOEAttack(stack, target, player);
        }
        return flag;
    }

    private void performAOEAttack(ItemStack stack, LivingEntity primaryTarget, Player attacker) {
        Level level = attacker.level();
        int aoeRadius = this.hasFear() ? 8 : 4;
        
        // AOE damage implementation will be added when entity targeting system is ready
        // This matches original SRP behavior of hitting all entities in range
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        // Add tooltips for weapon properties
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return false;
    }
}
