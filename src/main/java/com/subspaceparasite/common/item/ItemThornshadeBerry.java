package com.subspaceparasite.common.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * ItemThornshadeBerry - 荆棘影浆果
 * 食用后给予玩家反效果但可以快速种植荆棘影
 * 原版SRP中：食用时造成2点伤害并施加饥饿效果，同时播放特殊音效
 */
public class ItemThornshadeBerry extends Item {
    
    public ItemThornshadeBerry() {
        super(new Properties().stacksTo(64));
    }
    
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
        if (entityLiving instanceof Player player) {
            if (!level.isClientSide) {
                // 造成伤害和负面效果（忠实还原原版）
                player.hurt(player.damageSources().magic(), 4.0f);
                
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            
            // 播放音效
            level.playSound(null, player.getX(), player.getY(), player.getZ(), 
                net.minecraft.sounds.SoundEvents.PLAYER_HURT, 
                net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 1.0f);
        }
        
        return stack;
    }
    
    @Override
    public int getUseDuration(ItemStack stack) {
        return 20; // 1秒食用时间
    }
}
