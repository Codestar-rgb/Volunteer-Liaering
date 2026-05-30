package com.subspaceparasite.common.item;

import com.subspaceparasite.api.capability.IParasiteCapability;
import com.subspaceparasite.core.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Parasite Flesh item with infection mechanics.
 * When consumed by non-parasite entities, applies infection effects and increases infection value.
 */
public class ItemParasiteFlesh extends Item {
    
    public ItemParasiteFlesh() {
        super(new Properties().food(new FoodProperties.Builder()
                .nutrition(2)
                .saturationMod(0.3f)
                .meat()
                .effect(() -> new MobEffectInstance(ModEffects.BLEED.get(), 100, 0), 1.0f)
                .build()));
    }
    
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        ItemStack result = super.finishUsingItem(stack, level, livingEntity);
        
        if (!level.isClientSide() && livingEntity instanceof Player player) {
            IParasiteCapability capability = IParasiteCapability.get(player);
            if (capability != null) {
                // Increase infection value when consuming parasite flesh
                capability.addInfection(15);
                
                // If not already infected, apply Corrosion effect to simulate initial infection
                if (!capability.isInfected()) {
                    livingEntity.addEffect(new MobEffectInstance(ModEffects.CORROSION.get(), 600, 0));
                }
            }
        }
        
        return result;
    }
}
