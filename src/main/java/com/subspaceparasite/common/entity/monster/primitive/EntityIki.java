package com.subspaceparasite.common.entity.monster.primitive;

import com.subspaceparasite.api.core.component.infection.InfectionComponent;
import com.subspaceparasite.api.core.data.type.EvoPhase;
import com.subspaceparasite.api.core.data.type.ParasiteType;
import com.subspaceparasite.common.entity.monster.EntityParasiteBase;
import com.subspaceparasite.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * EntityIki - Primitive Stage Parasite
 * 特性：隐形能力，偷袭型单位，低血量高爆发
 * 行为：平时隐形，攻击时显形，优先攻击落单目标
 */
public class EntityIki extends EntityParasiteBase {

    public EntityIki(EntityType<? extends EntityParasiteBase> entityType, Level worldIn) {
        super(entityType, worldIn);
        this.xpReward = 10;
        this.setPhase(EvoPhase.PRIMITIVE);
        this.setParasiteType(ParasiteType.PRIM_IKI);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new FloatGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.4D, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, 
            living -> !InfectionComponent.isInfected(living)));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // 隐形逻辑：无目标时隐形
        if (!this.level().isClientSide && this.getTarget() == null) {
            this.setInvisible(true);
        } else {
            this.setInvisible(false);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SUBSRP_ENTITY_IKI_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSounds.SUBSRP_ENTITY_IKI_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SUBSRP_ENTITY_IKI_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        // 轻微脚步声
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return 1.4F;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityParasiteBase.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 20.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.32D)
            .add(Attributes.ATTACK_DAMAGE, 8.0D) // 高爆发
            .add(Attributes.ATTACK_SPEED, 0.7D);
    }
}
