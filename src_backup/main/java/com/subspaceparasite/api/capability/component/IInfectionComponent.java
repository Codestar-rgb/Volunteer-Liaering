package com.subspaceparasite.api.capability.component;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

/**
 * 感染组件接口
 * 
 * 管理寄生虫的感染传播逻辑：感染概率、感染范围、感染免疫等
 * 
 * @author SubspaceParasite Team
 * @version 1.20.1
 */
public interface IInfectionComponent {
    
    /**
     * 感染结果枚举
     */
    enum InfectedResult {
        /** 可以感染 */
        CAN_INFECT,
        /** 免疫感染 */
        IMMUNE,
        /** 已经是寄生虫 */
        ALREADY_PARASITE,
        /** 无效目标 */
        INVALID_TARGET
    }
    
    /**
     * 初始化感染组件
     * @param canSpread 是否可以传播感染
     * @param infectionChance 感染概率 (0.0-1.0)
     */
    void initialize(boolean canSpread, float infectionChance);
    
    /**
     * 获取是否可以传播感染
     * @return true 如果可以传播
     */
    boolean canSpreadInfection();
    
    /**
     * 设置是否可以传播感染
     * @param canSpread 是否可以传播
     */
    void setCanSpreadInfection(boolean canSpread);
    
    /**
     * 获取感染概率
     * @return 感染概率 (0.0-1.0)
     */
    float getInfectionChance();
    
    /**
     * 设置感染概率
     * @param chance 新的感染概率
     */
    void setInfectionChance(float chance);
    
    /**
     * 获取感染半径（方块单位）
     * @return 感染半径
     */
    float getInfectionRadius();
    
    /**
     * 设置感染半径
     * @param radius 新的感染半径
     */
    void setInfectionRadius(float radius);
    
    /**
     * 检查是否可以感染目标
     * @param target 目标实体
     * @return 感染结果
     */
    InfectedResult canInfect(LivingEntity target);
    
    /**
     * 尝试感染目标
     * @param target 目标实体
     * @return true 如果感染成功
     */
    boolean infect(LivingEntity target);
    
    /**
     * 在死亡时传播感染
     * @param position 死亡位置
     * @param level 世界等级
     */
    void spreadInfectionOnDeath(net.minecraft.world.level.Level level, net.minecraft.world.phys.Vec3 position);
    
    /**
     * 获取最大感染数量（单次死亡事件）
     * @return 最大感染数量
     */
    int getMaxInfectOnDeath();
    
    /**
     * 设置最大感染数量
     * @param count 最大感染数量
     */
    void setMaxInfectOnDeath(int count);
    
    /**
     * 添加感染免疫（用于已感染生物）
     * @param entity 实体
     */
    void addImmunity(LivingEntity entity);
    
    /**
     * 检查是否有感染免疫
     * @param entity 实体
     * @return true 如果有免疫
     */
    boolean hasImmunity(LivingEntity entity);
    
    /**
     * 移除感染免疫
     * @param entity 实体
     */
    void removeImmunity(LivingEntity entity);
    
    /**
     * 保存数据到 NBT
     * @param tag NBT 标签
     */
    void saveNBT(CompoundTag tag);
    
    /**
     * 从 NBT 加载数据
     * @param tag NBT 标签
     */
    void loadNBT(CompoundTag tag);
}
