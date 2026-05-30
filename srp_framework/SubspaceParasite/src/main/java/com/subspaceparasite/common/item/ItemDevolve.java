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
 * Debug item to reduce infection value or cure infection.
 * Usage: Right-click on an entity to reduce its infection level.
 */
public class ItemDevolve extends Item {
    
    public ItemDevolve() {
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
                    int currentInfection = cap.getInfectionLevel();
                    
                    if (currentInfection > 0) {
                        // Reduce infection by 25 points
                        int newInfection = Math.max(0, currentInfection - 25);
                        cap.setInfectionLevel(newInfection);
                        
                        if (newInfection == 0) {
                            player.sendSystemMessage(Component.literal("Infection CURED!")
                                    .withStyle(ChatFormatting.AQUA));
                        } else {
                            player.sendSystemMessage(Component.literal("Infection reduced: " + currentInfection + " -> " + newInfection)
                                    .withStyle(ChatFormatting.YELLOW));
                        }
                        return InteractionResultHolder.success(stack);
                    } else {
                        player.sendSystemMessage(Component.literal("Target has no infection!")
                                .withStyle(ChatFormatting.RED));
                        return InteractionResultHolder.fail(stack);
                    }
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
        tooltip.add(Component.literal("§bDebug Tool: Reduce Infection").withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.literal("Right-click on an entity to reduce infection by 25").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Can completely cure infection with multiple uses").withStyle(ChatFormatting.GRAY));
    }
}
