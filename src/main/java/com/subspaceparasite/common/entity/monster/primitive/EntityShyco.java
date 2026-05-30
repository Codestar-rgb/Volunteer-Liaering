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
 * 害羞伪装者 - Primitive Stage Parasite
 * 特性：伪装成方块
 * 行为：伏击攻击
 */
public class EntityShyco extends EntityParasiteBase {

    public EntityShyco(EntityType<? extends EntityParasiteBase> entityType, Level worldIn) {
        super(entityType, worldIn);
        this.xpReward = 10;
        this.setPhase(EvoPhase.PRIMITIVE);
        this.setParasiteType(ParasiteType.PRIM_SHYCO);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new FloatGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, 
            living -> !InfectionComponent.isInfected(living)));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SUBSRP_ENTITY_SHYCO_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSounds.SUBSRP_ENTITY_SHYCO_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SUBSRP_ENTITY_SHYCO_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return 1.5F;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityParasiteBase.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 24.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.22D)
            .add(Attributes.ATTACK_DAMAGE, 7.0D);
    }
}
