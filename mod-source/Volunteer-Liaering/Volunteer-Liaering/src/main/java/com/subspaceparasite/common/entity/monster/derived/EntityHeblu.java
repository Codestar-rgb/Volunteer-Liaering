package com.subspaceparasite.common.entity.monster.derived;

import com.subspaceparasite.api.IHitboxedEntity;
import com.subspaceparasite.common.entity.base.EntityDerivedBase;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.common.entity.projectile.EntityOrbBase;
import com.subspaceparasite.core.ModEntities;
import com.subspaceparasite.core.ModSounds;
import com.subspaceparasite.core.ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

/**
 * EntityHeblu - Derived Stage Flying Parasite
 * <p>
 * Heblu is a high-tier derived parasite with the following characteristics:
 * - Flight capability with advanced aerial movement
 * - Multiple body parts (head, left tendril, right tendril) that can be targeted
 * - Ranged attack with toxic projectiles and area denial
 * - "Raining" orb bombardment skill
 * - Vomit attack that creates toxic clouds
 * - High mobility and tactical positioning
 * <p>
 * Original SRP Features Implemented:
 * - Body part system with independent health pools
 * - Flying state management with visual/audio feedback
 * - Ranged attack with projectile spawning
 * - Area bombardment skill (raining orbs)
 * - Toxic vomit attack with particle effects
 * - Advanced AI for aerial combat
 */
public class EntityHeblu extends EntityDerivedBase implements RangedAttackMob, IHitboxedEntity {
    
    // ========== SynchedEntityData Accessors ==========

