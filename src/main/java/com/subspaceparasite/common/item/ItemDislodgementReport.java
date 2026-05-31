package com.subspaceparasite.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 脱离报告 (Dislodgement Report)
 * 原版 SRP 机制：记录并显示寄生虫脱离事件的特殊物品
 * 用于追踪被成功清除的寄生虫信息
 */
public class ItemDislodgementReport extends Item {

    public ItemDislodgementReport() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (!world.isClientSide) {
            // TODO: 获取当前世界的寄生虫脱离事件记录并显示
            // 需要依赖 SRP 的 DislodgementTracker 或类似系统
            // 暂时显示基础信息，后续接入完整追踪系统
            player.sendSystemMessage(Component.literal("§4§l[SRP Dislodgement Report]"));
            player.sendSystemMessage(Component.literal("Recent Dislodgements: §eLoading..."));
            player.sendSystemMessage(Component.literal("§7Tracks successfully removed parasites."));
        } else {
            // 客户端提示
            player.sendSystemMessage(Component.literal("§7Opening Dislodgement Report GUI... (WIP)"));
        }
        
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal("§4§lDislodgement Report"));
        tooltip.add(Component.literal("§7Right-click to view parasite removal history."));
        tooltip.add(Component.literal("§8Tracks dislodged parasite entities."));
    }
}
