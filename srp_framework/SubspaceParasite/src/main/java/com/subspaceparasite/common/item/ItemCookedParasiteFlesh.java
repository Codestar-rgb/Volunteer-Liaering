package com.subspaceparasite.common.item;

import com.subspaceparasite.api.capability.IParasiteCapability;
import com.subspaceparasite.core.ModEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Cooked Parasite Flesh - safer to consume than raw.
 * Provides better nutrition and less infection risk.
 */
public class ItemCookedParasiteFlesh extends Item {
    
    public ItemCookedParasiteFlesh() {
        super(new Properties().food(new net.minecraft.world.food.FoodProperties.Builder()
                .nutrition(6)
                .saturationMod(0.8f)
                .meat()
                .effect(() -> new MobEffectInstance(ModEffects.PARASITE_VITALITY.get(), 200, 0), 0.7f)
                .build()));
    }
    
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        ItemStack result = super.finishUsingItem(stack, level, livingEntity);
        
        if (!level.isClientSide() && livingEntity instanceof Player player) {
            IParasiteCapability capability = IParasiteCapability.get(player);
            if (capability != null) {
                // Much smaller infection increase for cooked flesh
                capability.addInfection(3);
                
                // Small chance to not apply any negative effects due to cooking
                if (level.random.nextFloat() < 0.3f) {
                    player.sendSystemMessage(Component.literal("The cooked flesh feels... manageable.").withStyle(ChatFormatting.GRAY));
                }
            }
        }
        
        return result;
    }
}
