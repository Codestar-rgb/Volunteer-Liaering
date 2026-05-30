package com.subspaceparasite.common.entity.projectile;

import com.subspaceparasite.common.entity.ai.misc.EntityCanHaveBodies;
import com.subspaceparasite.core.ModEntities;
import com.subspaceparasite.core.ModParticleTypes;
import com.subspaceparasite.core.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

/**
 * Void Orb - Gravitational-type orbital projectile used by Kirin and other advanced parasites.
 * <p>
 * Behavior:
 * - Creates a gravitational pull effect on nearby entities
 * - Traps up to 9 targets, pulling them toward the orb center
 * - Explodes after fuse time dealing massive AOE damage
 * - Features void-themed particle effects
 * 
 * Architecture:
 * - Extends EntityOrbBase for core projectile functionality
 * - Implements entity tracking system for multiple targets
 * - Supports gravitational physics calculations
 * - Optimized for performance with efficient entity filtering
 */
public class EntityOrbVoid extends EntityOrbBase {
    
    // ========== SynchedEntityData Accessors ===========
    
    private static final EntityDataAccessor<Integer> DATA_FUSE_STATE =
            SynchedEntityData.defineId(EntityOrbVoid.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_WAIT_START =
            SynchedEntityData.defineId(EntityOrbVoid.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_SELFE_STATE =
            SynchedEntityData.defineId(EntityOrbVoid.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_PARENT_ID =
            SynchedEntityData.defineId(EntityOrbVoid.class, EntityDataSerializers.INT);
    
    // Target tracking data parameters (up to 9 entities)
    private static final EntityDataAccessor<Integer> DATA_TARGET_1 =
            SynchedEntityData.defineId(EntityOrbVoid.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TARGET_2 =
            SynchedEntityData.defineId(EntityOrbVoid.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TARGET_3 =
            SynchedEntityData.defineId(EntityOrbVoid.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TARGET_4 =
            SynchedEntityData.defineId(EntityOrbVoid.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TARGET_5 =
            SynchedEntityData.defineId(EntityOrbVoid.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TARGET_6 =
            SynchedEntityData.defineId(EntityOrbVoid.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TARGET_7 =
            SynchedEntityData.defineId(EntityOrbVoid.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TARGET_8 =
            SynchedEntityData.defineId(EntityOrbVoid.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TARGET_9 =
            SynchedEntityData.defineId(EntityOrbVoid.class, EntityDataSerializers.INT);
    
    // ========== Core Fields ===========
    
    private LivingEntity parent;
    private boolean followParent = false;
    private double parentOffsetY = 0.8;
    private int timerDeath = 0;
    private int selfeState = 0;
    private int fuseState = 7;
    private int waitStart = 40;
    private float baseDamage = 10.0F;
    private int pullRadius = 40;
    private double pullStrength = 0.2;
    private List<EntityDataAccessor<Integer>> targetTrackers = new ArrayList<>();
    
    // ========== Constructors ===========
    
    public EntityOrbVoid(EntityType<? extends EntityOrbVoid> type, Level level) {
        super(type, level);
        this.fuseState = 7;
        this.waitStart = 40;
        this.setFuseState(fuseState);
        this.setWaitStart(waitStart);
        this.setSelfeState(0);
        initTargetTrackers();
    }
    
    public EntityOrbVoid(Level level, LivingEntity parent, int fuse, int waitStart) {
        this((EntityType<? extends EntityOrbVoid>) ModEntities.PROJECTILE_ORB_VOID.get(), level);
        this.parent = parent;
        this.followParent = true;
        this.fuseState = fuse;
        this.waitStart = waitStart;
        this.setFuseState(fuse);
        this.setWaitStart(waitStart);
        this.setPos(parent.getX(), parent.getEyeY() + 0.5, parent.getZ());
        this.setParentId(parent.getId());
    }
    
    public EntityOrbVoid(Level level, LivingEntity parent, int fuse, int waitStart, boolean stayWithParent) {
        this(level, parent, fuse, waitStart);
        this.followParent = stayWithParent;
    }
    
    // ========== Initialization ===========
    
    private void initTargetTrackers() {
        targetTrackers.add(DATA_TARGET_1);
        targetTrackers.add(DATA_TARGET_2);
        targetTrackers.add(DATA_TARGET_3);
        targetTrackers.add(DATA_TARGET_4);
        targetTrackers.add(DATA_TARGET_5);
        targetTrackers.add(DATA_TARGET_6);
        targetTrackers.add(DATA_TARGET_7);
        targetTrackers.add(DATA_TARGET_8);
        targetTrackers.add(DATA_TARGET_9);
    }
    
    // ========== Data Registration ===========
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FUSE_STATE, -1);
        this.entityData.define(DATA_WAIT_START, -1);
        this.entityData.define(DATA_SELFE_STATE, -1);
        this.entityData.define(DATA_PARENT_ID, -1);
        
        // Initialize all target trackers
        for (EntityDataAccessor<Integer> tracker : targetTrackers) {
            this.entityData.define(tracker, 0);
        }
    }
    
    // ========== NBT Persistence ===========
    
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("FuseState", this.fuseState);
        compound.putInt("WaitStart", this.waitStart);
        compound.putInt("SelfeState", this.selfeState);
        compound.putInt("TimerDeath", this.timerDeath);
        compound.putBoolean("FollowParent", this.followParent);
        compound.putInt("PullRadius", this.pullRadius);
        compound.putDouble("PullStrength", this.pullStrength);
        if (this.parent != null) {
            compound.putInt("ParentId", this.parent.getId());
        }
    }
    
    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("FuseState")) {
            this.fuseState = compound.getInt("FuseState");
        }
        if (compound.contains("WaitStart")) {
            this.waitStart = compound.getInt("WaitStart");
        }
        if (compound.contains("SelfeState")) {
            this.selfeState = compound.getInt("SelfeState");
        }
        if (compound.contains("TimerDeath")) {
            this.timerDeath = compound.getInt("TimerDeath");
        }
        if (compound.contains("FollowParent")) {
            this.followParent = compound.getBoolean("FollowParent");
        }
        if (compound.contains("PullRadius")) {
            this.pullRadius = compound.getInt("PullRadius");
        }
        if (compound.contains("PullStrength")) {
            this.pullStrength = compound.getDouble("PullStrength");
        }
    }
    
    // ========== Main Tick ===========
    
    @Override
    public void tick() {
        super.tick();
        
        int age = this.getAge();
        
        // Handle post-waitStart behavior
        if (age > this.getWaitStart()) {
            this.orbDoing();
            this.setSelfeState(1);
            this.dyingBurst(true, 1);
            
            if (level().isClientSide) {
                // Spawn void particles
                spawnVoidParticles();
                
                // Sync rotation from parent
                if (this.parent != null && parent.isAlive()) {
                    this.setYRot(parent.yRotO);
                    this.setXRot(parent.xRotO);
                }
                spawnOrbEffects(4);
                return;
            }
            
            // Server-side position tracking
            if (parent != null && parent.isAlive() && followParent) {
                this.setPos(parent.getX(), parent.getY() + parent.getBbHeight() + parentOffsetY, parent.getZ());
            } else if (followParent) {
                this.discard();
            }
        } else {
            // Pre-waitStart behavior
            if (level().isClientSide) {
                spawnOrbEffects(4);
                return;
            }
            
            if (parent != null && parent.isAlive() && followParent) {
                this.setPos(parent.getX(), parent.getY() + parent.getBbHeight() + parentOffsetY, parent.getZ());
            } else if (followParent) {
                this.discard();
            }
        }
    }
    
    // ========== Collision Handling ===========
    
    @Override
    protected void onHitEntity(EntityHitResult result) {
        // Void orbs don't deal direct damage on impact
        // They trap and pull entities instead
    }
    
    @Override
    protected void onHitBlock(HitResult result) {
        // No special block interaction
    }
    
    // ========== Orb Mechanics ===========
    
    /**
     * Handles the dying burst explosion sequence
     */
    protected void dyingBurst(boolean fromDeath, int value) {
        int currentState = this.getSelfeState();
        this.timerDeath += currentState * value;
        
        if (this.timerDeath < 0) {
            this.timerDeath = 0;
        }
        
        if (this.timerDeath >= this.getFuseState()) {
            this.timerDeath = this.getFuseState();
            selfExplode();
        } else {
            // Scale up during charging
            float scale = 1.0F + (timerDeath / (float) fuseState) * 0.8F;
            this.refreshDimensions();
        }
    }
    
    /**
     * Self-destruct explosion with massive AOE damage
     */
    protected void selfExplode() {
        this.setSelfeState(2);
        
        if (this.getSelfeState() == 2) {
            timerDeath++;
            
            if (timerDeath > 80) {
                // Shrink effect
                float newScale = Math.max(0.1F, this.getBbWidth() - 0.8F);
                
                if (!level().isClientSide) {
                    if (parent != null) {
                        float radius = this.getBbWidth() / 2.0F;
                        float height = this.getBbHeight();
                        
                        // Get entities in AOE
                        List<LivingEntity> entities = level().getEntitiesOfClass(
                                LivingEntity.class,
                                this.getBoundingBox().inflate(radius),
                                e -> !(e instanceof EntityOrbBase)
                        );
                        
                        for (LivingEntity mob : entities) {
                            float damage = parent instanceof EntityCanHaveBodies canHaveBodies 
                                    ? canHaveBodies.getMiniDamage() * 5.0F 
                                    : baseDamage * 5.0F;
                            mob.hurt(mob.damageSources().explosion(this, parent), damage);
                        }
                    }
                } else {
                    // Client-side explosion particles
                    spawnExplosionParticles();
                }
                
                // Play explosion sound
                level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        ModSounds.ORB_EXPLODE.get(), SoundSource.HOSTILE,
                        1.0F, 1.0F);
                
                if (timerDeath > 90) {
                    this.discard();
                }
            }
        }
    }
    
    /**
     * Main orb behavior - pulls targeted entities toward center
     */
    private void orbDoing() {
        // Pull all tracked entities
        for (LivingEntity target : getTargetedEntityVictims()) {
            pullEntity(target);
        }
        
        if (level().isClientSide) {
            return;
        }
        
        float radius = this.getBbWidth() / 2.0F;
        float height = this.getBbHeight();
        
        // Reset and acquire new targets
        resetTargetedEntity();
        
        // Search for entities within extended radius
        List<LivingEntity> entities = level().getEntitiesOfClass(
                LivingEntity.class,
                this.getBoundingBox().inflate(pullRadius),
                e -> !(e instanceof EntityOrbBase) && !(e instanceof com.subspaceparasite.api.IParasite)
        );
        
        for (LivingEntity target : entities) {
            // Skip parent entity
            if (this.parent == target) continue;
            
            // Try to track this entity
            if (setTargetedEntity(target)) {
                pullEntity(target);
            }
        }
    }
    
    /**
     * Pulls an entity toward the orb center with gravitational force
     */
    public void pullEntity(LivingEntity target) {
        double distanceSq = target.distanceToSqr(this);
        target.resetPosToBB();
        
        if (distanceSq < 4.0) {
            // Entity is at center - immobilize
            target.setPos(this.getX(), this.getY(), this.getZ());
            target.setDeltaMovement(0.0, 0.0, 0.0);
        } else {
            // Apply gravitational pull
            double deltaX = this.getX() - target.getX();
            double deltaY = this.getY() - target.getY();
            double deltaZ = this.getZ() - target.getZ();
            double distance = Math.sqrt(distanceSq);
            
            double pullX = (deltaX / distance) * pullStrength;
            double pullY = (deltaY / distance) * pullStrength;
            double pullZ = (deltaZ / distance) * pullStrength;
            
            target.push(pullX, pullY, pullZ);
        }
    }
    
    // ========== Target Tracking System ===========
    
    /**
     * Gets all currently tracked living entities
     */
    @OnlyIn(Dist.CLIENT)
    public List<LivingEntity> getTargetedEntityVictims() {
        List<LivingEntity> victims = new ArrayList<>();
        
        for (EntityDataAccessor<Integer> tracker : targetTrackers) {
            int entityId = this.entityData.get(tracker);
            if (entityId > 0) {
                Entity entity = level().getEntity(entityId);
                if (entity instanceof LivingEntity livingEntity && entity.isAlive()) {
                    victims.add(livingEntity);
                }
            }
        }
        
        return victims;
    }
    
    /**
     * Sets an entity as a tracked target in the first available slot
     */
    public boolean setTargetedEntity(LivingEntity target) {
        for (EntityDataAccessor<Integer> tracker : targetTrackers) {
            int currentId = this.entityData.get(tracker);
            if (currentId <= 0) {
                this.entityData.set(tracker, target.getId());
                return true;
            }
        }
        return false;
    }
    
    /**
     * Resets all target tracking slots
     */
    public void resetTargetedEntity() {
        for (EntityDataAccessor<Integer> tracker : targetTrackers) {
            this.entityData.set(tracker, 0);
        }
    }
    
    // ========== Particle Effects ===========
    
    @Override
    @OnlyIn(Dist.CLIENT)
    protected void spawnChargingParticles() {
        spawnVoidParticles();
    }
    
    @OnlyIn(Dist.CLIENT)
    protected void spawnVoidParticles() {
        for (int i = 0; i < 4; i++) {
            level().addParticle(ModParticleTypes.VOID_ORB.get(),
                    this.getX() + (random.nextDouble() - 0.5) * getBbWidth() * 3.0,
                    this.getY() + random.nextDouble() * getBbHeight() - 0.25,
                    this.getZ() + (random.nextDouble() - 0.5) * getBbWidth() * 3.0,
                    (random.nextDouble() - 0.5) * 2.0,
                    -random.nextDouble(),
                    (random.nextDouble() - 0.5) * 2.0);
        }
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    protected void spawnTrailParticles() {
        for (int i = 0; i < 3; i++) {
            level().addParticle(ParticleTypes.PORTAL,
                    this.getX() + (random.nextDouble() - 0.5) * 0.5,
                    this.getY() + random.nextDouble() * 0.5,
                    this.getZ() + (random.nextDouble() - 0.5) * 0.5,
                    0.0, 0.0, 0.0);
        }
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    protected void spawnImpactParticles() {
        spawnExplosionParticles();
    }
    
    @OnlyIn(Dist.CLIENT)
    protected void spawnExplosionParticles() {
        int particleCount = this.getFuseState() + this.getFuseState() / 2;
        for (int i = 0; i < particleCount; i++) {
            level().addParticle(ParticleTypes.PORTAL,
                    this.getX() + (random.nextDouble() - 0.5) * getBbWidth() * 2.0,
                    this.getY() + random.nextDouble() * getBbHeight() * 2.0,
                    this.getZ() + (random.nextDouble() - 0.5) * getBbWidth() * 2.0,
                    random.nextGaussian(),
                    0.0,
                    random.nextGaussian(),
                    0, 0, 0);
        }
    }
    
    @OnlyIn(Dist.CLIENT)
    public void spawnOrbEffects(int cap) {
        for (int i = -cap; i <= cap; i++) {
            for (int j = -cap; j <= cap; j++) {
                if (i > -2 && i < 2 && j == -1) {
                    j = 2;
                }
                if (random.nextInt(16) != 0) continue;
                
                for (int k = 0; k <= 5; k++) {
                    level().addParticle(ModParticleTypes.VOID_ORB.get(),
                            this.getX() + (random.nextDouble() - 0.5) * getBbWidth() * 2.0,
                            this.getY() + random.nextDouble() * getBbHeight() * 2.0,
                            this.getZ() + (random.nextDouble() - 0.5) * getBbWidth() * 2.0,
                            (i + random.nextFloat()) - 0.5,
                            k - random.nextFloat() - 1.0F,
                            (j + random.nextFloat()) - 0.5);
                }
            }
        }
    }
    
    // ========== Sound Effects ===========
    
    @Override
    protected void playImpactSound() {
        level().playSound(null, this.getX(), this.getY(), this.getZ(),
                ModSounds.ORB_EXPLODE.get(), SoundSource.HOSTILE,
                1.0F, 1.0F);
    }
    
    // ========== Getters and Setters ===========
    
    public int getFuseState() {
        Integer val = this.entityData.get(DATA_FUSE_STATE);
        return val != null && val != -1 ? val : this.fuseState;
    }
    
    public void setFuseState(int state) {
        this.entityData.set(DATA_FUSE_STATE, state);
        this.fuseState = state;
    }
    
    public int getWaitStart() {
        Integer val = this.entityData.get(DATA_WAIT_START);
        return val != null && val != -1 ? val : this.waitStart;
    }
    
    public void setWaitStart(int state) {
        this.entityData.set(DATA_WAIT_START, state);
        this.waitStart = state;
    }
    
    public int getSelfeState() {
        Integer val = this.entityData.get(DATA_SELFE_STATE);
        return val != null && val != -1 ? val : this.selfeState;
    }
    
    public void setSelfeState(int state) {
        this.entityData.set(DATA_SELFE_STATE, state);
        this.selfeState = state;
    }
    
    public int getParentId() {
        return this.entityData.get(DATA_PARENT_ID);
    }
    
    public void setParentId(int id) {
        this.entityData.set(DATA_PARENT_ID, id);
    }
    
    public LivingEntity getParent() {
        return this.parent;
    }
    
    public void setParent(LivingEntity parent) {
        this.parent = parent;
        this.setParentId(parent.getId());
    }
    
    public boolean isFollowingParent() {
        return followParent;
    }
    
    public void setFollowingParent(boolean follow) {
        this.followParent = follow;
    }
    
    public int getPullRadius() {
        return pullRadius;
    }
    
    public void setPullRadius(int radius) {
        this.pullRadius = radius;
    }
    
    public double getPullStrength() {
        return pullStrength;
    }
    
    public void setPullStrength(double strength) {
        this.pullStrength = strength;
    }
    
    @Override
    protected float getBaseDamage() {
        return baseDamage;
    }
    
    public void setBaseDamage(float damage) {
        this.baseDamage = damage;
    }
    
    @Override
    public boolean isPickable() {
        return true;
    }
    
    @Override
    public boolean isPushable() {
        return false;
    }
}
