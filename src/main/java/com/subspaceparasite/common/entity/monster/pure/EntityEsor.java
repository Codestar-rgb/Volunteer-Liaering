package com.subspaceparasite.common.entity.monster.pure;

import com.subspaceparasite.common.entity.base.EntityPureBase;
import com.subspaceparasite.api.parasite.EvoPhase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * EntityEsor - Pure阶段的触手突袭者
 * 特性：双触手身体部件，攀爬能力，激怒技能，水陆两栖
 * 对应资源：MCMOS_extracted/pure/esor
 *
 * 原版机制参考：
 * - 尺寸：0.901×4.2
 * - 拥有两条触手(leftTendril, rightTendril)作为独立身体部件
 * - 触手有独立生命值（基于本体生命值的百分比）
 * - 具备攀爬、潜水、跳跃突袭能力
 * - 技能系统：100tick冷却，激怒状态
 */
public class EntityEsor extends EntityPureBase {
    private static final double BASE_HEALTH = 200.0;
    private static final double BASE_ATTACK_DAMAGE = 18.0;
    private static final double BASE_SPEED = 0.28;
    private static final double BASE_ARMOR = 20.0;
    private static final double BASE_KNOCKBACK_RESISTANCE = 0.5;

    public EntityEsor(EntityType<? extends Monster> entityType, Level worldIn) {
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
     * 包括：游泳/潜水、跳跃突袭、AOE近战、闪避、技能释放
     */
    @Override
    protected void registerGoals() {
        super.registerGoals();
        // TODO: 添加EntityAISkill（100tick冷却，激怒技能）
        // TODO: 添加EntityAISwimmingDiving（潜水能力）
        // TODO: 添加EntityAIWaterLeapAtTargetStatus（水上跳跃突袭）
        // TODO: 添加EntityAIAttackMeleeStatusAOE（范围攻击）
        // TODO: 添加EntityAIEvade（闪避机制）
    }
}
