package com.subspaceparasite.common.entity.monster.primitive;

import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.core.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

/**
 * EntityShyco - Shy Camouflager Primitive stage parasite.
 * <p>
 * Original SRP 1.12.2: EntityShyco in primitive stage.
 * Modernized for Forge 1.20.1 with component-based architecture.
 * <p>
 * Stats (configurable):
 * - Health: 24.0
 * - Attack Damage: 7.0
 * - Speed: 0.22 (slow but stealthy)
 * <p>
 * Behavior:
 * - Camouflages as blocks when idle or stationary
 * - Becomes invisible and gains damage resistance while camouflaged
 * - Ambush predator that attacks when targets approach
 * - Breaks camouflage when moving or attacking
 * - Spreads infection on death
 */
public class EntityShyco extends EntityParasiteBase {

    private static final double BASE_HEALTH = 24.0;
    private static final double BASE_ATTACK_DAMAGE = 7.0;
    private static final double BASE_SPEED = 0.22;
    
    // Camouflage state
    private int camouflageTimer = 0;
    private boolean isCamouflaged = false;
    private static final int CAMOUFLAGE_DELAY = 100; // 5 seconds to fully camouflage
    private static final int CAMOUFLAGE_BREAK_TIME = 20; // Time before can re-camouflage after breaking
    
    public EntityShyco(EntityType<? extends EntityShyco> type, Level world) {
        super(type, world);
        this.xpReward = 10;
        this.setCanPickUpLoot(false);
        this.phaseCreated = EvoPhase.ONE;
        this.parasiteType = ParasiteType.PRI_SHYCO;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2, true));
        
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
            living -> !this.isInfected(living)));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SUBSRP_ENTITY_SHYCO_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.SUBSRP_ENTITY_SHYCO_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SUBSRP_ENTITY_SHYCO_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        // No footstep sounds when camouflaged
        if (!this.isCamouflaged) {
            super.playStepSound(pos, blockIn);
        }
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, net.minecraft.world.entity.EntityDimensions sizeIn) {
        return 1.5F;
    }

    @Override
    public void tick() {
        super.tick();
        
        if (!this.level().isClientSide()) {
            // Handle camouflage logic
            if (this.getTarget() == null && this.getDeltaMovement().lengthSqr() < 0.01 && !this.isCamouflaged) {
                camouflageTimer++;
                if (camouflageTimer >= CAMOUFLAGE_DELAY) {
                    this.isCamouflaged = true;
                    this.setInvisible(true);
                    // Spawn camouflage particles
                    ((Level)this.level()).sendParticles(ParticleTypes.SMOKE,
                        this.getX(), this.getY() + 0.5, this.getZ(),
                        10, 0.3, 0.3, 0.3, 0.02);
                }
            } else if (this.getTarget() != null || this.getDeltaMovement().lengthSqr() > 0.01) {
                if (this.isCamouflaged) {
                    this.isCamouflaged = false;
                    this.setInvisible(false);
                    camouflageTimer = -CAMOUFLAGE_BREAK_TIME; // Cooldown before can re-camouflage
                    // Spawn decamouflage particles
                    ((Level)this.level()).sendParticles(ParticleTypes.SMOKE,
                        this.getX(), this.getY() + 0.5, this.getZ(),
                        15, 0.5, 0.5, 0.5, 0.05);
                } else {
                    camouflageTimer = Math.max(-CAMOUFLAGE_BREAK_TIME, camouflageTimer - 1);
                }
            }
            
            // Damage resistance while camouflaged
            if (this.isCamouflaged) {
                this.setAttributeValue(Attributes.ARMOR, 10.0);
            } else {
                this.setAttributeValue(Attributes.ARMOR, 2.0);
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source) {
        // Break camouflage when hurt
        if (this.isCamouflaged && !this.level().isClientSide()) {
            this.isCamouflaged = false;
            this.setInvisible(false);
            camouflageTimer = -CAMOUFLAGE_BREAK_TIME;
            // Spawn decamouflage particles
            ((Level)this.level()).sendParticles(ParticleTypes.SMOKE,
                this.getX(), this.getY() + 0.5, this.getZ(),
                20, 0.5, 0.5, 0.5, 0.1);
        }
        return super.hurt(source);
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide()) {
            this.spreadInfectionOnDeath(5.0, 2);
            // Spawn extra particles on death
            ((Level)this.level()).sendParticles(ParticleTypes.SMOKE,
                this.getX(), this.getY(), this.getZ(),
                20, 0.5, 0.5, 0.5, 0.1);
        }
        super.die(source);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.updateSwingTime();
    }

    /**
     * Check if this entity is currently camouflaged
     */
    public boolean isCamouflaged() {
        return this.isCamouflaged;
    }

    /**
     * Get camouflage progress (0-100), negative means cooldown
     */
    public int getCamouflageProgress() {
        if (camouflageTimer < 0) {
            return -1; // In cooldown
        }
        return (int)((float)camouflageTimer / CAMOUFLAGE_DELAY * 100.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMonsterAttributes()
            .add(Attributes.FOLLOW_RANGE, 20.0)
            .add(Attributes.MAX_HEALTH, BASE_HEALTH)
            .add(Attributes.MOVEMENT_SPEED, BASE_SPEED)
            .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.4)
            .add(Attributes.ARMOR, 2.0);
    }

    @Override
    public String getTextureName() {
        return "textures/entity/primitive/subsrp_shyco.png";
    }

    @Override
    public String getModelName() {
        return "shyco";
    }
    
    /**
     * Check if entity is infected (helper method for target selection)
     */
    private boolean isInfected(LivingEntity entity) {
        return this.infectionComponent != null && this.infectionComponent.isInfected(entity);
    }
}
