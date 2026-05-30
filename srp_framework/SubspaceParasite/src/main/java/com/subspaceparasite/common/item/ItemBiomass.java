package com.subspaceparasite.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Biomass - Core material for parasite growth and crafting.
 * Can be consumed but provides minimal nutrition with negative effects.
 */
public class ItemBiomass extends Item {
    
    public ItemBiomass() {
        super(new Properties().food(new FoodProperties.Builder()
                .nutrition(1)
                .saturationMod(0.1f)
                .effect(() -> new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.HUNGER, 200, 1), 1.0f)
                .build()));
    }
    
    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§5Organic matter concentrated by parasites").withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.literal("Used in advanced parasite crafting").withStyle(ChatFormatting.GRAY));
    }
}
