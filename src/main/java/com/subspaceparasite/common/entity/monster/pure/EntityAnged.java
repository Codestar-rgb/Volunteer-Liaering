package com.subspaceparasite.common.entity.monster.pure;

import com.subspaceparasite.common.entity.base.EntityPureBase;
import com.subspaceparasite.api.parasite.EvoPhase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * EntityAnged - Pure阶段的愤怒变体
 * 特性：高攻击力，狂暴状态下伤害倍增。
 * 对应资源：MCMOS_extracted/pure/anged
 */
public class EntityAnged extends EntityPureBase {
    private static final double BASE_HEALTH = 130.0;
    private static final double BASE_ATTACK_DAMAGE = 16.0;
    private static final double BASE_SPEED = 0.28;
    private static final double BASE_ARMOR = 9.0;

    public EntityAnged(EntityType<? extends Monster> entityType, Level worldIn) {
        super(entityType, worldIn);
        this.setPhaseCreated(EvoPhase.PHASE_4);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityPureBase.createAttributes()
                .add(Attributes.MAX_HEALTH, BASE_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, BASE_SPEED)
                .add(Attributes.ARMOR, BASE_ARMOR);
    }
}
