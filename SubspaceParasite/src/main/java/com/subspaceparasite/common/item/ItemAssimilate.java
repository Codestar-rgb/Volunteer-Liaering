package com.subspaceparasite.common.item;

import com.subspaceparasite.api.capability.IParasiteCapability;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Debug item to instantly infect any living entity.
 * Usage: Right-click on an entity to set infection to 100 (maximum).
 */
public class ItemAssimilate extends Item {
    
    public ItemAssimilate() {
        super(new Properties().stacksTo(1));
    }
    
    @Override
    public InteractionResultHolder<ItemStack> interact(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (!level.isClientSide()) {
            // Raytrace to find entity
            var hitResult = player.pick(5.0, 0.0f, false);
            var lookedEntity = hitResult.getEntity();
            
            if (lookedEntity instanceof LivingEntity livingEntity) {
                IParasiteCapability cap = IParasiteCapability.get(livingEntity);
                
                if (cap != null) {
                    int oldInfection = cap.getInfectionLevel();
                    cap.setInfectionLevel(100);
                    
                    player.sendSystemMessage(Component.literal("INFECTION MAXIMIZED: " + oldInfection + " -> 100!")
                            .withStyle(ChatFormatting.DARK_RED));
                    
                    if (livingEntity instanceof Player targetPlayer) {
                        targetPlayer.sendSystemMessage(Component.literal("You have been fully infected!")
                                .withStyle(ChatFormatting.DARK_RED));
                    }
                    
                    return InteractionResultHolder.success(stack);
                } else {
                    player.sendSystemMessage(Component.literal("Target has no parasite capability!")
                            .withStyle(ChatFormatting.RED));
                    return InteractionResultHolder.fail(stack);
                }
            }
        }
        
        return InteractionResultHolder.pass(stack);
    }
    
    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§4Debug Tool: Instant Infection").withStyle(ChatFormatting.DARK_RED));
        tooltip.add(Component.literal("Right-click on an entity to set infection to 100").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("WARNING: This will trigger full infection transformation!").withStyle(ChatFormatting.RED));
    }
}
