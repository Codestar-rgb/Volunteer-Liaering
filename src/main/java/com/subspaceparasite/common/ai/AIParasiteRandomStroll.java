package com.subspaceparasite.common.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * 寄生虫随机移动 AI
 * 
 * 专为寄生虫设计的随机漫步 AI
 * 支持群体行为和殖民地范围内的移动优化
 * 
 * @author SubspaceParasite Team
 * @version 1.20.1
 */
public class AIParasiteRandomStroll<T extends PathfinderMob> extends Goal {
    
    protected final T mob;
    protected final double speedModifier;
    
    protected double wantedX;
    protected double wantedY;
    protected double wantedZ;
    
    /**
     * 是否优先向殖民地中心移动
     */
    private final boolean preferColonyCenter;
    
    /**
     * 殖民地中心位置（如果已知）
     */
    private double colonyCenterX;
    private double colonyCenterY;
    private double colonyCenterZ;
    
    /**
     * 殖民地半径
     */
    private double colonyRadius;
    
    /**
     * 是否在殖民地内
     */
    private boolean inColony;
    
    /**
     * 构造函数 - 基础版本
     */
    public AIParasiteRandomStroll(T mob, double speedModifier) {
        this(mob, speedModifier, false);
    }
    
    /**
     * 构造函数 - 完整版本
     * 
     * @param mob 宿主实体
     * @param speedModifier 移动速度乘数
     * @param preferColonyCenter 是否优先向殖民地中心移动
     */
    public AIParasiteRandomStroll(T mob, double speedModifier, boolean preferColonyCenter) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.preferColonyCenter = preferColonyCenter;
        this.setFlags(EnumSet.of(Flag.MOVE));
        
        // 默认殖民地设置
        this.colonyCenterX = 0;
        this.colonyCenterY = 0;
        this.colonyCenterZ = 0;
        this.colonyRadius = 50.0;
        this.inColony = false;
    }
    
    @Override
    public boolean canUse() {
        if (this.mob.isVehicle()) {
            return false;
        } else if (!this.mob.getMoveControl().hasWanted()) {
            // 没有当前移动目标时，可以开始随机移动
            return true;
        } else {
            // 检查是否已经到达目标
            return this.mob.getRandom().nextInt(reducedTickDelay(10)) == 0;
        }
    }
    
    @Override
    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone();
    }
    
    @Override
    public void start() {
        if (preferColonyCenter && inColony) {
            // 如果在殖民地内且启用殖民地偏好，向中心移动
            this.wantedX = colonyCenterX;
            this.wantedY = colonyCenterY;
            this.wantedZ = colonyCenterZ;
        } else {
            // 否则随机选择一个位置
            this.generateRandomPosition();
        }
        
        this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
    }
    
    /**
     * 生成随机目标位置
     */
    protected void generateRandomPosition() {
        // 获取宿主当前位置
        double currentX = this.mob.getX();
        double currentY = this.mob.getY();
        double currentZ = this.mob.getZ();
        
        // 生成随机偏移
        for (int i = 0; i < 10; ++i) {
            int offsetX = this.mob.getRandom().nextInt(2 * 8 + 1) - 8;
            int offsetY = this.mob.getRandom().nextInt(2 * 3 + 1) - 3;
            int offsetZ = this.mob.getRandom().nextInt(2 * 8 + 1) - 8;
            
            double targetX = currentX + (double)offsetX;
            double targetY = currentY + (double)offsetY;
            double targetZ = currentZ + (double)offsetZ;
            
            // 检查目标位置是否可行走
            if (this.mob.getNavigation().isStableDestination(net.minecraft.core.BlockPos.containing(targetX, targetY, targetZ))) {
                this.wantedX = targetX;
                this.wantedY = targetY;
                this.wantedZ = targetZ;
                return;
            }
        }
        
        // 如果没有找到合适位置，使用默认值
        this.wantedX = currentX;
        this.wantedY = currentY;
        this.wantedZ = currentZ;
    }
    
    /**
     * 设置殖民地中心位置
     */
    public void setColonyCenter(double x, double y, double z, double radius) {
        this.colonyCenterX = x;
        this.colonyCenterY = y;
        this.colonyCenterZ = z;
        this.colonyRadius = radius;
        
        // 检查是否在殖民地内
        double dx = this.mob.getX() - x;
        double dy = this.mob.getY() - y;
        double dz = this.mob.getZ() - z;
        this.inColony = (dx * dx + dy * dy + dz * dz) <= (radius * radius);
    }
    
    /**
     * 更新殖民地状态
     */
    public void updateColonyStatus() {
        double dx = this.mob.getX() - colonyCenterX;
        double dy = this.mob.getY() - colonyCenterY;
        double dz = this.mob.getZ() - colonyCenterZ;
        this.inColony = (dx * dx + dy * dy + dz * dz) <= (colonyRadius * colonyRadius);
    }
    
    @Override
    public void tick() {
        if (preferColonyCenter) {
            updateColonyStatus();
        }
    }
}
