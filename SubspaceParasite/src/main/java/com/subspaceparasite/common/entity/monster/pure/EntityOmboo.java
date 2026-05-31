package com.subspaceparasite.common.entity.monster.pure;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.util.EvoPhase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * EntityOmboo - Pure阶段的飞行轰炸者
 * 特性：飞行能力，投掷炸弹，高机动性
 * 对应资源：MCMOS_extracted/pure/omboo
 * 
 * 原版机制参考：
 * - 尺寸：1.7×2.4
 * - 具备飞行能力（EntityCanFly）
 * - 投掷炸弹攻击（EntityBomb）
 * - 飞行高度限制
 * - 适应力上限：0.95
 */
public class EntityOmboo extends EntityParasiteBase {
    // 基础属性值（待配置文件加载）
    public static final float MAX_HEALTH = 160.0F;
    public static final float ATTACK_DAMAGE = 12.0F;
    public static final float MOVEMENT_SPEED = 0.32F;
    public static final float ARMOR = 12.0F;
    public static final float KNOCKBACK_RESISTANCE = 0.3F;

    public EntityOmboo(EntityType<EntityOmboo> type, Level world) {
        super(type, world, EvoPhase.FOUR);
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(MAX_HEALTH).setBaseValue(MAX_HEALTH);
        this.getAttribute(ATTACK_DAMAGE).setBaseValue(ATTACK_DAMAGE);
        this.getAttribute(MOVEMENT_SPEED).setBaseValue(MOVEMENT_SPEED);
        this.getAttribute(ARMOR).setBaseValue(ARMOR);
        this.getAttribute(KNOCKBACK_RESISTANCE).setBaseValue(KNOCKBACK_RESISTANCE);
    }

    /**
     * 注册AI目标
     * 包括：飞行攻击、飞行限制、投弹攻击
     */
    @Override
    protected void registerGoals() {
        super.registerGoals();
        // TODO: 添加EntityAIFlightAttack（飞行攻击）
        // TODO: 添加EntityAIFlightLimits（飞行高度限制）
        // TODO: 添加EntityAIAttackProjectile（投弹攻击）
    }
}
