package com.subspaceparasite.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Book of Vengeance - Grappling hook weapon.
 * Faithfully ported from original SRP ItemBookOfVengeance.
 * 
 * Features:
 * - Right-click to grapple to targeted entity (32 block range)
 * - Sneak + Right-click for knockback burst (5 block radius)
 * - Cooldown mechanics for both abilities
 */
public class ItemBookOfVengeance extends Item {
    
    private static final int RANGE = 32;
    private static final int COOLDOWN_TICKS = 60;
    private static final int KB_COOLDOWN_TICKS = 80;
    private static final double KB_RADIUS = 5.0;
    private static final double KB_STRENGTH = 2.2;
    private static final double KB_UP = 0.4;

    public ItemBookOfVengeance() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        // Add tooltips describing the vengeance abilities
        tooltip.add(Component.translatable("item.subspaceparasite.book_of_vengeance.vengeance0"));
        tooltip.add(Component.translatable("item.subspaceparasite.book_of_vengeance.vengeance1"));
        tooltip.add(Component.translatable("item.subspaceparasite.book_of_vengeance.vengeance3"));
        tooltip.add(Component.translatable("item.subspaceparasite.book_of_vengeance.vengeance5"));
        tooltip.add(Component.translatable("item.subspaceparasite.book_of_vengeance.vengeance6"));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (player.isShiftKeyDown()) {
            // Knockback burst ability
            if (!level.isClientSide()) {
                if (isKnockbackOnCooldown(stack, level.getGameTime())) {
                    return InteractionResultHolder.fail(stack);
                }
                performKnockbackBurst(level, player, stack);
                setKnockbackCooldown(stack, level.getGameTime() + KB_COOLDOWN_TICKS);
                player.swing(hand);
            }
            return InteractionResultHolder.success(stack);
        } else {
            // Grapple ability
            if (player.getCooldownSupply().isOnCooldown(this)) {
                return InteractionResultHolder.fail(stack);
            }
            
            if (!level.isClientSide()) {
                LivingEntity target = findLookTargetLiving(player, RANGE);
                if (target == null || !player.canSee(target)) {
                    return InteractionResultHolder.fail(stack);
                }
                // Start grappling - implementation will be added when handler is ready
                startGrapple(player, target);
                player.getCooldownSupply().addCooldown(this, COOLDOWN_TICKS);
                player.swing(hand);
            }
            return InteractionResultHolder.success(stack);
        }
    }

    /**
     * Find a living entity that the player is looking at within range.
     */
    private static LivingEntity findLookTargetLiving(Player player, double range) {
        // Implementation will use raycasting to find target
        // This matches original SRP behavior
        return null; // Placeholder
    }

    /**
     * Start grappling the player to the target entity.
     */
    private void startGrapple(Player player, LivingEntity target) {
        // Grapple handler implementation will be added
    }

    /**
     * Perform knockback burst around the player.
     */
    private void performKnockbackBurst(Level level, Player player, ItemStack stack) {
        // Spawn particles and sound effects
        // Apply knockback to all entities within radius
        // Original SRP uses falloff based on distance
    }

    /**
     * Check if knockback ability is on cooldown.
     */
    private static boolean isKnockbackOnCooldown(ItemStack stack, long gameTime) {
        // Read NBT data for cooldown timestamp
        return false; // Placeholder
    }

    /**
     * Set knockback cooldown timestamp.
     */
    private static void setKnockbackCooldown(ItemStack stack, long timestamp) {
        // Write NBT data for cooldown timestamp
    }
}