    private static final EntityDataAccessor<Boolean> DATA_FLYING =
            SynchedEntityData.defineId(EntityHeblu.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_ATTACKING =
            SynchedEntityData.defineId(EntityHeblu.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_VOMIT_TIMER =
            SynchedEntityData.defineId(EntityHeblu.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_RAINING_ORBS =
            SynchedEntityData.defineId(EntityHeblu.class, EntityDataSerializers.INT);
    
    // ========== Body Part Health ==========

    private float leftTendrilHealth;
    private float rightTendrilHealth;
    private float headHealth;
    
    // ========== Body Parts (Client-side rendering support) ==========

    @Nullable
    private EntityBodyPart leftTendril;
    @Nullable
    private EntityBodyPart rightTendril;
    @Nullable
    private EntityBodyPart head;
    
    // ========== Combat State ==========

    private int flyingTimer = 0;
    private float wingAnimationTimer = 0.0f;
    private float wingAnimationSpeed = 0.0f;
    private BlockPos vomitTargetPos = null;
    private int vomitTimer = 0;
    private boolean isRaining = false;
    private int rainingOrbsRemaining = 0;
    private int orbSpawnCooldown = 0;
    
    // ========== Constants ==========

    private static final float BASE_HEALTH = 80.0f;
    private static final float ARMOR_VALUE = 12.0f;
    private static final float ATTACK_DAMAGE = 8.0f;
    private static final double MOVEMENT_SPEED = 0.35;
    private static final double FOLLOW_RANGE = 64.0;
    private static final float TENDRIL_HEALTH_MULTIPLIER = 0.4f;
    private static final int MAX_RAINING_ORBS = 20;
    private static final double RAINING_RADIUS = 12.0;
    
    // ========== Constructor ==========

    
    public EntityHeblu(EntityType<? extends Monster> type, Level level) {
        super(type, level);

        this.killCount = -10.0;
    }

    // ========== Native Biome Override ==========

    /**
     * Heblu are flying derived parasites — their native biome
     * is mountain/high-altitude biomes where flight is advantageous.
     * Returns true if currently in a mountain-tagged biome.
     */
    @Override
    protected boolean checkNativeBiome() {
        return this.level().getBiome(this.blockPosition()).is(
                net.minecraft.tags.BiomeTags.IS_MOUNTAIN);
    }

    
    // ========== Data Registration ==========
    
    @Override
    protected void defineSynchedData() {

        super.defineSynchedData();

        this.entityData.define(DATA_FLYING, false);

        this.entityData.define(DATA_ATTACKING, false);

        this.entityData.define(DATA_VOMIT_TIMER, 0);

        this.entityData.define(DATA_RAINING_ORBS, 0);

    }

    
    // ========== NBT Persistence ==========
    
    @Override
    public void addAdditionalSaveData(@Nonnull CompoundTag compound) {

        super.addAdditionalSaveData(compound);

        compound.putFloat("LeftTendrilHealth", this.leftTendrilHealth);

        compound.putFloat("RightTendrilHealth", this.rightTendrilHealth);

        compound.putFloat("HeadHealth", this.headHealth);

        compound.putInt("VomitTimer", this.vomitTimer);

        compound.putBoolean("IsRaining", this.isRaining);

        compound.putInt("RainingOrbsRemaining", this.rainingOrbsRemaining);

        compound.putBoolean("Flying", this.isFlying());

    }

    
    @Override
    public void readAdditionalSaveData(@Nonnull CompoundTag compound) {

        super.readAdditionalSaveData(compound);

        if (compound.contains("LeftTendrilHealth")) {

            this.leftTendrilHealth = compound.getFloat("LeftTendrilHealth");

        }

        if (compound.contains("RightTendrilHealth")) {

            this.rightTendrilHealth = compound.getFloat("RightTendrilHealth");

        }

        if (compound.contains("HeadHealth")) {

            this.headHealth = compound.getFloat("HeadHealth");

        }

        if (compound.contains("VomitTimer")) {

            this.vomitTimer = compound.getInt("VomitTimer");

        }

        if (compound.contains("IsRaining")) {

            this.isRaining = compound.getBoolean("IsRaining");

        }

        if (compound.contains("RainingOrbsRemaining")) {

            this.rainingOrbsRemaining = compound.getInt("RainingOrbsRemaining");

        }

        if (compound.contains("Flying")) {

            setFlying(compound.getBoolean("Flying"));

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
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6)
                .add(Attributes.FLYING_SPEED, 0.4);
    }

    
    // ========== Spawn Rules ==========
    
    public static boolean checkHebluSpawnRules(EntityType<EntityHeblu> entityType, ServerLevelAccessor level,
                                               MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return Monster.checkMonsterSpawnRules(entityType, level, spawnType, pos, random)
                && level.getDifficulty() != Difficulty.PEACEFUL
                && EntityParasiteBase.checkSpawnRules(entityType, level, spawnType, pos, random);
    }

    
    // ========== Initialization ==========
    
    @Override
    protected void registerGoals() {
        super.registerGoals();
        
        // Basic goals
        this.goalSelector.addGoal(0, new FloatGoal(this));
        
        // Flight attack goal
        this.goalSelector.addGoal(1, new HebluFlightAttackGoal());
        
        // Melee attack with AOE
        this.goalSelector.addGoal(2, new HebluMeleeAttackGoal());
        
        // Ranged attack
        this.goalSelector.addGoal(3, new HebluRangedAttackGoal());
        
        // Random movement
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1.0, 15));
        
        // Look at target
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0f));
        
