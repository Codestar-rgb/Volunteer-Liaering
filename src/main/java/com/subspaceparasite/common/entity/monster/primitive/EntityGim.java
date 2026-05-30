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
 * EntityGim - Primitive Stage Parasite
 * 特性：高机动性，快速突袭，低血量高伤害
 * 行为：高速移动，跳跃攻击，优先攻击脆弱目标
 */
public class EntityGim extends EntityParasiteBase {

    public EntityGim(EntityType<? extends EntityParasiteBase> entityType, Level worldIn) {
        super(entityType, worldIn);
        this.xpReward = 8;
        this.setPhase(EvoPhase.PRIMITIVE);
        this.setParasiteType(ParasiteType.PRIM_GIM);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new FloatGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.6D, true)); // 高速移动
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, 
            living -> !InfectionComponent.isInfected(living)));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // 高速跳跃逻辑
        if (this.onGround() && this.tickCount % 40 == 0 && this.getTarget() != null) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.8D, 0.0D));
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SUBSRP_ENTITY_GIM_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSounds.SUBSRP_ENTITY_GIM_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SUBSRP_ENTITY_GIM_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        // 轻量级脚步声
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return 0.9F;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityParasiteBase.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 18.0D) // 低血量
            .add(Attributes.MOVEMENT_SPEED, 0.35D) // 高速
            .add(Attributes.ATTACK_DAMAGE, 7.0D) // 高伤害
            .add(Attributes.ATTACK_SPEED, 0.8D)
            .add(Attributes.JUMP_STRENGTH, 1.2D);
    }
}
