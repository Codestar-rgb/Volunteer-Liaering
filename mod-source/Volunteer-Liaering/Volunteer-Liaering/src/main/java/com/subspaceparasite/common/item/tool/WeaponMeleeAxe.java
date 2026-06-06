package com.subspaceparasite.common.item.tool;

import com.subspaceparasite.core.ModEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Axe weapon with corrosion effect on hit.
 * Ported from original SRP WeaponMeleeAxe for Forge 1.20.1.
 */
public class WeaponMeleeAxe extends WeaponToolMeleeBase {

    public WeaponMeleeAxe(Tier material, String name, double attackSpeed, float reach,
                          float attackDamage, boolean hasFearEffect, byte toolId, Properties properties) {
        super(material, name, attackSpeed, reach, attackDamage, hasFearEffect, toolId, properties);
    }

    @Override
    public Item getNextTier() {
        // TODO: Return sentient version when registered in ModItems
        return null;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean flag = super.hurtEnemy(stack, target, attacker);

        if (flag) {
            double chance = this.hasFearEffect ? 0.5 : 0.25;
            int amplifier = this.hasFearEffect ? 1 : 0;

            if (attacker.level().random.nextDouble() < chance) {
                // Apply Corrosion effect for 5 seconds (100 ticks)
                target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    ModEffects.CORROSION.get(), 100, amplifier, false, false));
            }
        }

        return flag;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (this.hasFearEffect) {
            tooltip.add(Component.literal("Fear Effect").withStyle(ChatFormatting.DARK_RED));
        }
    }
}
