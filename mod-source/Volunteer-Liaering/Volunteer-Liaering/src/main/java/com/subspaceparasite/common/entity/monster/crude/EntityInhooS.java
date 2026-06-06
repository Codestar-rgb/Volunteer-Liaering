package com.subspaceparasite.common.entity.monster.crude;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.common.entity.base.InfectionComponent;
import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.common.entity.base.EntityCrudeBase;
import com.subspaceparasite.core.ModSounds;
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
 * 吸入者 S - Crude Stage Parasite
 * 特性：高速突进击退免疫
 * 
 * 属性设计说明：
 * - MAX_HEALTH: 36.0 - 较低血量，突击型单位
 * - MOVEMENT_SPEED: 0.32 - 高移动速度
 * - ATTACK_DAMAGE: 6.5 - 中等偏低攻击力，依靠速度和突袭
 */
public class EntityInhooS extends EntityCrudeBase {

    public EntityInhooS(EntityType<? extends EntityCrudeBase> entityType, Level worldIn) {
        super(entityType, worldIn);
        this.xpReward = 15;
        this.setPhaseCreated(EvoPhase.PHASE_2);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // 基础浮游行为
        this.goalSelector.addGoal(2, new FloatGoal(this));
        // 近战攻击行为，速度1.1D，启用路径跟踪
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.1D, true));
        // 目标选择：攻击非感染者生物，检测范围12格
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 12, true, false, 
            living -> !InfectionComponent.isInfected(living)));
        // 受伤反击机制
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SUBSRP_ENTITY_INHOOS_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSounds.SUBSRP_ENTITY_INHOOS_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SUBSRP_ENTITY_INHOOS_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        // 无脚步声，符合寄生虫特性
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return 1.8F;
    }

    /**
     * 创建实体属性配置
     * 采用静态方法以支持Forge实体属性注册系统
     */
    public static AttributeSupplier.Builder createAttributes() {
        return EntityCrudeBase.createAttributes()
            .add(Attributes.MAX_HEALTH, 36.0D)      // 较低血量
            .add(Attributes.MOVEMENT_SPEED, 0.32D)  // 高移动速度
            .add(Attributes.ATTACK_DAMAGE, 6.5D);   // 中等偏低攻击力
    }
}
