package com.subspaceparasite.common.entity.monster.pure;

import com.subspaceparasite.common.entity.base.EntityPureBase;
import com.subspaceparasite.api.parasite.EvoPhase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
public class EntityOmboo extends EntityPureBase {
    private static final double BASE_HEALTH = 160.0;
    private static final double BASE_ATTACK_DAMAGE = 12.0;
    private static final double BASE_SPEED = 0.32;
    private static final double BASE_ARMOR = 12.0;
    private static final double BASE_KNOCKBACK_RESISTANCE = 0.3;

    public EntityOmboo(EntityType<? extends Monster> entityType, Level worldIn) {
        super(entityType, worldIn);
        this.setPhaseCreated(EvoPhase.PHASE_4);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityPureBase.createAttributes()
                .add(Attributes.MAX_HEALTH, BASE_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, BASE_SPEED)
                .add(Attributes.ARMOR, BASE_ARMOR)
                .add(Attributes.KNOCKBACK_RESISTANCE, BASE_KNOCKBACK_RESISTANCE);
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
