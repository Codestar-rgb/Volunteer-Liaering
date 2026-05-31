package com.subspaceparasite.common.item.tool;

import com.subspaceparasite.core.ModItems;
import com.subspaceparasite.effect.ModEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Sword weapon with bleed effect on hit.
 * Ported from original SRP WeaponMeleeSword.
 */
public class WeaponMeleeSword extends WeaponToolMeleeBase {
    
    public WeaponMeleeSword(Tier material, String name, double attackSpeed, float reach, 
                            float attackDamage, boolean hasFearEffect, byte toolId, Properties properties) {
        super(material, name, attackSpeed, reach, attackDamage, hasFearEffect, toolId, properties);
    }
    
    @Override
    public Item getNextTier() {
        // Return sentient version if this is the normal sword
        if (this == ModItems.WEAPON_SWORD.get()) {
            return ModItems.WEAPON_SWORD_SENTIENT.get();
        }
        return null;
    }
    
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean flag = super.hurtEnemy(stack, target, attacker);
        
        if (flag) {
            double chance = this.hasFearEffect ? 0.5 : 0.25;
            int amplifier = this.hasFearEffect ? 1 : 0;
            
            if (attacker.level().random.nextDouble() < chance) {
                // Apply Bleed effect for 5 seconds (100 ticks)
                target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    ModEffects.BLEED.get(), 100, amplifier, false, false));
            }
        }
        
        return flag;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        // Add weapon type description
        tooltip.add(Component.translatable("tooltip.subspaceparasite.weaponm." + this.toolId)
            .withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.translatable("tooltip.subspaceparasite.weaponm." + (this.toolId * 10))
            .withStyle(ChatFormatting.RED));
        
        if (this.hasFearEffect) {
            tooltip.add(Component.translatable("tooltip.subspaceparasite.weaponm." + (this.toolId * 100))
                .withStyle(ChatFormatting.BLACK));
        }
    }
    
    @Override
    public Rarity getRarity(ItemStack stack) {
        return this.hasFearEffect ? Rarity.RARE : Rarity.COMMON;
    }
}
