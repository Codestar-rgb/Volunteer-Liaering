package com.subspaceparasite.common.entity.monster.deterrent;

import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityDeterrentBase;
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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * EntityVenkrolSIV - Deterrent Stage SIV (Special Infectious Variant) Parasite
 * <p>
 * Venkrol SIV is a specialized deterrent parasite with the following characteristics:
 * - High mobility and agility
 * - Infectious attacks that spread infection rapidly
 * - Enhanced sensory capabilities for tracking targets
 * - Can coordinate with other parasites
 * - Specialized combat behavior focused on infection spread
 * <p>
 * Stats (configurable):
 * - Health: 65.0
 * - Attack Damage: 9.0
 * - Speed: 0.38
 * - Armor: 8.0
 * <p>
 * Behavior:
 * - Aggressive pursuit of uninfected targets
 * - Applies infection on successful attacks
 * - Coordinates with nearby parasites
 * - Enhanced detection range for infected entities
 */
public class EntityVenkrolSIV extends EntityDeterrentBase {

    private static final double BASE_HEALTH = 65.0;
    private static final double BASE_ATTACK_DAMAGE = 9.0;
    private static final double BASE_SPEED = 0.38;
    private static final double BASE_ARMOR = 8.0;
    private static final double FOLLOW_RANGE = 48.0;
    
    // Infection spread parameters
    private static final float INFECTION_CHANCE_ON_HIT = 0.7F;
    private static final int INFECTION_LEVEL_ON_HIT = 2;
    private static final int INFECTION_DURATION = 400; // 20 seconds
    
    // Detection enhancement
    private static final double ENHANCED_DETECTION_RANGE = 64.0;

    public EntityVenkrolSIV(EntityType<? extends Monster> type, Level world) {
        super(type, world);
        this.xpReward = 25;
        this.setCanPickUpLoot(false);
        this.phaseCreated = EvoPhase.PHASE_3; // Phase 3 — deterrent tier
        this.parasiteType = ParasiteType.VENKROL_SIV;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        
        // Basic movement
        this.goalSelector.addGoal(0, new FloatGoal(this));
        
        // Aggressive melee combat
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.4, true));
        
        // Target selection - prioritize uninfected
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 
            10, true, false, living -> !isInfected(living)));
    }

    @Override
    public void tick() {
        super.tick();
        
        if (!this.level().isClientSide()) {
            // Enhanced detection for infected entities
            if (this.srpTicks % 20 == 0) {
                scanForInfectedEntities();
            }
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean result = super.doHurtTarget(target);
        
        if (result && target instanceof LivingEntity livingTarget) {
            // Apply infection on hit
            if (this.random.nextFloat() < INFECTION_CHANCE_ON_HIT) {
                applyInfection(livingTarget);
            }
            
            // Spawn infection particles
            if (!this.level().isClientSide()) {
                spawnInfectionParticles(livingTarget);
            }
        }
        
        return result;
    }

    /**
     * Apply infection to target entity
     */
    private void applyInfection(LivingEntity target) {
        if (this.infectionComponent != null) {
            this.infectionComponent.spreadCOTH(target);
        }
    }

    /**
     * Spawn infection particles around target
     */
    private void spawnInfectionParticles(LivingEntity target) {
        if (this.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 8; i++) {
                double offsetX = (this.random.nextDouble() - 0.5) * target.getBbWidth();
                double offsetY = this.random.nextDouble() * target.getBbHeight();
                double offsetZ = (this.random.nextDouble() - 0.5) * target.getBbWidth();
                
                serverLevel.sendParticles(ParticleTypes.SCULK_SOUL,
                    target.getX() + offsetX,
                    target.getY() + offsetY,
                    target.getZ() + offsetZ,
                    1, 0.0, 0.0, 0.0, 0.1);
            }
        }
    }

    /**
     * Scan for nearby infected entities to coordinate with
     */
    private void scanForInfectedEntities() {
        if (this.level() instanceof Level level) {
            List<EntityParasiteBase> nearbyParasites = level.getEntitiesOfClass(
                EntityParasiteBase.class,
                this.getBoundingBox().inflate(ENHANCED_DETECTION_RANGE),
                entity -> entity != this && entity.isAlliedTo(this)
            );
            
            // Coordinate with nearby parasites - share target information
            if (!nearbyParasites.isEmpty() && this.getTarget() != null) {
                for (EntityParasiteBase parasite : nearbyParasites) {
                    if (parasite.getTarget() == null && parasite.hasLineOfSight(this.getTarget())) {
                        parasite.setTarget(this.getTarget());
                    }
                }
            }
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.VENKROL_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.VENKROL_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.VENKROL_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        // Minimal footstep sounds for stealth
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, net.minecraft.world.entity.EntityDimensions sizeIn) {
        return 1.8F;
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide()) {
            // Spread infection on death
            this.spreadInfectionOnDeath(8.0, 3);
            
            // Spawn extra infection particles
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SCULK_SOUL,
                    this.getX(), this.getY(), this.getZ(),
                    40, 1.0, 1.0, 1.0, 0.2);
            }
        }
        super.die(source);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.updateSwingTime();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityDeterrentBase.createAttributes()
            .add(Attributes.FOLLOW_RANGE, FOLLOW_RANGE)
            .add(Attributes.MAX_HEALTH, BASE_HEALTH)
            .add(Attributes.MOVEMENT_SPEED, BASE_SPEED)
            .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
            .add(Attributes.ARMOR, BASE_ARMOR)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.4);
    }

    public String getTextureName() {
        return "textures/entity/deterrent/subsrp_venkrol_siv.png";
    }

    public String getModelName() {
        return "venkrolSIV";
    }
    
    /**
     * Check if entity is infected (helper method for target selection)
     */
    private boolean isInfected(LivingEntity entity) {
        return this.infectionComponent != null && this.infectionComponent.isInfected(entity);
    }

    /**
     * Spawn rule check for Venkrol SIV
     */
    public static boolean checkVenkrolSIVSpawnRules(EntityType<EntityVenkrolSIV> type,
                                                     ServerLevelAccessor level, MobSpawnType spawnType,
                                                     BlockPos pos, net.minecraft.util.RandomSource random) {
        return Monster.checkMonsterSpawnRules(type, level, spawnType, pos, random)
                && level.getDifficulty() != net.minecraft.world.Difficulty.PEACEFUL;
    }
}
