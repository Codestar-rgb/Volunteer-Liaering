package com.subspaceparasite.common.entity.monster.derived;

import com.subspaceparasite.api.IHitboxedEntity;
import com.subspaceparasite.common.entity.base.EntityDerivedBase;
import com.subspaceparasite.common.entity.monster.misc.EntityVoidOrb;
import com.subspaceparasite.config.ModConfigSystems;
import com.subspaceparasite.core.ModEntities;
import com.subspaceparasite.core.ModSounds;
import com.subspaceparasite.core.ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

/**
 * EntityKirin - Derived Stage Gravity/Void Parasite
 * <p>
 * Kirin is a specialized derived parasite with the following characteristics:
 * - Blink/teleportation ability for tactical repositioning
 * - Void orb summoning skill for area control
 * - Floating/hovering movement pattern
 * - High durability and gravitational manipulation
 * - Summoning void orbs that create gravity wells
 * <p>
 * Original SRP Features Implemented:
 * - Blink teleportation with charge-up warning
 * - Void orb summoning (EntityOrbVoid)
 * - Floating movement with bobbing animation
 * - Ground scanning for hover height adjustment
 * - Skill cooldown management
 */
public class EntityKirin extends EntityDerivedBase implements IHitboxedEntity {
    
    // ========== SynchedEntityData Accessors ==========
    private static final EntityDataAccessor<BlockPos> DATA_BLINK_POS =
            SynchedEntityData.defineId(EntityKirin.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Integer> DATA_BLINK_TICKS =
            SynchedEntityData.defineId(EntityKirin.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_SUMMONING =
            SynchedEntityData.defineId(EntityKirin.class, EntityDataSerializers.BOOLEAN);
    
    // ========== Combat State ==========
    private int voidOrbCooldown = 0;
    private int voidOrbCooldownTotal = 5;
    private int floatBobTimer = 0;
    private int noGroundTicks = 0;
    private boolean isSummoning = false;
    private int summonProgress = 0;
    
    // ========== Constants ==========
    private static final float BASE_HEALTH = 100.0f;
    private static final float ARMOR_VALUE = 14.0f;
    private static final float ATTACK_DAMAGE = 10.0f;
    private static final double MOVEMENT_SPEED = 0.28;
    private static final double FOLLOW_RANGE = 48.0;
    private static final double HOVER_HEIGHT = 0.35;
    private static final double BOB_AMPLITUDE = 0.06;
    private static final int FLOAT_GROUND_SCAN_INTERVAL = 24;
    private static final int BLINK_CHARGE_TIME = 60; // ticks
    private static final double BLINK_RANGE = 16.0;
    private static final int VOID_ORB_COUNT = 3;
    private static final double VOID_ORB_RADIUS = 8.0;
    
    // ========== Constructor ==========
    
    public EntityKirin(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.killCount = -10.0;
    }

    // ========== Native Biome Override ==========

    /**
     * Kirin are void-gravity derived parasites — their native biome
     * is any end-themed or void biome. Returns true if currently
     * in an End or void-tagged biome.
     */
    @Override
    protected boolean checkNativeBiome() {
        return this.level().getBiome(this.blockPosition()).is(
                net.minecraft.tags.BiomeTags.IS_END);
    }
    
    // ========== Data Registration ==========
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_BLINK_POS, BlockPos.ZERO);
        this.entityData.define(DATA_BLINK_TICKS, 0);
        this.entityData.define(DATA_SUMMONING, false);
    }
    
    // ========== NBT Persistence ==========
    
    @Override
    public void addAdditionalSaveData(@Nonnull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("VoidOrbCooldown", this.voidOrbCooldown);
        compound.putInt("VoidOrbCooldownTotal", this.voidOrbCooldownTotal);
        compound.putBoolean("IsSummoning", this.isSummoning);
        compound.putInt("SummonProgress", this.summonProgress);
    }
    
