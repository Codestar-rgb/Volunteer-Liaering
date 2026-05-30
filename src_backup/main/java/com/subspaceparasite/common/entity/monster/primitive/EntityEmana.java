package com.subspaceparasite.common.entity.monster.primitive;

import com.subspaceparasite.api.core.component.infection.InfectionComponent;
import com.subspaceparasite.api.core.data.type.EvoPhase;
import com.subspaceparasite.api.core.data.type.ParasiteType;
import com.subspaceparasite.common.entity.ai.goal.*;
import com.subspaceparasite.common.entity.monster.EntityParasiteBase;
import com.subspaceparasite.registry.ModEntities;
import com.subspaceparasite.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * EntityEmana - Primitive Stage Parasite
 * 特性：电磁干扰，远程瘫痪，对机械/红石单位有额外效果
 * 行为：悬浮移动，发射电磁脉冲，使目标短暂无法移动或使用物品
 */
public class EntityEmana extends EntityParasiteBase {

    public EntityEmana(EntityType<? extends EntityParasiteBase> entityType, Level worldIn) {
        super(entityType, worldIn);
        this.xpReward = 10;
        this.setPhase(EvoPhase.PRIMITIVE);
        this.setParasiteType(ParasiteType.PRIM_EMANA);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        
        // 主动攻击目标
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, true));
        // 电磁脉冲远程攻击 (待实现投射物逻辑，暂用近战替代)
        this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, LivingEntity.class, 6.0F, 1.0D, 1.2D, living -> {
            return InfectionComponent.isInfected(living) && !InfectionComponent.isSameStrain(living, this);
        }));
        // 优先攻击非感染生物
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, 
            living -> !InfectionComponent.isInfected(living)));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        
        // 特殊AI：悬浮移动
        this.goalSelector.addGoal(2, new FloatGoal(this));
    }

    @Override
    public boolean onClimbable() {
        return false; // Emana 悬浮，不攀爬
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        // 悬浮逻辑：保持离地高度
        if (!this.onGround() && this.getDeltaMovement().y < 0.0D) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // 电磁场效果：周围非感染生物获得缓慢效果
        if (!this.level().isClientSide && this.tickCount % 20 == 0) {
            this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(8.0D), 
                living -> !InfectionComponent.isInfected(living)).forEach(living -> {
                if (living.canBeSeenByEveryone()) {
                    // TODO: 施加自定义 "Electromagnetic Paralysis" 效果
                    // living.addEffect(new MobEffectInstance(ModEffects.ELECTRO_PARALYSIS.get(), 40, 0));
                }
            });
        }
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        // 死亡时释放电磁冲击波
        if (!this.level().isClientSide) {
            this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(5.0D), 
                living -> !InfectionComponent.isInfected(living)).forEach(living -> {
                // TODO: 施加短暂眩晕效果
            });
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SUBSRP_ENTITY_EMANA_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSounds.SUBSRP_ENTITY_EMANA_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SUBSRP_ENTITY_EMANA_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        // 悬浮生物无脚步声
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return 1.6F;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityParasiteBase.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 25.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.28D)
            .add(Attributes.ATTACK_DAMAGE, 5.0D)
            .add(Attributes.ATTACK_KNOCKBACK, 0.5D)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.3D)
            .add(Attributes.FOLLOW_RANGE, 48.0D);
    }
}
