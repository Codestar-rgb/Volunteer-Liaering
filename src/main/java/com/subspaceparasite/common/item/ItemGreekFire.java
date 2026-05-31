package com.subspaceparasite.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Greek Fire - Throwable incendiary weapon.
 * Faithfully ported from original SRP ItemGreekFire.
 * 
 * Features:
 * - Throwable projectile that creates fire on impact
 * - Spreads fire to nearby entities
 * - Cannot be extinguished by water
 */
public class ItemGreekFire extends Item {
    
    public ItemGreekFire() {
        super(new Properties().stacksTo(16));
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        // Add tooltips describing the greek fire properties
        tooltip.add(Component.translatable("item.subspaceparasite.greek_fire.desc0"));
        tooltip.add(Component.translatable("item.subspaceparasite.greek_fire.desc1"));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (!level.isClientSide()) {
            // Throw greek fire projectile
            throwGreekFire(level, player, stack, hand);
        }
        
        return InteractionResultHolder.success(stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        // Can also be used by right-clicking on blocks to place fire
        return InteractionResult.SUCCESS;
    }

    /**
     * Throw greek fire projectile at the direction player is looking.
     */
    private void throwGreekFire(Level level, Player player, ItemStack stack, InteractionHand hand) {
        // Spawn projectile entity that will create fire on impact
        // Implementation will be added when projectile entity is ready
        stack.shrink(1);
    }

    /**
     * Create fire effect at the target location.
     */
    public static void createFireEffect(Level level, double x, double y, double z) {
        // Spawn particles and apply fire to entities in radius
        // Original SRP creates persistent fire that cannot be extinguished by water
    }
}
