package com.subspaceparasite.common.item;

import com.subspaceparasite.core.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

/**
 * Greek Fire - 希腊火
 * 原版SRP特殊工具，用于点燃灌木丛集群
 * 
 * 功能:
 * - 右键点击感染灌木或寄生虫灌木
 * - 使用BFS算法点燃整个连接的灌木集群
 * - 最大传播范围12000方块
 * - 连接半径8格
 */
public class ItemGreekFire extends Item {
    
    private static final int MAX_SPREAD = 12000;
    private static final int LINK_RADIUS = 8;
    private static final int PARTICLE_EVERY = 8;
    private static final int MAX_PARTICLE_SPAWNS = 24;
    
    public ItemGreekFire() {
        super(new Properties().stacksTo(16).fireResistant());
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        
        Block block = world.getBlockState(pos).getBlock();
        if (!isTargetBush(block)) {
            return InteractionResult.FAIL;
        }
        
        burnBushCluster(world, pos);
        
        // 播放燃烧音效
        world.playSound(null, pos, net.minecraft.sounds.SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0f, 0.9f + world.random.nextFloat() * 0.2f);
        
        // 消耗物品
        if (player != null && !player.isCreative()) {
            context.getItemInHand().shrink(1);
        }
        
        return InteractionResult.SUCCESS;
    }
    
    /**
     * 判断是否是目标灌木（感染灌木或寄生虫灌木）
     */
    private boolean isTargetBush(Block block) {
        // Check for mod-specific infested and parasite bush blocks
        if (block == ModBlocks.INFESTED_BUSH.get() || block == ModBlocks.PARASITE_BUSH.get()) {
            return true;
        }
        // Fallback: check by block name for variants
        String blockName = block.getDescriptionId();
        return blockName.contains("infestedbush") || blockName.contains("parasitebush");
    }
    
    /**
     * 使用BFS算法燃烧整个灌木集群
     */
    private void burnBushCluster(Level world, BlockPos origin) {
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        Set<Long> visited = new HashSet<>();
        
        queue.add(origin);
        visited.add(packPos(origin.getX(), origin.getY(), origin.getZ()));
        
        int burnedCount = 0;
        int particleCounter = 0;
        
        while (!queue.isEmpty() && burnedCount < MAX_SPREAD) {
            BlockPos current = queue.pollFirst();
            
            // 燃烧当前灌木
            burnSingleBush(world, current);
            burnedCount++;
            
            // 生成粒子效果
            if (particleCounter % PARTICLE_EVERY == 0 && particleCounter / PARTICLE_EVERY < MAX_PARTICLE_SPAWNS) {
                spawnBurnParticles(world, current);
            }
            particleCounter++;
            
            // 查找周围连接的灌木
            for (BlockPos neighbor : BlockPos.betweenClosedStream(
                    current.offset(-LINK_RADIUS, -LINK_RADIUS, -LINK_RADIUS),
                    current.offset(LINK_RADIUS, LINK_RADIUS, LINK_RADIUS))
                    .map(BlockPos::immutable)
                    .toList()) {
                
                if (neighbor.equals(current)) continue;
                
                long packed = packPos(neighbor.getX(), neighbor.getY(), neighbor.getZ());
                if (visited.contains(packed)) continue;
                
                Block neighborBlock = world.getBlockState(neighbor).getBlock();
                if (isTargetBush(neighborBlock)) {
                    queue.addLast(neighbor);
                    visited.add(packed);
                }
            }
        }
    }
    
    /**
     * 燃烧单个灌木方块
     */
    private void burnSingleBush(Level world, BlockPos pos) {
        // 替换为燃烧后的方块或空气
        // TODO: 根据原版逻辑替换为相应的燃烧后状态
        world.setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
        
        // 生成火焰粒子（服务端使用sendParticles，客户端addParticle是no-op）
        if (world instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.FLAME,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                5, 0.5, 0.5, 0.5, 0.01);
        }
    }
    
    /**
     * 生成燃烧粒子效果（服务端使用sendParticles）
     */
    private void spawnBurnParticles(Level world, BlockPos pos) {
        if (world instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.SMOKE,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                3, 1.0, 1.0, 1.0, 0.02);
        }
    }
    
    /**
     * 将坐标打包为long值用于快速查找
     */
    private static long packPos(int x, int y, int z) {
        return (((long) x & 0xFFFFFFL) << 42) | (((long) y & 0xFFFFFFL) << 21) | ((long) z & 0xFFFFFFL);
    }
}
