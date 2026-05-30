package com.subspaceparasite.common.entity.projectile;

import com.subspaceparasite.common.entity.ai.misc.EntityCanHaveBodies;
import com.subspaceparasite.core.ModEntities;
import com.subspaceparasite.core.ModParticleTypes;
import com.subspaceparasite.core.ModSounds;
import net.minecraft.core.particles.ParticleOptions;
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

import java.util.List;

/**
 * Scary Orb - Attack-type orbital projectile used by Heblu and other derived parasites.
 * <p>
 * Behavior:
 * - Orbits around the parent entity, providing area denial
 * - Applies debuffs to nearby enemies (cooldown suppression)
 * - Explodes on command dealing significant AOE damage
 * - Features charging state with visual/audio feedback
 * 
 * Architecture:
 * - Extends EntityOrbBase for core projectile functionality
 * - Implements synchronized data for fuse, state, and targeting
 * - Supports particle effects and sound integration
 * - Designed for high performance with optimized collision detection
 */
public class EntityOrbScary extends EntityOrbBase {
    
    // ========== SynchedEntityData Accessors ===========
    
    private static final EntityDataAccessor<Integer> DATA_FUSE_STATE =
            SynchedEntityData.defineId(EntityOrbScary.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_WAIT_START =
            SynchedEntityData.defineId(EntityOrbScary.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_SELFE_STATE =
            SynchedEntityData.defineId(EntityOrbScary.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_PARENT_ID =
            SynchedEntityData.defineId(EntityOrbScary.class, EntityDataSerializers.INT);
    
    // ========== Core Fields ===========
    
    private LivingEntity parent;
    private boolean followParent = false;
    private double parentOffsetX = 0;
    private double parentOffsetY = 0.5;
    private double parentOffsetZ = 0;
    private int timerDeath = 0;
    private int selfeState = 0;
    private int fuseState = 7;
    private int waitStart = 40;
    private float baseDamage = 8.0F;
    
    // ========== Constructors ===========
    
    public EntityOrbScary(EntityType<? extends EntityOrbScary> type, Level level) {
        super(type, level);
        this.fuseState = 7;
        this.waitStart = 40;
        this.setFuseState(fuseState);
        this.setWaitStart(waitStart);
        this.setSelfeState(0);
    }
    
    public EntityOrbScary(Level level, LivingEntity parent, int fuse, int waitStart) {
        this((EntityType<? extends EntityOrbScary>) ModEntities.PROJECTILE_ORB_SCARY.get(), level);
        this.parent = parent;
        this.followParent = true;
        this.fuseState = fuse;
        this.waitStart = waitStart;
        this.setFuseState(fuse);
        this.setWaitStart(waitStart);
        this.setPos(parent.getX(), parent.getEyeY() - 0.1, parent.getZ());
        this.setParentId(parent.getId());
    }
    
    public EntityOrbScary(Level level, LivingEntity parent, int fuse, int waitStart, boolean stayWithParent) {
        this(level, parent, fuse, waitStart);
        this.followParent = stayWithParent;
    }
    
    // ========== Data Registration ===========
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FUSE_STATE, -1);
        this.entityData.define(DATA_WAIT_START, -1);
        this.entityData.define(DATA_SELFE_STATE, -1);
        this.entityData.define(DATA_PARENT_ID, -1);
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
    }
    
    // ========== Main Tick ===========
    
    @Override
    public void tick() {
        super.tick();
        
        int age = this.getAge();
        
        // Play summon sounds on first tick
        if (age == 1 && !level().isClientSide) {
            level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    ModSounds.ORB_SUMMON.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
            level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    ModSounds.ORB_IDLE.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
        }
        
        // Handle post-waitStart behavior
        if (age > this.getWaitStart()) {
            this.setSelfeState(1);
            this.dyingBurst(true, 1);
            
            if (level().isClientSide) {
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
                this.setPos(parent.getX(), parent.getY() + parent.getBbHeight() - 0.1, parent.getZ());
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
                this.setPos(parent.getX(), parent.getY() + parent.getBbHeight() + 0.5, parent.getZ());
            } else if (followParent) {
                this.discard();
            }
        }
        
        // Apply orb effects periodically
        if (!level().isClientSide && age % 10 == 0) {
            applyOrbEffect();
        }
    }
    
    // ========== Collision Handling ===========
    
    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (parent instanceof EntityCanHaveBodies canHaveBodies) {
            Entity target = result.getEntity();
            if (target instanceof LivingEntity livingTarget && !(target instanceof EntityOrbBase)) {
                float damage = getCalculatedDamage();
                target.hurt(target.damageSources().mobAttack(parent), damage);
                
                // Apply additional scary orb effect
                canHaveBodies.scaryOrbEffect(livingTarget, 1);
            }
        }
    }
    
    @Override
    protected void onHitBlock(HitResult result) {
        // No special block interaction for scary orb
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
     * Self-destruct explosion with AOE damage
     */
    protected void selfExplode() {
        this.setSelfeState(2);
        
        if (this.getSelfeState() == 2) {
            timerDeath++;
            
            if (timerDeath > 35) {
                // Shrink effect
                float newScale = Math.max(0.1F, this.getBbWidth() - 0.8F);
                // Note: Dimension refresh handled by engine
                
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
                        1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
                
                if (timerDeath > 45) {
                    this.discard();
                }
            }
        }
    }
    
    /**
     * Applies scary orb area effect to nearby entities
     */
    private void applyOrbEffect() {
        float radius = this.getBbWidth() / 2.0F;
        float height = this.getBbHeight();
        
        List<LivingEntity> entities = level().getEntitiesOfClass(
                LivingEntity.class,
                this.getBoundingBox().inflate(radius),
                e -> !(e instanceof EntityOrbBase)
        );
        
        if (parent instanceof EntityCanHaveBodies canHaveBodies) {
            for (LivingEntity mob : entities) {
                canHaveBodies.scaryOrbEffect(mob, entities.size());
            }
        }
    }
    
    // ========== Particle Effects ===========
    
    @Override
    @OnlyIn(Dist.CLIENT)
    protected void spawnChargingParticles() {
        for (int i = 0; i < 5; i++) {
            level().addParticle(ModParticleTypes.VOID_ORB.get(),
                    this.getX() + (random.nextDouble() - 0.5) * getBbWidth(),
                    this.getY() + random.nextDouble() * getBbHeight(),
                    this.getZ() + (random.nextDouble() - 0.5) * getBbWidth(),
                    0.0, 0.02, 0.0);
        }
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    protected void spawnTrailParticles() {
        for (int i = 0; i < 2; i++) {
            level().addParticle(ParticleTypes.SMOKE,
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
            level().addParticle(ParticleTypes.EXPLOSION_LARGE,
                    this.getX() + (random.nextDouble() - 0.5) * getBbWidth() * 2.0,
                    this.getY() + random.nextDouble() * getBbHeight() * 2.0,
                    this.getZ() + (random.nextDouble() - 0.5) * getBbWidth() * 2.0,
                    random.nextGaussian() * 0.5,
                    random.nextGaussian() * 0.5,
                    random.nextGaussian() * 0.5);
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
