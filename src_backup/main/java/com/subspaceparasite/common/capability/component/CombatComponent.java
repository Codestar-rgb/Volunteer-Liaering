package com.subspaceparasite.common.capability.component;

import com.subspaceparasite.api.capability.component.ICombatComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * 战斗组件实现
 * 
 * 管理实体的战斗相关属性：生命值、攻击力、护甲值、适应度等
 * 
 * @author SubspaceParasite Team
 * @version 1.20.1
 */
public class CombatComponent implements ICombatComponent {
    
    // ==================== 能力标识 ====================
    
    public static final String COMPONENT_CAPABILITY = "subspaceparasite:combat_component";
    
    // ==================== 成员变量 ====================
    
    private final LivingEntity host;
    
    private float maxHealth;
    private float attackDamage;
    private float armor;
    private float knockbackResistance;
    private float adaptation;
    private float currentHealth;
    
    // ==================== 常量定义 ====================
    
    private static final float BASE_ARMOR_DAMAGE_REDUCTION = 0.04f; // 每点护甲减少 4% 伤害
    private static final float ADAPTATION_DAMAGE_REDUCTION_CAP = 0.8f; // 适应度最大减伤 80%
    private static final float ADAPTATION_GAIN_RATE = 0.05f; // 每次受击获得 0.05 适应度
    
    // ==================== 构造函数 ====================
    
    public CombatComponent(LivingEntity host) {
        this.host = host;
        this.maxHealth = 20.0f;
        this.attackDamage = 2.0f;
        this.armor = 0.0f;
        this.knockbackResistance = 0.0f;
        this.adaptation = 0.0f;
        this.currentHealth = 20.0f;
    }
    
    // ==================== ICombatComponent 实现 ====================
    
    @Override
    public void initialize(float maxHealth, float attackDamage, float armor) {
        this.maxHealth = maxHealth;
        this.attackDamage = attackDamage;
        this.armor = armor;
        this.currentHealth = maxHealth;
        
        // 同步到实体属性
        if (host.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH) != null) {
            host.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH).setBaseValue(maxHealth);
        }
        if (host.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE) != null) {
            host.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).setBaseValue(attackDamage);
        }
        if (host.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR) != null) {
            host.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR).setBaseValue(armor);
        }
    }
    
    @Override
    public float getCurrentHealth() {
        return host.getHealth();
    }
    
    @Override
    public float getMaxHealth() {
        return maxHealth;
    }
    
    @Override
    public void setMaxHealth(float health) {
        this.maxHealth = health;
        if (host.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH) != null) {
            host.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH).setBaseValue(health);
        }
        // 如果当前血量超过新最大值，调整为最大值
        if (host.getHealth() > health) {
            host.setHealth(health);
        }
    }
    
    @Override
    public float getAttackDamage() {
        return attackDamage;
    }
    
    @Override
    public void setAttackDamage(float damage) {
        this.attackDamage = damage;
        if (host.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE) != null) {
            host.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).setBaseValue(damage);
        }
    }
    
    @Override
    public float getArmor() {
        return armor;
    }
    
    @Override
    public void setArmor(float armor) {
        this.armor = armor;
        if (host.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR) != null) {
            host.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR).setBaseValue(armor);
        }
    }
    
    @Override
    public float getKnockbackResistance() {
        return knockbackResistance;
    }
    
    @Override
    public void setKnockbackResistance(float resistance) {
        this.knockbackResistance = resistance;
        if (host.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE) != null) {
            host.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE).setBaseValue(resistance);
        }
    }
    
    @Override
    public float getAdaptation() {
        return adaptation;
    }
    
    @Override
    public void addAdaptation(float amount) {
        this.adaptation = Math.min(this.adaptation + amount, 1.0f);
    }
    
    @Override
    public void resetAdaptation() {
        this.adaptation = 0.0f;
    }
    
    @Override
    public float getDamageReduction(DamageSource source) {
        // 计算护甲减伤
        float armorReduction = armor * BASE_ARMOR_DAMAGE_REDUCTION;
        armorReduction = Math.min(armorReduction, 0.8f); // 护甲最多减伤 80%
        
        // 计算适应度减伤
        float adaptationReduction = adaptation * ADAPTATION_DAMAGE_REDUCTION_CAP;
        
        // 总减伤（乘法叠加）
        float totalReduction = 1.0f - (1.0f - armorReduction) * (1.0f - adaptationReduction);
        
        return totalReduction;
    }
    
    @Override
    public float applyDamage(float amount, DamageSource source) {
        float reduction = getDamageReduction(source);
        float actualDamage = amount * (1.0f - reduction);
        
        // 增加适应度
        if (amount > 0) {
            addAdaptation(ADAPTATION_GAIN_RATE);
        }
        
        return actualDamage;
    }
    
    @Override
    public float heal(float amount) {
        float oldHealth = host.getHealth();
        host.heal(amount);
        return host.getHealth() - oldHealth;
    }
    
    @Override
    public boolean isAlive() {
        return host.isAlive();
    }
    
    // ==================== NBT 保存/加载 ====================
    
    @Override
    public void saveNBT(CompoundTag tag) {
        tag.putFloat("MaxHealth", maxHealth);
        tag.putFloat("AttackDamage", attackDamage);
        tag.putFloat("Armor", armor);
        tag.putFloat("KnockbackResistance", knockbackResistance);
        tag.putFloat("Adaptation", adaptation);
        tag.putFloat("CurrentHealth", currentHealth);
    }
    
    @Override
    public void loadNBT(CompoundTag tag) {
        if (tag.contains("MaxHealth")) {
            this.maxHealth = tag.getFloat("MaxHealth");
        }
        if (tag.contains("AttackDamage")) {
            this.attackDamage = tag.getFloat("AttackDamage");
        }
        if (tag.contains("Armor")) {
            this.armor = tag.getFloat("Armor");
        }
        if (tag.contains("KnockbackResistance")) {
            this.knockbackResistance = tag.getFloat("KnockbackResistance");
        }
        if (tag.contains("Adaptation")) {
            this.adaptation = tag.getFloat("Adaptation");
        }
        if (tag.contains("CurrentHealth")) {
            this.currentHealth = tag.getFloat("CurrentHealth");
        }
    }
}
