package com.subspaceparasite.common.item;

import com.subspaceparasite.core.ModSounds;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

/**
 * Book of Vengeance - 复仇之书
 * 原版SRP特殊工具，提供钩爪和击退爆发功能
 * 
 * 功能:
 * - 右键点击进行钩爪传送（32格范围）
 * - 潜行+右键触发5格半径击退爆发（强度2.2，向上0.4）
 * - 击退爆发有80tick冷却
 * - 钩爪有60tick冷却
 */
public class ItemBookOfVengeance extends Item {
    
    private static final int RANGE = 32;
    private static final int COOLDOWN_TICKS = 60;
    private static final int KB_COOLDOWN_TICKS = 80;
    private static final String NBT_KB_NEXT = "srp_kb_next";
    private static final double KB_RADIUS = 5.0;
    private static final double KB_STRENGTH = 2.2;
    private static final double KB_UP = 0.4;
    
    public ItemBookOfVengeance() {
        super(new Properties().stacksTo(1).rarity(net.minecraft.world.item.Rarity.RARE));
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
        String base = "item.subspaceparasite.book_of_vengeance.";
        tooltip.add(Component.translatable(base + "vengeance0"));
        tooltip.add(Component.translatable(base + "vengeance1"));
        tooltip.add(Component.translatable(base + "vengeance3"));
        tooltip.add(Component.translatable(base + "vengeance5"));
        tooltip.add(Component.translatable(base + "vengeance6"));
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (player.isShiftKeyDown()) {
            // 潜行模式：触发击退爆发
            if (!world.isClientSide()) {
                if (!(world instanceof ServerLevel)) {
                    return InteractionResultHolder.fail(stack);
                }
                ServerLevel serverWorld = (ServerLevel) world;
                
                if (isKnockbackOnCooldown(stack, serverWorld.getGameTime())) {
                    return InteractionResultHolder.fail(stack);
                }
                
                doKnockbackBurst(serverWorld, player);
                setKnockbackCooldown(stack, serverWorld.getGameTime() + KB_COOLDOWN_TICKS);
                player.swing(hand);
            }
            return InteractionResultHolder.success(stack);
        } else {
            // 普通模式：钩爪传送（待实现VengeanceGrappleHandler）
            if (!world.isClientSide()) {
                // TODO: 实现钩爪逻辑
                // VengeanceGrappleHandler.grapple(player, RANGE);
            }
            player.swing(hand);
            return InteractionResultHolder.success(stack);
        }
    }
    
    /**
     * 检查击退是否在冷却中
     */
    public static boolean isKnockbackOnCooldown(ItemStack stack, long gameTime) {
        if (!stack.hasTag()) {
            return false;
        }
        long cooldownEnd = stack.getTag().getLong(NBT_KB_NEXT);
        return gameTime < cooldownEnd;
    }
    
    /**
     * 设置击退冷却
     */
    public static void setKnockbackCooldown(ItemStack stack, long endTime) {
        stack.getOrCreateTag().putLong(NBT_KB_NEXT, endTime);
    }
    
    /**
     * 执行击退爆发
     * 对周围5格内所有实体造成击退
     */
    public static void doKnockbackBurst(ServerLevel world, LivingEntity source) {
        Vec3 sourcePos = source.position();
        AABB aabb = new AABB(
            sourcePos.x - KB_RADIUS, sourcePos.y - KB_RADIUS, sourcePos.z - KB_RADIUS,
            sourcePos.x + KB_RADIUS, sourcePos.y + KB_RADIUS, sourcePos.z + KB_RADIUS
        );
        
        // 播放音效
        world.playSound(null, source.blockX(), source.blockY(), source.blockZ(), 
            ModSounds.ENTITY_ABO_AMBIENT.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
        
        // 生成粒子效果
        for (int i = 0; i < 20; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * KB_RADIUS * 2;
            double offsetY = (world.random.nextDouble() - 0.5) * KB_RADIUS * 2;
            double offsetZ = (world.random.nextDouble() - 0.5) * KB_RADIUS * 2;
            world.sendParticles(
                net.minecraft.core.particles.ParticleTypes.EXPLOSION,
                sourcePos.x + offsetX, sourcePos.y + offsetY, sourcePos.z + offsetZ,
                1, 0, 0, 0, 0
            );
        }
        
        // 对范围内所有实体施加击退
        List<Entity> entities = world.getEntities((Entity) null, aabb);
        for (Entity entity : entities) {
            if (entity == source || !entity.isAlive()) {
                continue;
            }
            
            Vec3 entityPos = entity.position();
            Vec3 direction = entityPos.subtract(sourcePos).normalize();
            
            // 如果是源实体自己，添加向上的跳跃速度
            if (entity == source) {
                entity.setDeltaMovement(direction.x * KB_STRENGTH, KB_SELF_JUMP_Y, direction.z * KB_STRENGTH);
            } else {
                // 对其他实体施加击退
                entity.setDeltaMovement(direction.x * KB_STRENGTH, KB_UP, direction.z * KB_STRENGTH);
                entity.hurtMarked = true;
            }
        }
    }
    
    private static final double KB_SELF_JUMP_Y = 0.42;
}
