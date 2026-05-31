package com.subspaceparasite.common.entity.monster.pure;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.util.EvoPhase;
import net.minecraft.world.entity.EntityType;
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
public class EntityGanro extends EntityParasiteBase {
    // 基础属性值（待配置文件加载）
    public static final float MAX_HEALTH = 180.0F;
    public static final float ATTACK_DAMAGE = 16.0F;
    public static final float MOVEMENT_SPEED = 0.27F;
    public static final float ARMOR = 15.0F;
    public static final float KNOCKBACK_RESISTANCE = 0.4F;

    public EntityGanro(EntityType<EntityGanro> type, Level world) {
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
