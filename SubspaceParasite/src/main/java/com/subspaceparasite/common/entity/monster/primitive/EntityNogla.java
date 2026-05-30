package com.subspaceparasite.common.entity.monster.primitive;

import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.core.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

/**
 * EntityNogla - Flying Scout Primitive stage parasite.
 * <p>
 * Original SRP 1.12.2: EntityNogla in primitive stage.
 * Modernized for Forge 1.20.1 with component-based architecture.
 * <p>
 * Stats (configurable):
 * - Health: 16.0
 * - Attack Damage: 5.0
 * - Speed: 0.38 (high mobility)
 * - Flying Speed: 0.6 (very fast flight)
 * <p>
 * Behavior:
 * - Flying scout mob with high mobility
 * - Hovers at altitude, dive-bombs on targets
 * - Low health but very fast movement
 * - Spreads infection on death
 */
public class EntityNogla extends EntityParasiteBase {

    private static final double BASE_HEALTH = 16.0;
    private static final double BASE_ATTACK_DAMAGE = 5.0;
    private static final double BASE_SPEED = 0.38;
    private static final double BASE_FLYING_SPEED = 0.6;
    
    public EntityNogla(EntityType<? extends EntityNogla> type, Level world) {
        super(type, world);
        this.xpReward = 8;
        this.setCanPickUpLoot(false);
        this.phaseCreated = EvoPhase.ONE;
        this.parasiteType = ParasiteType.PRI_NOGLA;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.5, true));
        
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 16, true, false,
            living -> !this.isInfected(living)));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    @Override
    public boolean canFly() {
        return true;
    }

    @Override
    public boolean isFlapping() {
        return this.isFlying() && this.tickCount % 8 == 0;
    }

    @Override
    public boolean onClimbable() {
        return false;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        // Flight logic: maintain altitude and reduce fall speed
        if (!this.onGround() && this.getDeltaMovement().y < 0.0D) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.5D, 1.0D));
        }
        this.updateSwingTime();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SUBSRP_ENTITY_NOGLA_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.SUBSRP_ENTITY_NOGLA_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SUBSRP_ENTITY_NOGLA_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        // Flying mob has no footstep sounds
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, net.minecraft.world.entity.EntityDimensions sizeIn) {
        return 0.8F;
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide()) {
            this.spreadInfectionOnDeath(4.0, 1);
        }
        super.die(source);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMonsterAttributes()
            .add(Attributes.FOLLOW_RANGE, 32.0)
            .add(Attributes.MAX_HEALTH, BASE_HEALTH)
            .add(Attributes.MOVEMENT_SPEED, BASE_SPEED)
            .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
            .add(Attributes.FLYING_SPEED, BASE_FLYING_SPEED);
    }

    @Override
    public String getTextureName() {
        return "textures/entity/primitive/subsrp_nogla.png";
    }

    @Override
    public String getModelName() {
        return "nogla";
    }
    
    /**
     * Check if entity is infected (helper method for target selection)
     */
    private boolean isInfected(LivingEntity entity) {
        return this.infectionComponent != null && this.infectionComponent.isInfected(entity);
    }
}
