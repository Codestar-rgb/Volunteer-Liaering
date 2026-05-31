package com.subspaceparasite.common.entity.monster.crude;

import com.subspaceparasite.api.entity.IParasiteEntity;
import com.subspaceparasite.api.enums.EvoPhase;
import com.subspaceparasite.api.enums.GeneType;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * EntityLeer - 凝视者（Tier 2  crude阶段精神攻击单位）
 * 
 * 功能：
 * - 远程精神攻击
 * - 恐惧光环效果
 * - 中等体型战斗单位
 * 
 * @author SubspaceParasite Team
 * @version 1.20.1
 */
public class EntityLeer extends EntityParasiteBase implements IParasiteEntity {

    // ==================== 常量定义 ====================

    private static final float BASE_HEALTH = 38.0f;
    private static final float BASE_ATTACK_DAMAGE = 7.0f;
    private static final float BASE_MOVEMENT_SPEED = 0.23f;
    private static final float BASE_ARMOR = 4.0f;

    private static final int FEAR_RANGE = 12; // 恐惧光环范围

    // ==================== 构造函数 ====================

    public EntityLeer(EntityType<? extends EntityLeer> entityType, Level level) {
        super(entityType, level);
        this.phase = EvoPhase.CRUDE;
        this.parasiteType = GeneType.CRUDE_LEER;
        this.xpValue = 15;
    }

    // ==================== AI 行为注册 ====================

    @Override
    protected void registerGoals() {
        super.registerGoals();

        // 基础目标
        this.goalSelector.addGoal(0, new FloatGoal(this));

        // 近战攻击目标
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1D, true));

        // 目标选择器
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(
            this, 
            LivingEntity.class, 
            FEAR_RANGE, 
            true, 
            false,
            living -> !this.isInfectedAlly(living)
        ));
        
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    // ==================== 声音事件 ====================

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SUBSRP_ENTITY_LEER_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.SUBSRP_ENTITY_LEER_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SUBSRP_ENTITY_LEER_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        // 无脚步声（悬浮特性）
    }

    // ==================== 实体属性 ====================

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return 1.8F;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityParasiteBase.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, BASE_HEALTH)
            .add(Attributes.MOVEMENT_SPEED, BASE_MOVEMENT_SPEED)
            .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
            .add(Attributes.ARMOR, BASE_ARMOR);
    }

    // ==================== IParasiteEntity 实现 ====================

    @Override
    public EvoPhase getParasitePhase() {
        return this.phase;
    }

    @Override
    public GeneType getGeneType() {
        return this.parasiteType;
    }

    // ==================== 辅助方法 ====================

    /**
     * 检查目标是否为感染的盟友
     */
    private boolean isInfectedAlly(LivingEntity target) {
        // TODO: 添加感染组件检查逻辑
        return false;
    }
}
