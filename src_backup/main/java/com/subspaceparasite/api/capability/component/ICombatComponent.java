package com.subspaceparasite.api.capability.component;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;

/**
 * 战斗组件接口
 * 
 * 管理实体的战斗相关属性：生命值、攻击力、护甲值、适应度等
 * 
 * @author SubspaceParasite Team
 * @version 1.20.1
 */
public interface ICombatComponent {
    
    /**
     * 初始化战斗组件
     * @param maxHealth 最大生命值
     * @param attackDamage 攻击力
     * @param armor 护甲值
     */
    void initialize(float maxHealth, float attackDamage, float armor);
    
    /**
     * 获取当前生命值
     * @return 当前生命值
     */
    float getCurrentHealth();
    
    /**
     * 获取最大生命值
     * @return 最大生命值
     */
    float getMaxHealth();
    
    /**
     * 设置最大生命值
     * @param health 新的最大生命值
     */
    void setMaxHealth(float health);
    
    /**
     * 获取攻击力
     * @return 攻击力
     */
    float getAttackDamage();
    
    /**
     * 设置攻击力
     * @param damage 新的攻击力
     */
    void setAttackDamage(float damage);
    
    /**
     * 获取护甲值
     * @return 护甲值
     */
    float getArmor();
    
    /**
     * 设置护甲值
     * @param armor 新的护甲值
     */
    void setArmor(float armor);
    
    /**
     * 获取击退抗性
     * @return 击退抗性 (0.0-1.0)
     */
    float getKnockbackResistance();
    
    /**
     * 设置击退抗性
     * @param resistance 新的击退抗性
     */
    void setKnockbackResistance(float resistance);
    
    /**
     * 获取适应度值（用于 SRP 的适应机制）
     * @return 适应度
     */
    float getAdaptation();
    
    /**
     * 增加适应度
     * @param amount 增加量
     */
    void addAdaptation(float amount);
    
    /**
     * 重置适应度
     */
    void resetAdaptation();
    
    /**
     * 获取伤害减免比例（基于适应度和护甲）
     * @param source 伤害来源
     * @return 伤害减免比例 (0.0-1.0)
     */
    float getDamageReduction(DamageSource source);
    
    /**
     * 应用伤害（考虑适应度和护甲）
     * @param amount 原始伤害量
     * @param source 伤害来源
     * @return 实际受到的伤害
     */
    float applyDamage(float amount, DamageSource source);
    
    /**
     * 治疗实体
     * @param amount 治疗量
     * @return 实际治疗量
     */
    float heal(float amount);
    
    /**
     * 判断是否存活
     * @return true 如果存活
     */
    boolean isAlive();
    
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
