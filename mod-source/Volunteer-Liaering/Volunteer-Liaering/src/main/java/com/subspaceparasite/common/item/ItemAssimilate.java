package com.subspaceparasite.common.item;

import com.subspaceparasite.common.capability.ParasiteCapability;
import com.subspaceparasite.common.capability.ParasiteCapabilityProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
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
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (!level.isClientSide()) {
            // Proper entity raytrace: find entity player is looking at
            Entity lookedEntity = pickEntity(player, 5.0);
            
            if (lookedEntity instanceof LivingEntity livingEntity) {
                var cap = livingEntity.getCapability(ParasiteCapability.CAPABILITY);
            ParasiteCapability capInstance = cap.orElse(null);
                
                if (capInstance != null) {
                    int oldInfection = capInstance.getInfectionLevel();
                    capInstance.setInfectionLevel(100);
                    
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
        tooltip.add(Component.literal("Debug Tool: Instant Infection").withStyle(ChatFormatting.DARK_RED));
        tooltip.add(Component.literal("Right-click on an entity to set infection to 100").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("WARNING: This will trigger full infection transformation!").withStyle(ChatFormatting.RED));
    }

    /**
     * Proper entity raytrace helper. player.pick() only does block raytracing
     * and never returns EntityHitResult. This method performs an AABB-based
     * entity raytrace to find the entity the player is looking at.
     */
    private static Entity pickEntity(Player player, double maxDistance) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 endPos = eyePos.add(lookVec.scale(maxDistance));
        AABB searchBox = player.getBoundingBox().expandTowards(lookVec.scale(maxDistance)).inflate(1.0);
        List<Entity> entities = player.level().getEntities(player, searchBox, e -> true);
        Entity closest = null;
        double closestDist = maxDistance;
        for (Entity entity : entities) {
            AABB entityBB = entity.getBoundingBox().inflate(0.3);
            var clipResult = entityBB.clip(eyePos, endPos);
            if (clipResult.isPresent()) {
                double dist = eyePos.distanceToSqr(clipResult.get());
                if (dist < closestDist * closestDist) {
                    closestDist = Math.sqrt(dist);
                    closest = entity;
                }
            }
        }
        return closest;
    }
}
