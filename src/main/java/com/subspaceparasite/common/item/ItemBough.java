package com.subspaceparasite.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.List;

/**
 * ItemBough - 枝杈
 * 特殊工具，用于快速传播荆棘影植物
 * 原版SRP中：具有特殊攻击机制，可以造成即死伤害并传播荆棘影
 * 
 * 简化版1.20.1移植：保留放置荆棘影的核心功能
 */
public class ItemBough extends Item {
    
    public static final int USE_TICKS = 20;
    public static final int COOLDOWN_TICKS = 60;
    
    public ItemBough() {
        super(new Properties().stacksTo(1).durability(384));
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction facing = context.getClickedFace();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        
        if (facing != Direction.UP) {
            return InteractionResult.FAIL;
        }
        
        BlockState clickedState = level.getBlockState(pos);
        BlockPos placePos = clickedState.isCollisionShapeFullBlock(level, pos) ? pos.above() : pos;
        
        if (player != null && !player.mayBuild()) {
            return InteractionResult.FAIL;
        }
        
        // 检查是否可以放置荆棘影
        // TODO: 添加荆棘影方块的canSurvive检查
        
        if (!level.isClientSide) {
            // TODO: 实现荆棘影方块放置逻辑
            // 播放音效
            level.playSound(null, placePos, SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
            
            if (player != null && !player.getAbilities().instabuild) {
                // TODO: 添加耐久度消耗
            }
        }
        
        return InteractionResult.SUCCESS;
    }
    
    @Override
    public int getUseDuration(ItemStack stack) {
        return USE_TICKS;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.subspaceparasite.bough.desc"));
    }
}
