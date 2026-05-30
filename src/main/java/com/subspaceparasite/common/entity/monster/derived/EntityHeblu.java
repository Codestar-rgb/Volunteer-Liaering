package com.subspaceparasite.common.entity.monster.derived;

import com.subspaceparasite.api.IHitboxedEntity;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.common.entity.projectile.EntityOrbBase;
import com.subspaceparasite.config.ModConfigSystems;
import com.subspaceparasite.core.ModEntities;
import com.subspaceparasite.core.ModSounds;
import com.subspaceparasite.core.ModParticleTypes;
import com.subspaceparasite.handler.DamageHandler;
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
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
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
public class EntityHeblu extends EntityParasiteBase implements RangedAttackMob, IHitboxedEntity {
    
    // ========== SynchedEntityData Accessors ==========\n
    private static final EntityDataAccessor<Boolean> DATA_FLYING =
            SynchedEntityData.defineId(EntityHeblu.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_ATTACKING =
            SynchedEntityData.defineId(EntityHeblu.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_VOMIT_TIMER =
            SynchedEntityData.defineId(EntityHeblu.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_RAINING_ORBS =
            SynchedEntityData.defineId(EntityHeblu.class, EntityDataSerializers.INT);
    
    // ========== Body Part Health ==========\n
    private float leftTendrilHealth;
    private float rightTendrilHealth;
    private float headHealth;
    
    // ========== Body Parts (Client-side rendering support) ==========\n
    @Nullable
    private EntityBodyPart leftTendril;
    @Nullable
    private EntityBodyPart rightTendril;
    @Nullable
    private EntityBodyPart head;
    
    // ========== Combat State ==========\n
    private int flyingTimer = 0;
    private float wingAnimationTimer = 0.0f;
    private float wingAnimationSpeed = 0.0f;
    private BlockPos vomitTargetPos = null;
    private int vomitTimer = 0;
    private boolean isRaining = false;
    private int rainingOrbsRemaining = 0;
    private int orbSpawnCooldown = 0;
    
    // ========== Constants ==========\n
    private static final float BASE_HEALTH = 80.0f;
    private static final float ARMOR_VALUE = 12.0f;
    private static final float ATTACK_DAMAGE = 8.0f;
    private static final double MOVEMENT_SPEED = 0.35;
    private static final double FOLLOW_RANGE = 64.0;
    private static final float TENDRIL_HEALTH_MULTIPLIER = 0.4f;
    private static final int MAX_RAINING_ORBS = 20;
    private static final double RAINING_RADIUS = 12.0;
    
    // ========== Constructor ==========\n
    \n    public EntityHeblu(EntityType<? extends EntityHeblu> type, Level level) {
        super(type, level);\n
        this.canModRender = 0;
        this.type = (byte) 14; // Derived stage type ID
        this.killcount = -10.0f;
    }\n
    \n    // ========== Data Registration ==========\n    \n    @Override
    protected void defineSynchedData() {\n
        super.defineSynchedData();\n
        this.entityData.define(DATA_FLYING, false);\n
        this.entityData.define(DATA_ATTACKING, false);\n
        this.entityData.define(DATA_VOMIT_TIMER, 0);\n
        this.entityData.define(DATA_RAINING_ORBS, 0);\n
    }\n
    \n    // ========== NBT Persistence ==========\n    \n    @Override
    public void addAdditionalSaveData(@Nonnull CompoundTag compound) {\n
        super.addAdditionalSaveData(compound);\n
        compound.putFloat("LeftTendrilHealth", this.leftTendrilHealth);\n
        compound.putFloat("RightTendrilHealth", this.rightTendrilHealth);\n
        compound.putFloat("HeadHealth", this.headHealth);\n
        compound.putInt("VomitTimer", this.vomitTimer);\n
        compound.putBoolean("IsRaining", this.isRaining);\n
        compound.putInt("RainingOrbsRemaining", this.rainingOrbsRemaining);\n
        compound.putBoolean("Flying", this.isFlying());\n
    }\n
    \n    @Override
    public void readAdditionalSaveData(@Nonnull CompoundTag compound) {\n
        super.readAdditionalSaveData(compound);\n
        if (compound.contains("LeftTendrilHealth")) {\n
            this.leftTendrilHealth = compound.getFloat("LeftTendrilHealth");\n
        }\n
        if (compound.contains("RightTendrilHealth")) {\n
            this.rightTendrilHealth = compound.getFloat("RightTendrilHealth");\n
        }\n
        if (compound.contains("HeadHealth")) {\n
            this.headHealth = compound.getFloat("HeadHealth");\n
        }\n
        if (compound.contains("VomitTimer")) {\n
            this.vomitTimer = compound.getInt("VomitTimer");\n
        }\n
        if (compound.contains("IsRaining")) {\n
            this.isRaining = compound.getBoolean("IsRaining");\n
        }\n
        if (compound.contains("RainingOrbsRemaining")) {\n
            this.rainingOrbsRemaining = compound.getInt("RainingOrbsRemaining");\n
        }\n
        if (compound.contains("Flying")) {\n
            setFlying(compound.getBoolean("Flying"));\n
        }\n
    }\n
    \n    // ========== Attribute Supplier ==========\n    \n    public static AttributeSupplier.Builder createAttributes() {\n
        return EntityParasiteBase.createLivingAttributes()\n                .add(Attributes.MAX_HEALTH, BASE_HEALTH)\n                .add(Attributes.ARMOR, ARMOR_VALUE)\n                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE)\n                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)\n                .add(Attributes.FOLLOW_RANGE, FOLLOW_RANGE)\n                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6)\n                .add(Attributes.FLYING_SPEED, 0.4);\n    }\n
    \n    // ========== Spawn Rules ==========\n    \n    public static boolean checkHebluSpawnRules(EntityType<EntityHeblu> entityType, ServerLevelAccessor level,\n                                               MobSpawnType spawnType, BlockPos pos, RandomSource random) {\n        return checkMonsterSpawnRules(entityType, level, spawnType, pos, random)\n                && level.getDifficulty() != DifficultyInstance.PEACEFUL;\n    }\n
    \n    // ========== Initialization ==========\n    \n    @Override
    protected void registerGoals() {\n        super.registerGoals();\n        \n        // Basic goals\n        this.goalSelector.addGoal(0, new FloatGoal(this));\n        \n        // Flight attack goal\n        this.goalSelector.addGoal(1, new HebluFlightAttackGoal());\n        \n        // Melee attack with AOE\n        this.goalSelector.addGoal(2, new HebluMeleeAttackGoal());\n        \n        // Ranged attack\n        this.goalSelector.addGoal(3, new HebluRangedAttackGoal());\n        \n        // Random movement\n        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1.0, 15));\n        \n        // Look at target\n        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0f));\n        \n        // Target goals\n        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));\n        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));\n        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, false));\n    }\n
    \n    @Override
    public void onInitialSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,\n                               MobSpawnType reason, @Nullable CompoundTag spawnData,\n                               @Nullable CompoundTag entityData) {\n        super.onInitialSpawn(level, difficulty, reason, spawnData, entityData);\n        \n        // Initialize body part health\n        float maxHealth = this.getMaxHealth();\n        this.leftTendrilHealth = maxHealth * TENDRIL_HEALTH_MULTIPLIER;\n        this.rightTendrilHealth = maxHealth * TENDRIL_HEALTH_MULTIPLIER;\n        this.headHealth = maxHealth * TENDRIL_HEALTH_MULTIPLIER;\n        \n        // Initialize body parts (for rendering/collision)\n        if (level.getLevel().isClientSide) {\n            this.leftTendril = new EntityBodyPart(this, 1.2f, 1.5f, 1.0f, 2.0f, 1.5f, -1, 1, true);\n            this.rightTendril = new EntityBodyPart(this, 1.2f, 1.5f, 1.0f, 2.0f, 1.5f, 1, 2, true);\n            this.head = new EntityBodyPart(this, 1.0f, 1.0f, 1.5f, 0.0f, 2.5f, 0, 3, false, 0.8f);\n        }\n    }\n
    \n    // ========== Tick Update ==========\n    \n    @Override
    public void tick() {\n        super.tick();\n        \n        // Sync flying state\n        setFlying(isFlying());\n        \n        // Update body parts\n        if (!level().isClientSide) {\n            if (headHealth > 0.0f && head != null) {\n                head.tick();\n            }\n            if (leftTendrilHealth > 0.0f && leftTendril != null) {\n                leftTendril.tick();\n            }\n            if (rightTendrilHealth > 0.0f && rightTendril != null) {\n                rightTendril.tick();\n            }\n        }\n        \n        // Server-side logic\n        if (!level().isClientSide && this.tickCount % 10 == 0) {\n            // Check for flight state adjustment\n            if (isFlying() && !onGround()) {\n                // Apply slight upward motion when flying\n                if (random.nextInt(3) == 0) {\n                    setDeltaMovement(getDeltaMovement().add(0, 0.1, 0));\n                }\n            }\n            \n            // Handle raining orbs skill\n            if (rainingOrbsRemaining > 0) {\n                handleRainingOrbs();\n            }\n            \n            // Random flight state change\n            if (vomitTimer <= 0 && random.nextInt(25) == 0 && !isFlying()) {\n                setFlying(true);\n            }\n        }\n        \n        // Handle vomit attack\n        if (vomitTimer > 0) {\n            handleVomitAttack();\n        } else {\n            isRaining = false;\n        }\n        \n        // Flying timer and animation\n        if (isFlying()) {\n            flyingTimer++;\n            wingAnimationSpeed = 0.782f;\n            wingAnimationTimer += wingAnimationSpeed;\n            \n            // Play wing sound periodically\n            if (wingAnimationTimer >= 24.0f) {\n                playSound(ModSounds.HEBLU_IDLE.get(), 5.0f, 0.8f + random.nextFloat() * 0.3f);\n                wingAnimationTimer = 0.0f;\n            }\n            \n            // Land if on ground for extended period\n            if (onGround() && !level().isClientSide && flyingTimer > 40) {\n                setFlying(false);\n            }\n        } else {\n            flyingTimer = 0;\n            wingAnimationSpeed = 0.0f;\n        }\n        \n        // Client-side particles\n        if (level().isClientSide && isFlying()) {\n            spawnFlyingParticles();\n        }\n    }\n
    \n    // ========== Raining Orbs Skill ==========\n    \n    private void handleRainingOrbs() {\n        rainingOrbsRemaining--;\n        \n        if (rainingOrbsRemaining <= 15) {\n            double radius = RAINING_RADIUS;\n            double x, z;\n            \n            // Target current enemy or random position\n            if (getTarget() != null && random.nextBoolean()) {\n                LivingEntity target = getTarget();\n                x = target.getX() + (random.nextDouble() * 2.0 - 1.0) * radius;\n                z = target.getZ() + (random.nextDouble() * 2.0 - 1.0) * radius;\n            } else {\n                x = getX() + (random.nextDouble() * 2.0 - 1.0) * radius;\n                z = getZ() + (random.nextDouble() * 2.0 - 1.0) * radius;\n            }\n            \n            double y = getY() + 20.0;\n            \n            // Spawn orb projectile\n            if (!level().isClientSide) {\n                EntityOrbBase orb = ModEntities.PROJECTILE_ORB_SCARY.get().create(level());\n                if (orb != null) {\n                    orb.setPos(x, y, z);\n                    orb.setDeltaMovement(0, -0.5, 0);\n                    level().addFreshEntity(orb);\n                }\n            }\n        }\n    }\n
    \n    // ========== Vomit Attack ==========\n    \n    private void handleVomitAttack() {\n        vomitTimer--;\n        \n        if (vomitTargetPos != null) {\n            lookAt(vomitTargetPos.getX() + 0.5, vomitTargetPos.getY(), vomitTargetPos.getZ() + 0.5);\n        }\n        \n        // Client-side particles\n        if (level().isClientSide) {\n            for (int i = 0; i < 19; i++) {\n                Vec3 lookVector = getLookAngle();\n                double boneDistance = isRaining ? (isFlying() ? 4.3 : 6.1) : 8.2;\n                double offsetX = getX() + lookVector.x * boneDistance;\n                double offsetY = getY() + getEyeHeight() + (isRaining ? 7.2 : 2.2);\n                double offsetZ = getZ() + lookVector.z * boneDistance;\n                \n                double motionX = -Mth.sin((float) (yRot * (Math.PI / 180.0))) * 1.4;\n                double motionZ = Mth.cos((float) (yRot * (Math.PI / 180.0))) * 1.4;\n                double motionY = isRaining ? 4.5 + random.nextDouble() * 0.3 : -0.55 + random.nextDouble() * 0.5;\n                \n                double spreadFactor = isRaining ? 0.2 : 0.55;\n                motionX += (random.nextDouble() - 0.5) * spreadFactor;\n                motionZ += (random.nextDouble() - 0.5) * spreadFactor;\n                \n                // Spawn flame and toxic cloud particles\n                level().addParticle(ParticleTypes.FLAME, offsetX, offsetY, offsetZ, motionX, motionY, motionZ);\n                level().addParticle(ModParticleTypes.SPORE.get(), offsetX, offsetY, offsetZ, motionX, motionY, motionZ);\n            }\n        }\n    }\n
    \n    // ========== Particle Effects ==========\n    \n    private void spawnFlyingParticles() {\n        if (random.nextInt(5) == 0) {\n            for (int i = 0; i < 3; i++) {\n                level().addParticle(ParticleTypes.SMOKE,\n                        getX() + (random.nextDouble() - 0.5) * getBbWidth(),\n                        getY() + random.nextDouble() * getBbHeight(),\n                        getZ() + (random.nextDouble() - 0.5) * getBbWidth(),\n                        0.0, 0.02, 0.0);\n            }\n        }\n    }\n
    \n    // ========== Combat Methods ==========\n    \n    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {\n        if (vomitTimer <= 0) {\n            // Check if target is at different elevation\n            if (target.getY() > getY() + 5.0 || target.getY() < getY()) {\n                // Direct projectile attack\n                Vec3 lookVector = getLookAngle();\n                double d1 = 4.0;\n                double d2 = target.getX() - (getX() + lookVector.x * d1);\n                double d3 = target.getBoundingBox().minY + (double) (target.getBbHeight() / 2.0) \n                        - (0.5 + getY() + (double) (getBbHeight() / 2.0));\n                double d4 = target.getZ() - (getZ() + lookVector.z * d1);\n                \n                if (!level().isClientSide) {\n                    EntityOrbBase projectile = ModEntities.PROJECTILE_ORB_SCARY.get().create(level());\n                    if (projectile != null) {\n                        projectile.setOwner(this);\n                        projectile.setPos(getX() + lookVector.x * d1, \n                                getY() + getBbHeight() / 2.0 + 0.5, \n                                getZ() + lookVector.z * d1);\n                        projectile.shoot(d2, d3, d4, 1.6f, 12.0f);\n                        level().addFreshEntity(projectile);\n                    }\n                    \n                    // Spawn impact warning particles\n                    ((ServerLevel) level()).sendParticles(ParticleTypes.FLAME,\n                            getX() + lookVector.x * d1, getY() + getBbHeight() / 2.0 + 0.5, \n                            getZ() + lookVector.z * d1, 3, 0.0, -1.0, 0.0, 0.0);\n                }\n            } else {\n                // Vomit attack\n                vomitTimer = 40;\n                vomitTargetPos = target.blockPosition();\n                \n                if (random.nextBoolean()) {\n                    isRaining = true;\n                    rainingOrbsRemaining = MAX_RAINING_ORBS;\n                }\n                \n                playSound(ModSounds.HEBLU_SHOOT.get(), getSoundVolume() * 2.0f, \n                        0.8f + random.nextFloat() * 0.4f);\n            }\n        }\n    }\n
    \n    @Override
    public boolean doHurtTarget(Entity target) {\n        if (!super.doHurtTarget(target)) {\n            return false;\n        }\n        \n        // Apply knockback and additional effects\n        if (target instanceof LivingEntity livingTarget) {\n            // Knockback\n            target.setDeltaMovement(target.getDeltaMovement().add(0, 0.3, 0));\n            \n            // Apply debuff (if configured)\n            if (ModConfigSystems.COMMON.hebluDebuffEnabled.get()) {\n                livingTarget.addEffect(new MobEffectInstance(\n                        net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 60, 1));\n            }\n        }\n        \n        return true;\n    }\n
    \n    @Override
    public boolean hurt(DamageSource source, float amount) {\n        // Redirect damage to body parts if applicable\n        if (source.getEntity() instanceof LivingEntity attacker) {\n            // Check if hitting a body part (simplified - full implementation would use raytracing)\n            double distToLeft = getBodyPartDistance(leftTendril, attacker);\n            double distToRight = getBodyPartDistance(rightTendril, attacker);\n            double distToHead = getBodyPartDistance(head, attacker);\n            \n            float minDist = Math.min(distToLeft, Math.min(distToRight, distToHead));\n            \n            if (distToLeft == minDist && leftTendrilHealth > 0) {\n                leftTendrilHealth -= amount * 0.8f; // Reduced damage to tendrils\n                if (leftTendrilHealth <= 0) {\n                    leftTendrilHealth = 0;\n                    // Tendril destroyed - apply penalty\n                    if (!level().isClientSide) {\n                        // Could apply visual effect or stat reduction\n                    }\n                }\n                return super.hurt(source, amount * 0.5f);\n            } else if (distToRight == minDist && rightTendrilHealth > 0) {\n                rightTendrilHealth -= amount * 0.8f;\n                if (rightTendrilHealth <= 0) {\n                    rightTendrilHealth = 0;\n                }\n                return super.hurt(source, amount * 0.5f);\n            } else if (distToHead == minDist && headHealth > 0) {\n                headHealth -= amount * 1.2f; // Increased damage to head\n                if (headHealth <= 0) {\n                    headHealth = 0;\n                    // Head destroyed - significant penalty\n                    if (!level().isClientSide) {\n                        // Could apply major stat reduction or enrage\n                    }\n                }\n                return super.hurt(source, amount * 1.5f);\n            }\n        }\n        \n        return super.hurt(source, amount);\n    }\n
    \n    private double getBodyPartDistance(EntityBodyPart part, LivingEntity attacker) {\n        if (part == null) return Double.MAX_VALUE;\n        return attacker.distanceToSqr(part);\n    }\n
    \n    // ========== Sound Events ==========\n    \n    @Override
    @Nullable\n
    protected SoundEvent getAmbientSound() {\n        return ModSounds.HEBLU_IDLE.get();\n    }\n
    \n    @Override\n
    @Nullable\n
    protected SoundEvent getHurtSound(DamageSource damageSource) {\n        return ModSounds.HEBLU_HURT.get();\n    }\n
    \n    @Override\n
    @Nullable\n
    protected SoundEvent getDeathSound() {\n        return ModSounds.HEBLU_DEATH.get();\n    }\n
    \n    // ========== Getters and Setters ==========\n    \n    public boolean isFlying() {\n        return this.entityData.get(DATA_FLYING);\n    }\n
    \n    public void setFlying(boolean flying) {\n        this.entityData.set(DATA_FLYING, flying);\n        this.flyingTimer = flying ? 1 : 0;\n    }\n
    \n    public boolean isAttacking() {\n        return this.entityData.get(DATA_ATTACKING);\n    }\n
    \n    public void setAttacking(boolean attacking) {\n        this.entityData.set(DATA_ATTACKING, attacking);\n    }\n
    \n    public float getLeftTendrilHealth() {\n        return leftTendrilHealth;\n    }\n
    \n    public float getRightTendrilHealth() {\n        return rightTendrilHealth;\n    }\n
    \n    public float getHeadHealth() {\n        return headHealth;\n    }\n
    \n    // ========== Inner Classes ==========\n    \n    /**
     * Body Part Entity for Heblu's appendages
     * Follows parent entity with offset positioning
     */\n    private static class EntityBodyPart extends Entity {\n        private final EntityHeblu parent;\n        private final float damageMultiplier;\n        private final float eyeHeight;\n        private final float offsetX;\n        private final float offsetY;\n        private final float offsetZ;\n        private final int inverted;\n        private final int partId;\n        private final boolean isSidePart;\n        \n        public EntityBodyPart(EntityHeblu parent, float width, float height, \n                             float damageMultiplier, float offsetX, float offsetY,\n                             int inverted, int partId, boolean isSidePart) {\n            this(parent, width, height, damageMultiplier, offsetX, offsetY, inverted, partId, isSidePart, height * 0.8f);\n        }\n        \n        public EntityBodyPart(EntityHeblu parent, float width, float height,\n                             float damageMultiplier, float offsetX, float offsetY,\n                             int inverted, int partId, boolean isSidePart, float eyeHeight) {\n            super(ModEntities.PRIMITIVE_BANO.get(), parent.level()); // Placeholder type\n            this.parent = parent;\n            this.damageMultiplier = damageMultiplier;\n            this.eyeHeight = eyeHeight;\n            this.offsetX = offsetX;\n            this.offsetY = offsetY;\n            this.offsetZ = offsetZ;\n            this.inverted = inverted;\n            this.partId = partId;\n            this.isSidePart = isSidePart;\n            this.setSize(width, height);\n        }\n        \n        @Override\n        public void tick() {\n            if (parent == null || parent.isRemoved()) {\n                discard();\n                return;\n            }\n            \n            if (isSidePart) {\n                updatePositionWithParentSides();\n            } else {\n                updatePositionWithParentFront();\n            }\n            \n            super.tick();\n        }\n        \n        private void updatePositionWithParentSides() {\n            float yawRad = parent.yRot * (float) (Math.PI / 180.0);\n            float sinYaw = Mth.sin(yawRad);\n            float cosYaw = Mth.cos(yawRad);\n            \n            yRot = parent.yRot;\n            setPos(\n                parent.getX() + (double) inverted * (double) (cosYaw * offsetX),\n                parent.getY() + (double) offsetY,\n                parent.getZ() + (double) inverted * (double) (sinYaw * offsetZ)\n            );\n        }\n        \n        private void updatePositionWithParentFront() {\n            float yawRad = parent.yRot * (float) (Math.PI / 180.0);\n            float pitchRad = 0.17453292f;\n            float cosPitch = Mth.cos(pitchRad);\n            float sinYaw = Mth.sin(yawRad);\n            float cosYaw = Mth.cos(yawRad);\n            \n            yRot = parent.yRot;\n            setPos(\n                parent.getX() + (double) inverted * (double) (sinYaw * offsetX * cosPitch),\n                parent.getY() + (double) offsetY,\n                parent.getZ() - (double) inverted * (double) (cosYaw * offsetX * cosPitch)\n            );\n        }\n        \n        @Override\n        public boolean hurt(DamageSource source, float amount) {\n            if (level().isClientSide && source.getEntity() instanceof Player) {\n                // Send packet to server for body part hit (networking not implemented yet)\n                return false;\n            }\n            \n            // Redirect damage to parent\n            return parent.hurt(source, amount * damageMultiplier);\n        }\n        \n        @Override\n        protected void defineSynchedData() {\n            // No synced data needed\n        }\n        \n        @Override\n        protected void readAdditionalSaveData(CompoundTag compound) {\n            // No NBT needed\n        }\n        \n        @Override\n        protected void addAdditionalSaveData(CompoundTag compound) {\n            // No NBT needed\n        }\n        \n        public int getId() {\n            return partId;\n        }\n        \n        public EntityHeblu getParent() {\n            return parent;\n        }\n        \n        @Override\n        public float getEyeHeight() {\n            return eyeHeight;\n        }\n        \n        @Override\n        public boolean isPickable() {\n            return true;\n        }\n    }\n
    \n    /**
     * Flight Attack Goal - Enables aerial maneuvering and positioning
     */\n    private class HebluFlightAttackGoal extends net.minecraft.world.entity.ai.goal.Goal {\n        private final EntityHeblu heblu;\n        private int flightAdjustmentTimer;\n        \n        public HebluFlightAttackGoal() {\n            this.heblu = EntityHeblu.this;\n            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));\n        }\n        \n        @Override\n        public boolean canUse() {\n            LivingEntity target = heblu.getTarget();\n            return target != null && heblu.canAttack(target, null);\n        }\n        \n        @Override\n        public void start() {\n            flightAdjustmentTimer = 0;\n        }\n        \n        @Override\n        public void tick() {\n            LivingEntity target = heblu.getTarget();\n            if (target == null) return;\n            \n            // Maintain optimal flying height\n            double targetHeight = target.getY() + 3.0;\n            double heightDiff = targetHeight - heblu.getY();\n            \n            if (Math.abs(heightDiff) > 2.0) {\n                heblu.setDeltaMovement(heblu.getDeltaMovement().add(0, Math.signum(heightDiff) * 0.15, 0));\n            }\n            \n            // Circle around target at range\n            double distance = heblu.distanceToSqr(target);\n            double optimalRange = 16.0; // Optimal ranged attack distance\n            \n            if (distance > optimalRange * optimalRange) {\n                // Move closer\n                heblu.getNavigation().moveTo(target, 1.2);\n            } else if (distance < (optimalRange * 0.6) * (optimalRange * 0.6)) {\n                // Move back\n                Vec3 escapeDir = heblu.position().vectorTo(target.position()).normalize().scale(-1);\n                heblu.getNavigation().moveTo(heblu.getX() + escapeDir.x * 5, \n                                           heblu.getY(), \n                                           heblu.getZ() + escapeDir.z * 5, 1.0);\n            }\n            \n            // Periodic flight adjustments\n            flightAdjustmentTimer++;\n            if (flightAdjustmentTimer % 20 == 0 && !heblu.onGround()) {\n                // Add slight horizontal drift for evasive movement\n                heblu.setDeltaMovement(heblu.getDeltaMovement().add(\n                    (heblu.random.nextDouble() - 0.5) * 0.2,\n                    0,\n                    (heblu.random.nextDouble() - 0.5) * 0.2\n                ));\n            }\n        }\n    }\n
    \n    /**
     * Melee Attack Goal - Close-range attacks with AOE effect
     */\n    private class HebluMeleeAttackGoal extends MeleeAttackGoal {\n        public HebluMeleeAttackGoal() {\n            super(EntityHeblu.this, 1.3, false);\n        }\n        \n        @Override\n        protected void checkAndPerformAttack(LivingEntity target, double distSq) {\n            double attackRange = 9.0; // Extended melee range due to tendrils\n            \n            if (distSq <= attackRange * attackRange) {\n                this.resetAttackCooldown();\n                this.mob.swing(Hand.MAIN_HAND);\n                \n                // Deal damage to primary target\n                if (mob.doHurtTarget(target)) {\n                    // AOE damage to nearby enemies\n                    List<LivingEntity> nearby = level().getNearbyEntities(\n                        LivingEntity.class,\n                        net.minecraft.world.entity.ai.targeting.TargetingConditions.DEFAULT,\n                        mob,\n                        mob.getBoundingBox().inflate(attackRange / 2.0)\n                    );\n                    \n                    for (LivingEntity nearbyEntity : nearby) {\n                        if (nearbyEntity != target && mob.canAttack(nearbyEntity, null)) {\n                            nearbyEntity.hurt(mob.damageSources().mobAttack(mob), \n                                            (float) mob.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.6f);\n                            nearbyEntity.knockback(0.4, \n                                mob.getX() - nearbyEntity.getX(), \n                                mob.getZ() - nearbyEntity.getZ());\n                        }\n                    }\n                }\n            }\n        }\n    }\n
    \n    /**
     * Ranged Attack Goal - Manages projectile attacks and special skills
     */\n    private class HebluRangedAttackGoal extends net.minecraft.world.entity.ai.goal.Goal {\n        private final EntityHeblu heblu;\n        private int attackCooldown;\n        private int seeTime;\n        private final double speedModifier;\n        private final int attackIntervalMin;\n        private final int attackIntervalMax;\n        private final float attackRadius;\n        \n        public HebluRangedAttackGoal() {\n            this.heblu = EntityHeblu.this;\n            this.speedModifier = 1.3;\n            this.attackIntervalMin = 100;\n            this.attackIntervalMax = 140;\n            this.attackRadius = 40.0f;\n            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));\n        }\n        \n        @Override\n        public boolean canUse() {\n            LivingEntity target = heblu.getTarget();\n            return target != null && heblu.canAttack(target, null) && heblu.getSqDistanceTo(target) <= attackRadius * attackRadius;\n        }\n        \n        @Override\n        public boolean canContinueToUse() {\n            return this.canUse() && !heblu.getNavigation().isDone();\n        }\n        \n        @Override\n        public void start() {\n            this.attackCooldown = 0;\n            this.seeTime = 0;\n        }\n        \n        @Override\n        public void stop() {\n            heblu.setAttacking(false);\n        }\n        \n        @Override\n        public void tick() {\n            LivingEntity target = heblu.getTarget();\n            if (target == null) return;\n            \n            double distance = heblu.distanceToSqr(target);\n            boolean canSee = heblu.getSensing().hasLineOfSight(target);\n            \n            if (canSee) {\n                seeTime++;\n            } else {\n                seeTime = 0;\n            }\n            \n            // Move towards or away from target based on distance\n            if (distance > (attackRadius * 0.75) * (attackRadius * 0.75)) {\n                heblu.getNavigation().moveTo(target, speedModifier);\n            } else if (distance < (attackRadius * 0.3) * (attackRadius * 0.3)) {\n                // Back away if too close\n                Vec3 escapeDir = heblu.position().vectorTo(target.position()).normalize().scale(-1);\n                heblu.getNavigation().moveTo(\n                    heblu.getX() + escapeDir.x * 8,\n                    heblu.getY(),\n                    heblu.getZ() + escapeDir.z * 8,\n                    speedModifier\n                );\n            }\n            \n            heblu.lookAt(target, 30.0f, 30.0f);\n            \n            // Attack when ready and have clear line of sight\n            if (--attackCooldown <= 0 && seeTime >= 20) {\n                heblu.setAttacking(true);\n                heblu.performRangedAttack(target, 1.0f);\n                attackCooldown = attackIntervalMin + heblu.random.nextInt(attackIntervalMax - attackIntervalMin);\n                seeTime = 0;\n            }\n        }\n    }\n}
