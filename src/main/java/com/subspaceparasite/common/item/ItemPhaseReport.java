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
 * 相位报告 (Phase Report)
 * 原版 SRP 机制：右键点击显示当前寄生虫入侵阶段信息的特殊物品
 * 用于监控世界寄生虫感染进度
 */
public class ItemPhaseReport extends Item {

    public ItemPhaseReport() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (!world.isClientSide) {
            // TODO: 获取当前世界的寄生虫阶段信息并显示
            // 需要依赖 SRP 的 PhaseManager 或类似系统
            // 暂时显示基础信息，后续接入完整阶段系统
            player.sendSystemMessage(Component.literal("§4§l[SRP Phase Report]"));
            player.sendSystemMessage(Component.literal("Current Phase: §eCalculating..."));
            player.sendSystemMessage(Component.literal("§7Check server logs for detailed phase data."));
        } else {
            // 客户端提示
            player.sendSystemMessage(Component.literal("§7Opening Phase Report GUI... (WIP)"));
        }
        
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal("§4§lPhase Report"));
        tooltip.add(Component.literal("§7Right-click to view current infection phase."));
        tooltip.add(Component.literal("§8Monitors parasite evolution stages."));
    }
}
