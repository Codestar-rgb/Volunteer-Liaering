package com.subspaceparasite.common.item;

import com.subspaceparasite.api.capability.IParasiteCapability;
import com.subspaceparasite.api.entity.IEvolvable;
import com.subspaceparasite.api.enums.EvoPhase;
import net.minecraft.ChatFormatting;
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
 * Debug item to force evolution phase change on parasite entities.
 * Usage: Right-click on a parasite entity to advance its evolution phase.
 */
public class ItemEvolve extends Item {
    
    public ItemEvolve() {
        super(new Properties().stacksTo(1));
    }
    
    @Override
    public InteractionResultHolder<ItemStack> interact(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (!level.isClientSide()) {
            // Raytrace to find entity
            Entity lookedEntity = player.pick(5.0, 0.0f, false).getEntity();
            
            if (lookedEntity instanceof LivingEntity livingEntity) {
                IParasiteCapability cap = IParasiteCapability.get(livingEntity);
                IEvolvable evolvable = IEvolvable.get(livingEntity);
                
                if (cap != null && evolvable != null) {
                    EvoPhase current = evolvable.getEvolutionPhase();
                    EvoPhase next = current.getNextPhase();
                    
                    if (next != null && next != current) {
                        evolvable.setEvolutionPhase(next);
                        player.sendSystemMessage(Component.literal("Evolved from " + current.getName() + " to " + next.getName())
                                .withStyle(ChatFormatting.GREEN));
                        return InteractionResultHolder.success(stack);
                    } else {
                        player.sendSystemMessage(Component.literal("Already at maximum evolution!")
                                .withStyle(ChatFormatting.RED));
                        return InteractionResultHolder.fail(stack);
                    }
                } else {
                    player.sendSystemMessage(Component.literal("Target is not a parasite entity!")
                            .withStyle(ChatFormatting.RED));
                    return InteractionResultHolder.fail(stack);
                }
            }
        }
        
        return InteractionResultHolder.pass(stack);
    }
    
    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§aDebug Tool: Force Evolution").withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.literal("Right-click on a parasite to advance evolution phase").withStyle(ChatFormatting.GRAY));
    }
}
