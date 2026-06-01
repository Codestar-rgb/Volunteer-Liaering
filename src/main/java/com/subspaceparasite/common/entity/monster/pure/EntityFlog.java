package com.subspaceparasite.common.entity.monster.pure;

import com.subspaceparasite.common.entity.base.EntityPureBase;
import com.subspaceparasite.api.parasite.EvoPhase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * EntityFlog - Pure阶段的Flog变体
 * 特性：重型地面单位，具有冲击能力。
 * 对应资源：MCMOS_extracted/pure/flog
 */
public class EntityFlog extends EntityPureBase {
    private static final double BASE_HEALTH = 160.0;
    private static final double BASE_ATTACK_DAMAGE = 15.0;
    private static final double BASE_SPEED = 0.23;
    private static final double BASE_ARMOR = 11.0;

    public EntityFlog(EntityType<? extends Monster> entityType, Level worldIn) {
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
