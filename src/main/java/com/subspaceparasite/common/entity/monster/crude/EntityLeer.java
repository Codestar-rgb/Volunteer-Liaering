package com.subspaceparasite.common.entity.monster.crude;

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
 * 凝视者 - Crude Stage Parasite
 * 特性：远程精神攻击恐惧光环
 */
public class EntityLeer extends EntityParasiteBase {

    public EntityLeer(EntityType<? extends EntityParasiteBase> entityType, Level worldIn) {
        super(entityType, worldIn);
        this.xpReward = 15;
        this.setPhase(EvoPhase.CRUDE);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new FloatGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.1D, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 12, true, false, 
            living -> !InfectionComponent.isInfected(living)));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SUBSRP_ENTITY_LEER_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSounds.SUBSRP_ENTITY_LEER_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SUBSRP_ENTITY_LEER_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return 1.8F;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityParasiteBase.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 38.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.23D)
            .add(Attributes.ATTACK_DAMAGE, 7.0D);
    }
}
