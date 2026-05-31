package com.subspaceparasite.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * 寄生虫屏障方块实体 - 管理屏障力场的范围和效果
 * 基于原版 SRP TileEntityParasiteBarrier移植到1.20.1
 * 
 * 功能:
 * - 存储屏障中心位置和半径
 * - 定期清理屏障范围内的敌对生物
 */
public class TileEntityParasiteBarrier extends BlockEntity {
    private BlockPos centerPos;
    private int radiusChunks = 1; // 默认 1 区块半径 (16 格)
    private int tickCounter = 0;

    public TileEntityParasiteBarrier(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PARASITE_BARRIER.get(), pos, state);
    }

    /**
     * 从当前位置初始化屏障中心
     */
    public void initCenterFromPos(BlockPos pos) {
        this.centerPos = pos.immutable();
        setChanged();
    }

    /**
     * 获取屏障半径（以区块为单位）
     */
    public int getRadiusChunks() {
        return this.radiusChunks;
    }

    /**
     * 设置屏障半径
     */
    public void setRadiusChunks(int radius) {
        this.radiusChunks = Math.max(1, Math.min(10, radius)); // 限制 1-10 区块
        setChanged();
        
        // 重新清理周围生物
        if (getLevel() instanceof ServerLevel serverLevel) {
            scrubMobsAroundField();
        }
    }

    /**
     * 清理屏障范围内的所有敌对生物
     */
    public void scrubMobsAroundField() {
        if (level == null || centerPos == null) {
            return;
        }

        int radiusBlocks = radiusChunks * 16; // 转换为方块数
        
        // 获取屏障范围内的所有实体
        List<Entity> entities = level.getEntities(null, 
            new net.minecraft.world.phys.AABB(
                centerPos.getX() - radiusBlocks,
                centerPos.getY() - radiusBlocks,
                centerPos.getZ() - radiusBlocks,
                centerPos.getX() + radiusBlocks,
                centerPos.getY() + radiusBlocks,
                centerPos.getZ() + radiusBlocks
            ));

        for (Entity entity : entities) {
            if (entity instanceof Mob mob && isHostileMob(mob)) {
                // 移除敌对生物
                mob.discard();
            }
        }
    }

    /**
     * 判断是否为敌对生物
     */
    private boolean isHostileMob(Mob mob) {
        String mobName = mob.getType().getDescriptionId();
        return mobName.contains("parasite") || 
               mobName.contains("infested") ||
               mob.getTarget() != null;
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        
        if (centerPos != null) {
            compound.putInt("CenterX", centerPos.getX());
            compound.putInt("CenterY", centerPos.getY());
            compound.putInt("CenterZ", centerPos.getZ());
        }
        compound.putInt("RadiusChunks", radiusChunks);
        compound.putInt("TickCounter", tickCounter);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        
        if (compound.contains("CenterX")) {
            centerPos = new BlockPos(
                compound.getInt("CenterX"),
                compound.getInt("CenterY"),
                compound.getInt("CenterZ")
            );
        }
        radiusChunks = compound.getInt("RadiusChunks");
        tickCounter = compound.getInt("TickCounter");
    }

    @Override
    public Component getName() {
        return Component.translatable("block.subspaceparasite.parasitebarrier");
    }
}
