package com.subspaceparasite.common.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * 寄生虫攻击 AI 目标
 * 
 * 专为寄生虫设计的高性能近战攻击 AI
 * 支持适应度系统和进化增益
 * 
 * @author SubspaceParasite Team
 * @version 1.20.1
 */
public class AIParasiteAttack<T extends PathfinderMob> extends Goal {
    
    protected final T mob;
    protected final double speedModifier;
    protected final boolean followingTargetEvenIfNotSeen;
    
    protected int attackTick;
    protected int delayedAttackNumTicks;
    protected double oldHeadYaw;
    protected double oldHeadPitch;
    
    /**
     * 构造函数
     * 
     * @param mob 宿主实体
     * @param speedModifier 移动速度乘数
     * @param followingTargetEvenIfNotSeen 即使看不见目标也继续追踪
     */
    public AIParasiteAttack(T mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.followingTargetEvenIfNotSeen = followingTargetEvenIfNotSeen;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }
    
    @Override
    public boolean canUse() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity == null) {
            return false;
        } else if (!livingentity.isAlive()) {
            return false;
        } else {
            return this.mob.canAttack(livingentity);
        }
    }
    
    @Override
    public boolean canContinueToUse() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity == null) {
            return false;
        } else if (!livingentity.isAlive()) {
            return false;
        } else if (this.followingTargetEvenIfNotSeen) {
            return !this.mob.getNavigation().isDone();
        } else {
            return this.mob.canAttack(livingentity) && this.mob.hasLineOfSight(livingentity);
        }
    }
    
    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.mob.getTarget(), this.speedModifier);
        this.attackTick = 0;
        this.delayedAttackNumTicks = 0; // 重置延迟攻击计数
    }
    
    @Override
    public void stop() {
        LivingEntity target = this.mob.getTarget();
        if (target != null) {
            // 保存头部旋转状态用于平滑过渡
            this.oldHeadYaw = mob.yHeadRot;
            this.oldHeadPitch = mob.getXRot();
        }
        this.mob.getNavigation().stop();
    }
    
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
    
    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target == null) return;
        
        // 更新路径
        this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        double distanceSquared = this.mob.distanceToSqr(target);
        
        // 检查是否可以进行攻击
        if (distanceSquared <= this.mob.getMeleeAttackRangeSq(target)) {
            this.delayedAttackNumTicks--;
            
            if (this.delayedAttackNumTicks <= 0) {
                // 执行攻击
                if (this.mob.getSensing().hasLineOfSight(target)) {
                    this.attackTick = this.mob.tickCount;
                    this.mob.doHurtTarget(target);
                    
                    // 设置下次攻击延迟（基于攻击速度）
                    float attackSpeed = (float) this.mob.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED);
                    this.delayedAttackNumTicks = Math.max(0, 20 - (int)(attackSpeed * 4));
                }
            }
        } else {
            // 距离太远，重新计算路径
            if (this.delayedAttackNumTicks > 0) {
                --this.delayedAttackNumTicks;
            }
            
            // 如果目标消失或太远，停止追踪
            if (!this.followingTargetEvenIfNotSeen) {
                if (this.mob.getSensing().hasLineOfSight(target)) {
                    this.mob.getNavigation().moveTo(target, this.speedModifier);
                }
            } else if (!this.mob.isWithinRestriction(target.blockPosition())) {
                this.mob.getNavigation().moveTo(target, this.speedModifier);
            }
        }
    }
    
    /**
     * 获取当前攻击冷却进度（0-1）
     * 可用于动画系统
     */
    public float getAttackProgress(float partialTicks) {
        if (this.attackTick <= 0) {
            return 0.0F;
        }
        
        int elapsed = this.mob.tickCount - this.attackTick + (int)(partialTicks * 20.0F);
        float cooldown = (float)this.mob.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED);
        float maxTime = 20.0F / (cooldown * 4.0F);
        
        return Math.min(1.0F, elapsed / maxTime);
    }
}
