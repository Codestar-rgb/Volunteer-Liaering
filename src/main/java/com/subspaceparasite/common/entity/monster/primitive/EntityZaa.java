package com.subspaceparasite.common.entity.monster.primitive;

import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityPrimitiveBase;
import com.subspaceparasite.core.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

/**
 * EntityZaa - Void Walker Primitive stage parasite.
 * <p>
 * Original SRP 1.12.2: EntityZaa in primitive stage.
 * Modernized for Forge 1.20.1 with component-based architecture.
 * <p>
 * Stats (configurable):
 * - Health: 20.0
 * - Attack Damage: 6.5
 * - Speed: 0.31
 * <p>
 * Behavior:
 * - Short-range teleportation ability
 * - Phase movement through obstacles
 * - Teleports when taking damage or when target is out of reach
 * - Spreads infection on death
 */
public class EntityZaa extends EntityPrimitiveBase {

    private static final double BASE_HEALTH = 20.0;
    private static final double BASE_ATTACK_DAMAGE = 6.5;
    private static final double BASE_SPEED = 0.31;
    
    // Teleportation cooldown and state
    private int teleportCooldown = 0;
    private static final int TELEPORT_COOLDOWN = 60; // 3 seconds
    private static final int TELEPORT_RANGE = 12;
    private static final float TELEPORT_CHANCE = 0.3F;
    
    public EntityZaa(EntityType<? extends Monster> type, Level world) {
        super(type, world);
        this.xpReward = 10;
        this.setCanPickUpLoot(false);
        this.phaseCreated = EvoPhase.PHASE_1;
        this.parasiteType = ParasiteType.PRI_ZAA;
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
        return ModSounds.SUBSRP_ENTITY_ZAA_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.SUBSRP_ENTITY_ZAA_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SUBSRP_ENTITY_ZAA_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        // No footstep sounds for void walker
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, net.minecraft.world.entity.EntityDimensions sizeIn) {
        return 1.5F;
    }

    @Override
    public void tick() {
        super.tick();
        
        if (!this.level().isClientSide()) {
            // Teleport cooldown
            if (teleportCooldown > 0) {
                teleportCooldown--;
            }
            
            // Random teleport when idle or when target is far
            if (this.getTarget() != null && teleportCooldown <= 0) {
                double distToTarget = this.distanceToSqr(this.getTarget());
                if (distToTarget > 144.0 && this.random.nextFloat() < TELEPORT_CHANCE) {
                    this.tryTeleportTowardsTarget();
                }
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide() && teleportCooldown <= 0 && this.random.nextFloat() < TELEPORT_CHANCE) {
            // Chance to teleport when hit
            if (this.tryRandomTeleport()) {
                teleportCooldown = TELEPORT_COOLDOWN;
            }
        }
        return super.hurt(source, amount);
    }

    /**
     * Try to teleport towards current target
     */
    private boolean tryTeleportTowardsTarget() {
        if (this.getTarget() == null) return false;
        
        LivingEntity target = this.getTarget();
        double targetX = target.getX();
        double targetY = target.getY();
        double targetZ = target.getZ();
        
        // Calculate direction to target
        double dx = targetX - this.getX();
        double dy = targetY - this.getY();
        double dz = targetZ - this.getZ();
        
        // Normalize and scale to teleport range
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dist < 0.1) return false;
        
        double teleportDist = Math.min(TELEPORT_RANGE, dist - 2.0);
        double newX = this.getX() + (dx / dist) * teleportDist;
        double newY = this.getY() + (dy / dist) * teleportDist;
        double newZ = this.getZ() + (dz / dist) * teleportDist;
        
        return this.attemptTeleport(newX, newY, newZ);
    }

    /**
     * Try to teleport to a random nearby position
     */
    private boolean tryRandomTeleport() {
        double newX = this.getX() + (this.random.nextDouble() - 0.5) * TELEPORT_RANGE * 2;
        double newY = this.getY() + (this.random.nextDouble() - 0.5) * TELEPORT_RANGE;
        double newZ = this.getZ() + (this.random.nextDouble() - 0.5) * TELEPORT_RANGE * 2;
        
        return this.attemptTeleport(newX, newY, newZ);
    }

    /**
     * Attempt teleportation with validation and effects
     */
    private boolean attemptTeleport(double x, double y, double z) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(x, y, z);
        
        // Find valid ground position
        while (mutablePos.getY() > this.level().getMinBuildHeight() && 
               !this.level().getBlockState(mutablePos).canOcclude()) {
            mutablePos.move(0, -1, 0);
        }
        
        if (mutablePos.getY() <= this.level().getMinBuildHeight()) {
            return false;
        }
        
        // Check if position is safe
        if (!this.level().isEmptyBlock(mutablePos) || 
            !this.level().isEmptyBlock(mutablePos.above())) {
            return false;
        }
        
        // Spawn portal particles before teleport
        if (!this.level().isClientSide()) {
            ((ServerLevel)this.level()).sendParticles(ParticleTypes.PORTAL, 
                this.getX(), this.getY(), this.getZ(), 
                20, 0.5, 0.5, 0.5, 0.1);
        }
        
        // Perform teleport
        this.moveTo(mutablePos.getX() + 0.5, mutablePos.getY(), mutablePos.getZ() + 0.5, 
                    this.getYRot(), this.getXRot());
        this.setDeltaMovement(0, 0, 0);
        
        // Spawn portal particles after teleport
        if (!this.level().isClientSide()) {
            ((ServerLevel)this.level()).sendParticles(ParticleTypes.PORTAL, 
                this.getX(), this.getY(), this.getZ(), 
                20, 0.5, 0.5, 0.5, 0.1);
        }
        
        return true;
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide()) {
            this.spreadInfectionOnDeath(5.0, 2);
            // Spawn extra particles on death
            ((ServerLevel)this.level()).sendParticles(ParticleTypes.PORTAL, 
                this.getX(), this.getY(), this.getZ(), 
                30, 1.0, 1.0, 1.0, 0.2);
        }
        super.die(source);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        // updateSwingTime() removed in MC 1.20.1; swing animation handled by LivingEntity.tick()
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityPrimitiveBase.createAttributes()
            .add(Attributes.FOLLOW_RANGE, 24.0)
            .add(Attributes.MAX_HEALTH, BASE_HEALTH)
            .add(Attributes.MOVEMENT_SPEED, BASE_SPEED)
            .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.2);
    }

    public String getTextureName() {
        return "textures/entity/primitive/subsrp_zaa.png";
    }

    public String getModelName() {
        return "zaa";
    }
    
    /**
     * Check if entity is infected (helper method for target selection)
     */
    private boolean isInfected(LivingEntity entity) {
        return this.infectionComponent != null && this.infectionComponent.isInfected(entity);
    }
}
