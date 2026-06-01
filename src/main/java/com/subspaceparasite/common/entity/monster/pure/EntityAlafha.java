package com.subspaceparasite.common.entity.monster.pure;

import com.subspaceparasite.common.entity.base.EntityPureBase;
import com.subspaceparasite.api.parasite.EvoPhase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * EntityAlafha - Pure阶段的Alpha领袖单位
 * 特性：群体指挥能力，强化周围寄生虫。
 * 对应资源：MCMOS_extracted/pure/alafha
 */
public class EntityAlafha extends EntityPureBase {
    private static final double BASE_HEALTH = 150.0;
    private static final double BASE_ATTACK_DAMAGE = 14.0;
    private static final double BASE_SPEED = 0.26;
    private static final double BASE_ARMOR = 10.0;

    public EntityAlafha(EntityType<? extends Monster> entityType, Level worldIn) {
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
