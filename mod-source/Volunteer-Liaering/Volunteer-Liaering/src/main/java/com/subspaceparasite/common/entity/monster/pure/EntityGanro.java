package com.subspaceparasite.common.entity.monster.pure;

import com.subspaceparasite.common.entity.base.EntityPureBase;
import com.subspaceparasite.api.parasite.EvoPhase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * EntityGanro - Pure阶段的攀爬突袭者
 * 特性：墙壁攀爬能力，触手攻击，水陆两栖，跳跃突袭
 * 对应资源：MCMOS_extracted/pure/ganro
 *
 * 原版机制参考：
 * - 尺寸：0.901×4.2
 * - 拥有两条触手(tendril1, tendril2)作为独立身体部件
 * - 具备攀爬、潜水、跳跃突袭能力
 * - 技能冷却系统：80-100tick主技能，40tick副技能
 * - AOE近战攻击范围：8.0×4.0
 */
public class EntityGanro extends EntityPureBase {
    private static final double BASE_HEALTH = 180.0;
    private static final double BASE_ATTACK_DAMAGE = 16.0;
    private static final double BASE_SPEED = 0.27;
    private static final double BASE_ARMOR = 15.0;
    private static final double BASE_KNOCKBACK_RESISTANCE = 0.4;

    public EntityGanro(EntityType<? extends Monster> entityType, Level worldIn) {
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
     * 包括：游泳/潜水、跳跃突袭、AOE近战、闪避冲刺、技能释放
     */
    @Override
    protected void registerGoals() {
        super.registerGoals();
        // TODO: 添加EntityAISkill（主技能：80-100tick冷却）
        // TODO: 添加EntityAISwimmingDiving（潜水能力）
        // TODO: 添加EntityAIWaterLeapAtTargetStatus（水上跳跃突袭）
        // TODO: 添加EntityAIAttackMeleeStatusAOE（8.0×4.0范围）
        // TODO: 添加EntityAIEvadeDash（闪避冲刺）
    }
}