        // Target goals
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, false));
    }

    
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                               MobSpawnType reason, @Nullable SpawnGroupData spawnData,
                               @Nullable CompoundTag entityData) {
        super.finalizeSpawn(level, difficulty, reason, spawnData, entityData);
        
        // Initialize body part health
        float maxHealth = this.getMaxHealth();
        this.leftTendrilHealth = maxHealth * TENDRIL_HEALTH_MULTIPLIER;
        this.rightTendrilHealth = maxHealth * TENDRIL_HEALTH_MULTIPLIER;
        this.headHealth = maxHealth * TENDRIL_HEALTH_MULTIPLIER;
        
        // Initialize body parts on both sides (needed for server-side hit detection
        // and client-side rendering). EntityBody uses PartEntity which handles both sides.
        this.leftTendril = new EntityBodyPart(this, 1.2f, 1.5f, 1.0f, 2.0f, 1.5f, 0.0f, -1, 1, true);
        this.rightTendril = new EntityBodyPart(this, 1.2f, 1.5f, 1.0f, 2.0f, 1.5f, 0.0f, 1, 2, true);
        this.head = new EntityBodyPart(this, 1.0f, 1.0f, 1.5f, 0.0f, 2.5f, 0.0f, 0, 3, false, 0.8f);

        return spawnData;
    }

    
    // ========== Tick Update ==========
    
    @Override
    public void tick() {
        super.tick();
        
        // Sync flying state
        setFlying(isFlying());
        
        // Update body parts
        if (!level().isClientSide) {
            if (headHealth > 0.0f && head != null) {
                head.tick();
            }
            if (leftTendrilHealth > 0.0f && leftTendril != null) {
                leftTendril.tick();
            }
            if (rightTendrilHealth > 0.0f && rightTendril != null) {
                rightTendril.tick();
            }
        }
        
        // Server-side logic
        if (!level().isClientSide && this.tickCount % 10 == 0) {
            // Check for flight state adjustment
            if (isFlying() && !onGround()) {
                // Apply slight upward motion when flying
                if (random.nextInt(3) == 0) {
                    setDeltaMovement(getDeltaMovement().add(0, 0.1, 0));
                }
            }
            
            // Handle raining orbs skill
            if (rainingOrbsRemaining > 0) {
                handleRainingOrbs();
            }
            
            // Random flight state change
            if (vomitTimer <= 0 && random.nextInt(25) == 0 && !isFlying()) {
                setFlying(true);
            }
        }
        
        // Handle vomit attack
        if (vomitTimer > 0) {
            handleVomitAttack();
        } else {
            isRaining = false;
        }
        
        // Flying timer and animation
        if (isFlying()) {
            flyingTimer++;
            wingAnimationSpeed = 0.782f;
            wingAnimationTimer += wingAnimationSpeed;
            
            // Play wing sound periodically
            if (wingAnimationTimer >= 24.0f) {
                playSound(ModSounds.HEBLU_IDLE.get(), 5.0f, 0.8f + random.nextFloat() * 0.3f);
                wingAnimationTimer = 0.0f;
            }
            
            // Land if on ground for extended period
            if (onGround() && !level().isClientSide && flyingTimer > 40) {
                setFlying(false);
            }
        } else {
            flyingTimer = 0;
            wingAnimationSpeed = 0.0f;
        }
        
        // Client-side particles
        if (level().isClientSide && isFlying()) {
            spawnFlyingParticles();
        }
    }

    
    // ========== Raining Orbs Skill ==========
    
    private void handleRainingOrbs() {
        rainingOrbsRemaining--;
        
        if (rainingOrbsRemaining <= 15) {
            double radius = RAINING_RADIUS;
            double x, z;
            
            // Target current enemy or random position
            if (getTarget() != null && random.nextBoolean()) {
                LivingEntity target = getTarget();
                x = target.getX() + (random.nextDouble() * 2.0 - 1.0) * radius;
                z = target.getZ() + (random.nextDouble() * 2.0 - 1.0) * radius;
            } else {
                x = getX() + (random.nextDouble() * 2.0 - 1.0) * radius;
                z = getZ() + (random.nextDouble() * 2.0 - 1.0) * radius;
            }
            
            double y = getY() + 20.0;
            
            // Spawn orb projectile
            if (!level().isClientSide) {
                EntityOrbBase orb = ModEntities.PROJECTILE_ORB_SCARY.get().create(level());
                if (orb != null) {
                    orb.setPos(x, y, z);
                    orb.setDeltaMovement(0, -0.5, 0);
                    level().addFreshEntity(orb);
                }
            }
        }
    }

    
    // ========== Vomit Attack ==========
    
    private void handleVomitAttack() {
        vomitTimer--;
        
        if (vomitTargetPos != null) {
            lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(vomitTargetPos.getX() + 0.5, vomitTargetPos.getY(), vomitTargetPos.getZ() + 0.5));
        }
        
        // Client-side particles
        if (level().isClientSide) {
            for (int i = 0; i < 19; i++) {
                Vec3 lookVector = getLookAngle();
                double boneDistance = isRaining ? (isFlying() ? 4.3 : 6.1) : 8.2;
                double offsetX = getX() + lookVector.x * boneDistance;
                double offsetY = getY() + getEyeHeight() + (isRaining ? 7.2 : 2.2);
                double offsetZ = getZ() + lookVector.z * boneDistance;
                
                double motionX = -Mth.sin(getYRot() * ((float)Math.PI / 180F)) * 1.4;
                double motionZ = Mth.cos(getYRot() * ((float)Math.PI / 180F)) * 1.4;
                double motionY = isRaining ? 4.5 + random.nextDouble() * 0.3 : -0.55 + random.nextDouble() * 0.5;
                
                double spreadFactor = isRaining ? 0.2 : 0.55;
                motionX += (random.nextDouble() - 0.5) * spreadFactor;
                motionZ += (random.nextDouble() - 0.5) * spreadFactor;
                
                // Spawn flame and toxic cloud particles
                level().addParticle(ParticleTypes.FLAME, offsetX, offsetY, offsetZ, motionX, motionY, motionZ);
                level().addParticle(ModParticleTypes.SPORE.get(), offsetX, offsetY, offsetZ, motionX, motionY, motionZ);
            }
        }
    }

    
    // ========== Particle Effects ==========
    
    private void spawnFlyingParticles() {
        if (random.nextInt(5) == 0) {
            for (int i = 0; i < 3; i++) {
                level().addParticle(ParticleTypes.SMOKE,
                        getX() + (random.nextDouble() - 0.5) * getBbWidth(),
                        getY() + random.nextDouble() * getBbHeight(),
                        getZ() + (random.nextDouble() - 0.5) * getBbWidth(),
                        0.0, 0.02, 0.0);
            }
        }
    }

    
    // ========== Combat Methods ==========
    
    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        if (vomitTimer <= 0) {
            // Check if target is at different elevation
            if (target.getY() > getY() + 5.0 || target.getY() < getY()) {
                // Direct projectile attack
                Vec3 lookVector = getLookAngle();
                double d1 = 4.0;
                double d2 = target.getX() - (getX() + lookVector.x * d1);
                double d3 = target.getBoundingBox().minY + (double) (target.getBbHeight() / 2.0) 
                        - (0.5 + getY() + (double) (getBbHeight() / 2.0));
                double d4 = target.getZ() - (getZ() + lookVector.z * d1);
                
                if (!level().isClientSide) {
                    EntityOrbBase projectile = ModEntities.PROJECTILE_ORB_SCARY.get().create(level());
                    if (projectile != null) {
                        projectile.setOwner(this);
                        projectile.setPos(getX() + lookVector.x * d1, 
                                getY() + getBbHeight() / 2.0 + 0.5, 
                                getZ() + lookVector.z * d1);
                        projectile.shoot(d2, d3, d4, 1.6f, 12.0f);
                        level().addFreshEntity(projectile);
                    }
                    
                    // Spawn impact warning particles
                    ((ServerLevel) level()).sendParticles(ParticleTypes.FLAME,
                            getX() + lookVector.x * d1, getY() + getBbHeight() / 2.0 + 0.5, 
                            getZ() + lookVector.z * d1, 3, 0.0, -1.0, 0.0, 0.0);
                }
            } else {
                // Vomit attack
                vomitTimer = 40;
                vomitTargetPos = target.blockPosition();
                
                if (random.nextBoolean()) {
                    isRaining = true;
                    rainingOrbsRemaining = MAX_RAINING_ORBS;
                }
                
                playSound(ModSounds.HEBLU_SHOOT.get(), getSoundVolume() * 2.0f, 
                        0.8f + random.nextFloat() * 0.4f);
            }
        }
    }

    
    @Override
    public boolean doHurtTarget(Entity target) {
        if (!super.doHurtTarget(target)) {
            return false;
        }
        
        // Apply knockback and additional effects
        if (target instanceof LivingEntity livingTarget) {
            // Knockback
            target.setDeltaMovement(target.getDeltaMovement().add(0, 0.3, 0));
            
            // Apply debuff (if configured)
            if (true) { // TODO: Add ModConfig entry for hebluDebuffEnabled
                livingTarget.addEffect(new MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
            }
        }
        
        return true;
    }

    
    @Override
    public boolean hurt(DamageSource source, float amount) {
        // Redirect damage to body parts if applicable
        if (source.getEntity() instanceof LivingEntity attacker) {
            // Check if hitting a body part (simplified - full implementation would use raytracing)
            double distToLeft = getBodyPartDistance(leftTendril, attacker);
            double distToRight = getBodyPartDistance(rightTendril, attacker);
            double distToHead = getBodyPartDistance(head, attacker);
            
            float minDist = (float) Math.min(distToLeft, Math.min(distToRight, distToHead));
            
            if (distToLeft == minDist && leftTendrilHealth > 0) {
                leftTendrilHealth -= amount * 0.8f; // Reduced damage to tendrils
                if (leftTendrilHealth <= 0) {
                    leftTendrilHealth = 0;
                    // Tendril destroyed - apply penalty
                    if (!level().isClientSide) {
                        // Could apply visual effect or stat reduction
                    }
                }
                return super.hurt(source, amount * 0.5f);
            } else if (distToRight == minDist && rightTendrilHealth > 0) {
                rightTendrilHealth -= amount * 0.8f;
                if (rightTendrilHealth <= 0) {
                    rightTendrilHealth = 0;
                }
                return super.hurt(source, amount * 0.5f);
            } else if (distToHead == minDist && headHealth > 0) {
                headHealth -= amount * 1.2f; // Increased damage to head
                if (headHealth <= 0) {
                    headHealth = 0;
                    // Head destroyed - significant penalty
                    if (!level().isClientSide) {
                        // Could apply major stat reduction or enrage
                    }
                }
                return super.hurt(source, amount * 1.5f);
            }
        }
        
        return super.hurt(source, amount);
    }

    
    private double getBodyPartDistance(EntityBodyPart part, LivingEntity attacker) {
        if (part == null) return Double.MAX_VALUE;
        return attacker.distanceToSqr(part);
    }

    
    // ========== Sound Events ==========
    
    @Override
    @Nullable

    protected SoundEvent getAmbientSound() {
        return ModSounds.HEBLU_IDLE.get();
    }

    
    @Override

    @Nullable

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.HEBLU_HURT.get();
    }

    
    @Override

    @Nullable

    protected SoundEvent getDeathSound() {
        return ModSounds.HEBLU_DEATH.get();
    }

    
    // ========== Getters and Setters ==========
    
    public boolean isFlying() {
        return this.entityData.get(DATA_FLYING);
    }

    
    public void setFlying(boolean flying) {
        this.entityData.set(DATA_FLYING, flying);
        this.flyingTimer = flying ? 1 : 0;
    }

    
    public boolean isAttacking() {
        return this.entityData.get(DATA_ATTACKING);
    }

    
    public void setAttacking(boolean attacking) {
        this.entityData.set(DATA_ATTACKING, attacking);
    }

    
    public float getLeftTendrilHealth() {
        return leftTendrilHealth;
    }

    
    public float getRightTendrilHealth() {
        return rightTendrilHealth;
    }

    
    public float getHeadHealth() {
        return headHealth;
    }

    
    // ========== Inner Classes ==========
    
    /**
     * Body Part Entity for Heblu's appendages
     * Follows parent entity with offset positioning
     */
    private static class EntityBodyPart extends Entity {
        private final EntityHeblu parent;
        private final float damageMultiplier;
        private final float eyeHeight;
        private final float offsetX;
        private final float offsetY;
        private final float offsetZ;
        private final int inverted;
        private final int partId;
        private final boolean isSidePart;
        
        public EntityBodyPart(EntityHeblu parent, float width, float height, 
                             float damageMultiplier, float offsetX, float offsetY, float offsetZ,
                             int inverted, int partId, boolean isSidePart) {
            this(parent, width, height, damageMultiplier, offsetX, offsetY, offsetZ, inverted, partId, isSidePart, height * 0.8f);
        }
        
        public EntityBodyPart(EntityHeblu parent, float width, float height,
                             float damageMultiplier, float offsetX, float offsetY, float offsetZ,
                             int inverted, int partId, boolean isSidePart, float eyeHeight) {
            super(ModEntities.PRIMITIVE_BANO.get(), parent.level()); // Placeholder type
            this.parent = parent;
            this.damageMultiplier = damageMultiplier;
            this.eyeHeight = eyeHeight;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
            this.inverted = inverted;
            this.partId = partId;
            this.isSidePart = isSidePart;
            // TODO: Dynamic size not directly settable in 1.20.1; override getDimensions(Pose) if needed
        }
        
        @Override
        public void tick() {
            if (parent == null || parent.isRemoved()) {
                discard();
                return;
            }
            
            if (isSidePart) {
                updatePositionWithParentSides();
            } else {
                updatePositionWithParentFront();
            }
            
            super.tick();
        }
        
        private void updatePositionWithParentSides() {
            float yawRad = parent.getYRot() * ((float)Math.PI / 180F);
            float sinYaw = Mth.sin(yawRad);
            float cosYaw = Mth.cos(yawRad);
            
            setYRot(parent.getYRot());
            setPos(
                parent.getX() + (double) inverted * (double) (cosYaw * offsetX),
                parent.getY() + (double) offsetY,
                parent.getZ() + (double) inverted * (double) (sinYaw * offsetZ)
            );
        }
        
        private void updatePositionWithParentFront() {
            float yawRad = parent.getYRot() * ((float)Math.PI / 180F);
            float pitchRad = 0.17453292f;
            float cosPitch = Mth.cos(pitchRad);
            float sinYaw = Mth.sin(yawRad);
            float cosYaw = Mth.cos(yawRad);
            
            setYRot(parent.getYRot());
            setPos(
                parent.getX() + (double) inverted * (double) (sinYaw * offsetX * cosPitch),
                parent.getY() + (double) offsetY,
                parent.getZ() - (double) inverted * (double) (cosYaw * offsetX * cosPitch)
            );
        }
        
        @Override
        public boolean hurt(DamageSource source, float amount) {
            if (level().isClientSide && source.getEntity() instanceof Player) {
                // Send packet to server for body part hit (networking not implemented yet)
                return false;
            }
            
            // Redirect damage to parent
            return parent.hurt(source, amount * damageMultiplier);
        }
        
        @Override
        protected void defineSynchedData() {
            // No synced data needed
        }
        
        @Override
        protected void readAdditionalSaveData(CompoundTag compound) {
            // No NBT needed
        }
        
        @Override
        protected void addAdditionalSaveData(CompoundTag compound) {
            // No NBT needed
        }
        
        public int getPartId() {
            return partId;
        }
        
        public EntityHeblu getParent() {
            return parent;
        }
        
        // getStandingEyeHeight is final in 1.20.1 PartEntity; use eyeHeight field directly where needed
        
        @Override
        public boolean isPickable() {
            return true;
        }
    }

    
    /**
     * Flight Attack Goal - Enables aerial maneuvering and positioning
     */
    private class HebluFlightAttackGoal extends net.minecraft.world.entity.ai.goal.Goal {
        private final EntityHeblu heblu;
        private int flightAdjustmentTimer;
        
        public HebluFlightAttackGoal() {
            this.heblu = EntityHeblu.this;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }
        
        @Override
        public boolean canUse() {
            LivingEntity target = heblu.getTarget();
            return target != null && heblu.canAttack(target);
        }
        
        @Override
        public void start() {
            flightAdjustmentTimer = 0;
        }
        
        @Override
        public void tick() {
            LivingEntity target = heblu.getTarget();
            if (target == null) return;
            
            // Maintain optimal flying height
            double targetHeight = target.getY() + 3.0;
            double heightDiff = targetHeight - heblu.getY();
            
            if (Math.abs(heightDiff) > 2.0) {
                heblu.setDeltaMovement(heblu.getDeltaMovement().add(0, Math.signum(heightDiff) * 0.15, 0));
            }
            
            // Circle around target at range
            double distance = heblu.distanceToSqr(target);
            double optimalRange = 16.0; // Optimal ranged attack distance
            
            if (distance > optimalRange * optimalRange) {
                // Move closer
                heblu.getNavigation().moveTo(target, 1.2);
            } else if (distance < (optimalRange * 0.6) * (optimalRange * 0.6)) {
                // Move back
                Vec3 escapeDir = heblu.position().vectorTo(target.position()).normalize().scale(-1);
                heblu.getNavigation().moveTo(heblu.getX() + escapeDir.x * 5, 
                                           heblu.getY(), 
                                           heblu.getZ() + escapeDir.z * 5, 1.0);
            }
            
            // Periodic flight adjustments
            flightAdjustmentTimer++;
            if (flightAdjustmentTimer % 20 == 0 && !heblu.onGround()) {
                // Add slight horizontal drift for evasive movement
                heblu.setDeltaMovement(heblu.getDeltaMovement().add(
                    (heblu.random.nextDouble() - 0.5) * 0.2,
                    0,
                    (heblu.random.nextDouble() - 0.5) * 0.2
                ));
            }
        }
    }

    
    /**
     * Melee Attack Goal - Close-range attacks with AOE effect
     */
    private class HebluMeleeAttackGoal extends MeleeAttackGoal {
        public HebluMeleeAttackGoal() {
            super(EntityHeblu.this, 1.3, false);
        }
        
        @Override
        protected void checkAndPerformAttack(LivingEntity target, double distSq) {
            double attackRange = 9.0; // Extended melee range due to tendrils
            
            if (distSq <= attackRange * attackRange) {
                this.resetAttackCooldown();
                this.mob.swing(InteractionHand.MAIN_HAND);
                
                // Deal damage to primary target
                if (mob.doHurtTarget(target)) {
                    // AOE damage to nearby enemies
                    List<LivingEntity> nearby = level().getNearbyEntities(
                        LivingEntity.class,
                        net.minecraft.world.entity.ai.targeting.TargetingConditions.DEFAULT,
                        mob,
                        mob.getBoundingBox().inflate(attackRange / 2.0)
                    );
                    
                    for (LivingEntity nearbyEntity : nearby) {
                        if (nearbyEntity != target && mob.canAttack(nearbyEntity)) {
                            nearbyEntity.hurt(mob.damageSources().mobAttack(mob), 
                                            (float) mob.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.6f);
                            nearbyEntity.knockback(0.4, 
                                mob.getX() - nearbyEntity.getX(), 
                                mob.getZ() - nearbyEntity.getZ());
                        }
                    }
                }
            }
        }
    }

    
    /**
     * Ranged Attack Goal - Manages projectile attacks and special skills
     */
    private class HebluRangedAttackGoal extends net.minecraft.world.entity.ai.goal.Goal {
        private final EntityHeblu heblu;
        private int attackCooldown;
        private int seeTime;
        private final double speedModifier;
        private final int attackIntervalMin;
        private final int attackIntervalMax;
        private final float attackRadius;
        
        public HebluRangedAttackGoal() {
            this.heblu = EntityHeblu.this;
            this.speedModifier = 1.3;
            this.attackIntervalMin = 100;
            this.attackIntervalMax = 140;
            this.attackRadius = 40.0f;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }
        
        @Override
        public boolean canUse() {
            LivingEntity target = heblu.getTarget();
            return target != null && heblu.canAttack(target) && heblu.distanceToSqr(target) <= attackRadius * attackRadius;
        }
        
        @Override
        public boolean canContinueToUse() {
            return this.canUse() && !heblu.getNavigation().isDone();
        }
        
        @Override
        public void start() {
            this.attackCooldown = 0;
            this.seeTime = 0;
        }
        
        @Override
        public void stop() {
            heblu.setAttacking(false);
        }
        
        @Override
        public void tick() {
            LivingEntity target = heblu.getTarget();
            if (target == null) return;
            
            double distance = heblu.distanceToSqr(target);
            boolean canSee = heblu.getSensing().hasLineOfSight(target);
            
            if (canSee) {
                seeTime++;
            } else {
                seeTime = 0;
            }
            
            // Move towards or away from target based on distance
            if (distance > (attackRadius * 0.75) * (attackRadius * 0.75)) {
                heblu.getNavigation().moveTo(target, speedModifier);
            } else if (distance < (attackRadius * 0.3) * (attackRadius * 0.3)) {
                // Back away if too close
                Vec3 escapeDir = heblu.position().vectorTo(target.position()).normalize().scale(-1);
                heblu.getNavigation().moveTo(
                    heblu.getX() + escapeDir.x * 8,
                    heblu.getY(),
                    heblu.getZ() + escapeDir.z * 8,
                    speedModifier
                );
            }
            
            heblu.lookAt(target, 30.0f, 30.0f);
            
            // Attack when ready and have clear line of sight
            if (--attackCooldown <= 0 && seeTime >= 20) {
                heblu.setAttacking(true);
                heblu.performRangedAttack(target, 1.0f);
                attackCooldown = attackIntervalMin + heblu.random.nextInt(attackIntervalMax - attackIntervalMin);
                seeTime = 0;
            }
        }
    }
}
