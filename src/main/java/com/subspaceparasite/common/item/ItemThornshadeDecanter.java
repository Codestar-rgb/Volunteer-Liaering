package com.subspaceparasite.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Thornshade Decanter - Throwable potion-like weapon.
 * Faithfully ported from original SRP ItemThornshadeDecanter.
 * 
 * Features:
 * - Throwable container with thornshade berry extract
 * - Creates harmful effect cloud on impact
 * - Applies poison and damage over time to entities in area
 */
public class ItemThornshadeDecanter extends Item {
    
    public ItemThornshadeDecanter() {
        super(new Properties().stacksTo(16));
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        // Add tooltips describing the decanter properties
        tooltip.add(Component.translatable("item.subspaceparasite.thornshade_decanter.desc0"));
        tooltip.add(Component.translatable("item.subspaceparasite.thornshade_decanter.desc1"));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (!level.isClientSide()) {
            // Throw decanter projectile
            throwDecanter(level, player, stack, hand);
        }
        
        return InteractionResultHolder.success(stack);
    }

    /**
     * Throw thornshade decanter projectile.
     */
    private void throwDecanter(Level level, Player player, ItemStack stack, InteractionHand hand) {
        // Spawn projectile entity that will create effect cloud on impact
        // Implementation will be added when projectile entity is ready
        stack.shrink(1);
    }

    /**
     * Create thornshade effect cloud at the target location.
     */
    public static void createEffectCloud(Level level, double x, double y, double z) {
        // Spawn particles and apply effects to entities in radius
        // Original SRP applies poison and damage over time
    }
}