    @Override
    public void readAdditionalSaveData(@Nonnull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("VoidOrbCooldown")) {
            this.voidOrbCooldown = compound.getInt("VoidOrbCooldown");
        }
        if (compound.contains("VoidOrbCooldownTotal")) {
            this.voidOrbCooldownTotal = compound.getInt("VoidOrbCooldownTotal");
        }
        if (compound.contains("IsSummoning")) {
            this.isSummoning = compound.getBoolean("IsSummoning");
        }
        if (compound.contains("SummonProgress")) {
            this.summonProgress = compound.getInt("SummonProgress");
        }
    }
    
    // ========== Attribute Supplier ==========
    
    public static AttributeSupplier.Builder createAttributes() {
        return EntityDerivedBase.createAttributes()
                .add(Attributes.MAX_HEALTH, BASE_HEALTH)
                .add(Attributes.ARMOR, ARMOR_VALUE)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.FOLLOW_RANGE, FOLLOW_RANGE)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
                .add(Attributes.JUMP_STRENGTH, 0.5);
    }
    
    // ========== Spawn Rules ==========
    
    public static boolean checkKirinSpawnRules(EntityType<EntityKirin> entityType, ServerLevelAccessor level,
                                               MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return Monster.checkMonsterSpawnRules(entityType, level, spawnType, pos, random)
                && level.getDifficulty() != Difficulty.PEACEFUL;
    }
    
    // ========== Initialization ==========
    
    @Override
    protected void registerGoals() {
        super.registerGoals();
        
        // Basic goals
        this.goalSelector.addGoal(0, new FloatGoal(this));
        
        // Blink teleportation goal
        this.goalSelector.addGoal(1, new KirinBlinkGoal());
        
        // Melee attack
        this.goalSelector.addGoal(2, new KirinMeleeAttackGoal());
        
        // Void orb summoning skill
        this.goalSelector.addGoal(3, new KirinSummonGoal());
        
        // Random movement
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.8, 12));
        
        // Look at target
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 10.0f));
        
        // Target goals
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, false));
    }
    
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                               MobSpawnType reason, @Nullable SpawnGroupData spawnData,
                               @Nullable CompoundTag entityData) {
        this.voidOrbCooldown = this.voidOrbCooldownTotal;
        return super.finalizeSpawn(level, difficulty, reason, spawnData, entityData);
    }
    
    // ========== Tick Update ==========
    
    @Override
    public void tick() {
        super.tick();
        
        // Floating/hovering behavior
        handleFloating();
        
        // Blink charge handling (client-side particles)
        if (level().isClientSide) {
            handleBlinkParticles();
        }
        
        // Void orb cooldown
        if (voidOrbCooldown > 0) {
            voidOrbCooldown--;
        }
        
        // Summoning progress
        if (isSummoning) {
            handleSummoning();
        }
        
        // Ground scan for floating
        if (!level().isClientSide && tickCount % FLOAT_GROUND_SCAN_INTERVAL == 0) {
            scanGroundForFloating();
        }
    }
    
    // ========== Floating Behavior ==========
    
    private void handleFloating() {
        if (onGround()) {
            noGroundTicks = 0;
        } else {
            noGroundTicks++;
        }
        
        // Bobbing animation while floating
        floatBobTimer++;
        if (onGround() || noGroundTicks < 10) {
            // Normal movement
            setDeltaMovement(getDeltaMovement().multiply(1.0, 0.9, 1.0));
        } else {
            // Hovering with bobbing
            double targetHeight = getY() + HOVER_HEIGHT + Math.sin(floatBobTimer * 0.1) * BOB_AMPLITUDE;
            
            // Adjust vertical movement to maintain hover height
            if (getY() < targetHeight - 0.5) {
                setDeltaMovement(getDeltaMovement().add(0, 0.08, 0));
            } else if (getY() > targetHeight + 0.5) {
                setDeltaMovement(getDeltaMovement().add(0, -0.08, 0));
            }
        }
    }
    
    private void scanGroundForFloating() {
        BlockPos below = blockPosition().below();
        if (!level().isEmptyBlock(below)) {
            // Ground detected, can float
            noGroundTicks = 0;
        }
    }
    
    // ========== Blink Teleportation ==========
    
    public void setBlinkCharge(BlockPos pos, int ticks) {
        if (pos == null) {
            pos = BlockPos.ZERO;
        }
        this.entityData.set(DATA_BLINK_POS, pos);
        this.entityData.set(DATA_BLINK_TICKS, Math.max(0, ticks));
    }
    
    public void clearBlinkCharge() {
        this.entityData.set(DATA_BLINK_POS, BlockPos.ZERO);
        this.entityData.set(DATA_BLINK_TICKS, 0);
    }
    
    private void handleBlinkParticles() {
        int ticks = this.entityData.get(DATA_BLINK_TICKS);
        BlockPos pos = this.entityData.get(DATA_BLINK_POS);
        
        if (ticks > 0 && !pos.equals(BlockPos.ZERO)) {
            float t = 1.0f - (float) ticks / (float) BLINK_CHARGE_TIME;
            double y = (double) pos.getY() + 1.5 + (double) t;
            double cx = (double) pos.getX() + 0.5;
            double cz = (double) pos.getZ() + 0.5;
            
            // Spawn warning particles (spiral pattern)
            float sizeBig = 6.0f;
            float sizeSmall = 5.5f;
            double baseSpeed = 0.35;
            double accel = 1.25;
            double speed = baseSpeed + (double) t * accel;
            
            float angSmall = (float) ((double) tickCount * speed % (Math.PI * 2));
            float angBig = (float) ((double) tickCount * -speed % (Math.PI * 2));
            
            // Small spiral
            for (int i = 0; i < 8; i++) {
                float angle = angSmall + (i * (float) (Math.PI * 2 / 8));
                double px = cx + Math.cos(angle) * sizeSmall;
                double pz = cz + Math.sin(angle) * sizeSmall;
                level().addParticle(ParticleTypes.PORTAL, px, y, pz, 0, 0.02, 0);
            }
            
            // Big spiral
            for (int i = 0; i < 8; i++) {
                float angle = angBig + (i * (float) (Math.PI * 2 / 8));
                double px = cx + Math.cos(angle) * sizeBig;
                double pz = cz + Math.sin(angle) * sizeBig;
                level().addParticle(ParticleTypes.PORTAL, px, y, pz, 0, 0.02, 0);
            }
        }
    }
    
    private boolean performBlink(BlockPos targetPos) {
        if (level().isClientSide) {
            return true;
        }
        
        // Spawn departure particles
        ((ServerLevel) level()).sendParticles(ParticleTypes.PORTAL,
                getX(), getY() + getEyeHeight(), getZ(),
                20, getBbWidth() / 2, getBbHeight() / 2, getBbWidth() / 2, 0.02);
        
        // Teleport
        boolean success = this.randomTeleport(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, true);
        
        if (success) {
            // Spawn arrival particles
            ((ServerLevel) level()).sendParticles(ParticleTypes.PORTAL,
                    getX(), getY() + getEyeHeight(), getZ(),
                    20, getBbWidth() / 2, getBbHeight() / 2, getBbWidth() / 2, 0.02);
            
            playSound(ModSounds.HEBLU_IDLE.get(), 2.0f, 1.5f);
        }
        
        return success;
    }
    
    // ========== Void Orb Summoning ==========
    
    private void handleSummoning() {
        summonProgress++;
        
        if (summonProgress >= 100) {
            // Complete summoning
            completeSummoning();
            isSummoning = false;
            summonProgress = 0;
        } else if (summonProgress % 10 == 0 && level().isClientSide) {
            // Spawn summoning particles
            for (int i = 0; i < 5; i++) {
                double offsetX = (random.nextDouble() - 0.5) * getBbWidth();
                double offsetY = random.nextDouble() * getBbHeight();
                double offsetZ = (random.nextDouble() - 0.5) * getBbWidth();
                level().addParticle(ModParticleTypes.VOID_ORB.get(),
                        getX() + offsetX, getY() + offsetY, getZ() + offsetZ,
                        0, 0.05, 0);
            }
        }
    }
    
    private void completeSummoning() {
        if (level().isClientSide) {
            return;
        }
        
        // Spawn void orbs in a circle around Kirin
        for (int i = 0; i < VOID_ORB_COUNT; i++) {
            float angle = (float) (i * 2 * Math.PI / VOID_ORB_COUNT);
            double orbX = getX() + Math.cos(angle) * VOID_ORB_RADIUS;
            double orbZ = getZ() + Math.sin(angle) * VOID_ORB_RADIUS;
            double orbY = getY() + 2.0;
            
            EntityVoidOrb orb = ModEntities.VOID_ORB.get().create(level());
            if (orb != null) {
                orb.setPos(orbX, orbY, orbZ);
                level().addFreshEntity(orb);
            }
        }
        
        // Reset cooldown
        voidOrbCooldown = voidOrbCooldownTotal;
        
        playSound(ModSounds.PROJECTILE_SHOOT.get(), 3.0f, 0.6f);
    }
    
    private void startSummoning() {
        if (voidOrbCooldown <= 0 && !isSummoning) {
            isSummoning = true;
            summonProgress = 0;
            entityData.set(DATA_SUMMONING, true);
        }
    }
    
    // ========== Sound Events ==========
    
    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        return ModSounds.KIRIN_IDLE.get();
    }
    
    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.KIRIN_HURT.get();
    }
    
    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return ModSounds.KIRIN_DEATH.get();
    }
    
    // ========== Getters and Setters ==========
    
    public boolean isSummoning() {
        return this.entityData.get(DATA_SUMMONING);
    }
    
    public void setSummoning(boolean summoning) {
        this.entityData.set(DATA_SUMMONING, summoning);
        this.isSummoning = summoning;
    }
    
    public BlockPos getBlinkTargetPos() {
        return this.entityData.get(DATA_BLINK_POS);
    }
    
    public int getBlinkTicksRemaining() {
        return this.entityData.get(DATA_BLINK_TICKS);
    }
    
    public int getVoidOrbCooldown() {
        return voidOrbCooldown;
    }
    
    // ========== Inner Classes ==========
    
    /**
     * Blink Teleportation Goal - Tactical repositioning ability
     */
    private class KirinBlinkGoal extends net.minecraft.world.entity.ai.goal.Goal {
        private final EntityKirin kirin;
        private BlockPos blinkTarget;
        private int chargeTime;
        private int cooldown;
        
        public KirinBlinkGoal() {
            this.kirin = EntityKirin.this;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
            this.cooldown = 0;
        }
        
        @Override
        public boolean canUse() {
            if (cooldown > 0) {
                cooldown--;
                return false;
            }
            
            LivingEntity target = kirin.getTarget();
            if (target == null) return false;
            
            // Check if should blink (too close or too far)
            double distance = kirin.distanceToSqr(target);
            return distance < 9.0 || distance > 144.0; // 3^2 or 12^2
        }
        
        @Override
        public void start() {
            LivingEntity target = kirin.getTarget();
            if (target == null) return;
            
            // Calculate blink position (behind or away from target)
            double distance = kirin.distanceToSqr(target);
            Vec3 direction;
            
            if (distance < 9.0) {
                // Blink away from target
                direction = kirin.position().vectorTo(target.position()).normalize().scale(-1);
            } else {
                // Blink closer to target
                direction = target.position().vectorTo(kirin.position()).normalize().scale(-1);
            }
            
            // Find valid blink position
            blinkTarget = kirin.blockPosition().offset(
                    (int) (direction.x * BLINK_RANGE),
                    0,
                    (int) (direction.z * BLINK_RANGE)
            );
            
            // Ensure position is valid
            while (!kirin.level().isEmptyBlock(blinkTarget) && blinkTarget.getY() < 256) {
                blinkTarget = blinkTarget.above();
            }
            
            // Start charge-up
            chargeTime = BLINK_CHARGE_TIME;
            kirin.setBlinkCharge(blinkTarget, chargeTime);
        }
        
        @Override
        public void tick() {
            if (blinkTarget == null) {
                stop();
                return;
            }
            
            // Face blink target
            kirin.lookAt(net.minecraft.commands.arguments.EntityAnchorArgument.Anchor.EYES, new Vec3(blinkTarget.getX() + 0.5, blinkTarget.getY(), blinkTarget.getZ() + 0.5));
            
            // Decrease charge time
            chargeTime--;
            kirin.setBlinkCharge(blinkTarget, chargeTime);
            
            if (chargeTime <= 0) {
                // Perform blink
                kirin.performBlink(blinkTarget);
                kirin.clearBlinkCharge();
                cooldown = 100; // 5 second cooldown
                stop();
            }
        }
        
        @Override
        public void stop() {
            kirin.clearBlinkCharge();
            blinkTarget = null;
        }
    }
    
    /**
     * Melee Attack Goal - Standard close-range combat
     */
    private class KirinMeleeAttackGoal extends MeleeAttackGoal {
        public KirinMeleeAttackGoal() {
            super(EntityKirin.this, 1.0, true);
        }
        
        @Override
        protected void checkAndPerformAttack(LivingEntity target, double distSq) {
            double attackRange = 4.0;
            
            if (distSq <= attackRange * attackRange) {
                this.resetAttackCooldown();
                this.mob.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
                mob.doHurtTarget(target);
                
                // Apply slight knockback
                target.setDeltaMovement(target.getDeltaMovement().add(0, 0.2, 0));
            }
        }
    }
    
    /**
     * Void Orb Summoning Goal - Area control skill
     */
    private class KirinSummonGoal extends net.minecraft.world.entity.ai.goal.Goal {
        private final EntityKirin kirin;
        private int checkInterval;
        
        public KirinSummonGoal() {
            this.kirin = EntityKirin.this;
            this.setFlags(EnumSet.of(Flag.MOVE));
            this.checkInterval = 0;
        }
        
        @Override
        public boolean canUse() {
            if (kirin.isSummoning()) {
                return false;
            }
            
            if (kirin.voidOrbCooldown > 0) {
                return false;
            }
            
            LivingEntity target = kirin.getTarget();
            if (target == null) return false;
            
            // Check every 60 ticks (3 seconds)
            checkInterval++;
            if (checkInterval < 60) {
                return false;
            }
            checkInterval = 0;
            
            // Use summoning when multiple enemies are nearby
            List<LivingEntity> nearbyEnemies = kirin.level().getNearbyEntities(
                    LivingEntity.class,
                    net.minecraft.world.entity.ai.targeting.TargetingConditions.DEFAULT,
                    kirin,
                    kirin.getBoundingBox().inflate(VOID_ORB_RADIUS)
            );
            
            return nearbyEnemies.size() >= 2;
        }
        
        @Override
        public void start() {
            kirin.startSummoning();
        }
        
        @Override
        public boolean canContinueToUse() {
            return kirin.isSummoning();
        }
        
        @Override
        public void tick() {
            // Keep moving slowly while summoning
            LivingEntity target = kirin.getTarget();
            if (target != null) {
                kirin.lookAt(target, 30.0f, 30.0f);
            }
        }
        
        @Override
        public void stop() {
            kirin.setSummoning(false);
        }
    }
}
