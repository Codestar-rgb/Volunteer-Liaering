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
 * EntityLum - Primitive Stage Parasite
 * 特性：发光诱饵，群体增益，夜间活跃
 * 行为：发出强光吸引生物，为周围寄生虫提供增益效果
 */
public class EntityLum extends EntityParasiteBase {

    public EntityLum(EntityType<? extends EntityParasiteBase> entityType, Level worldIn) {
        super(entityType, worldIn);
        this.xpReward = 9;
        this.setPhase(EvoPhase.PRIMITIVE);
        this.setParasiteType(ParasiteType.PRIM_LUM);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new FloatGoal(this));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false)); // 被动攻击
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, 
            living -> !InfectionComponent.isInfected(living)));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // 发光逻辑：为周围寄生虫提供速度增益
        if (!this.level().isClientSide && this.tickCount % 40 == 0) {
            this.level().getEntitiesOfClass(EntityParasiteBase.class, this.getBoundingBox().inflate(10.0D)).forEach(parasite -> {
                if (parasite != this && parasite.isAlive()) {
                    parasite.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 1, false, false));
                }
            });
        }
        // 设置发光等级
        this.setLightLevel(this.getBrightness());
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SUBSRP_ENTITY_LUM_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSounds.SUBSRP_ENTITY_LUM_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SUBSRP_ENTITY_LUM_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        // 轻微脚步声
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return 1.2F;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityParasiteBase.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 22.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.25D)
            .add(Attributes.ATTACK_DAMAGE, 4.0D)
            .add(Attributes.FOLLOW_RANGE, 32.0D);
    }
}
