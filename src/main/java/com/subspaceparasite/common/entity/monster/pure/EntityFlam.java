package com.subspaceparasite.common.entity.monster.pure;

import com.subspaceparasite.common.entity.base.EntityPureBase;
import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.core.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * EntityFlam - Pure阶段的火焰变体
 * 特性：火焰攻击能力，可点燃目标。
 * 对应资源：MCMOS_extracted/pure/flam
 */
public class EntityFlam extends EntityPureBase {
    private static final double BASE_HEALTH = 125.0;
    private static final double BASE_ATTACK_DAMAGE = 14.5;
    private static final double BASE_SPEED = 0.27;
    private static final double BASE_ARMOR = 8.0;

    public EntityFlam(EntityType<? extends Monster> entityType, Level worldIn) {
        super(entityType, worldIn);
        this.setPhaseCreated(EvoPhase.PHASE_4);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SUBSRP_ENTITY_FLAM_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.SUBSRP_ENTITY_FLAM_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SUBSRP_ENTITY_FLAM_DEATH.get();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityPureBase.createAttributes()
                .add(Attributes.MAX_HEALTH, BASE_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, BASE_SPEED)
                .add(Attributes.ARMOR, BASE_ARMOR);
    }
}
