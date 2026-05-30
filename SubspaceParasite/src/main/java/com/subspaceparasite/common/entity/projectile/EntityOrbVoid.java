package com.subspaceparasite.common.entity.projectile;

import com.subspaceparasite.core.ModEntities;
import com.subspaceparasite.core.ModSounds;
import com.subspaceparasite.core.ModParticleTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * EntityOrbVoid - Void/Gravity Orb Projectile
 * <p>
 * Specialized orb that creates gravitational pull effects,
 * used primarily by Kirin for area control and crowd manipulation.
 * <p>
 * Features:
 * - Gravitational牵引 (pull) effect on nearby entities
 * - Immobilization at close range
 * - AOE damage on explosion
 * - Void-themed particle effects
 * - Multi-target tracking (up to 9 entities)
 */
public class EntityOrbVoid extends EntityOrbBase {
    
    // ========== SynchedEntityData Accessors ==========
    private static final EntityDataAccessor<Integer> DATA_PULL_RADIUS =
            SynchedEntityData.defineId(EntityOrbVoid.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_PULL_STRENGTH =
            SynchedEntityData.defineId(EntityOrbVoid.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> DATA_EXPLODED =
            SynchedEntityData.defineId(EntityOrbVoid.class, EntityDataSerializers.BOOLEAN);
    
    // ========== Core Fields ==========
    private int pullRadius = 40; // blocks
    private float pullStrength = 0.2f;
    private boolean hasExploded = false;
    private int immobilizeTimer = 0;
    private static final int MAX_TRACKED_TARGETS = 9;
    private static final double IMMobilIZE_RANGE = 3.0;
    private static final float EXPLOSION_DAMAGE = 15.0f;
    private static final double EXPLOSION_RADIUS = 6.0;
    
    // ========== Constructor ==========
    
    public EntityOrbVoid(EntityType<? extends EntityOrbVoid> type, Level level) {
        super(type, level);
        this.orbType = 2; // Void orb type
    }
    
    public EntityOrbVoid(Level level, LivingEntity shooter, int powerLevel) {
        super(level, shooter, 2, powerLevel); // Type 2 = Void
        this.pullRadius = 40;
        this.pullStrength = 0.2f + (powerLevel * 0.05f);
        this.damageMultiplier = 1.0F + (powerLevel * 0.3F);
    }
    
    public EntityOrbVoid(Level level, LivingEntity shooter, int radius, float strength, boolean instant) {
        this(level, shooter, 1);
        this.pullRadius = radius;
        this.pullStrength = strength;
        if (instant) {
            this.lifetime = 20; // 1 second before explosion
        }
    }
    
    // ========== Data Registration ==========
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_PULL_RADIUS, 40);
        this.entityData.define(DATA_PULL_STRENGTH, 0.2f);
        this.entityData.define(DATA_EXPLODED, false);
    }
    
    // ========== NBT Persistence ==========
    
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("PullRadius", this.pullRadius);
        compound.putFloat("PullStrength", this.pullStrength);
        compound.putBoolean("HasExploded", this.hasExploded);
    }
    
    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("PullRadius")) {
            this.pullRadius = compound.getInt("PullRadius");
        }
        if (compound.contains("PullStrength")) {
            this.pullStrength = compound.getFloat("PullStrength");
        }
        if (compound.contains("HasExploded")) {
            this.hasExploded = compound.getBoolean("HasExploded");
        }
    }
    
    // ========== Main Tick ==========
    
    @Override
    public void tick() {
        super.tick();
        
        if (hasExploded) {
            this.discard();
            return;
        }
        
        // Server-side gravitational effects
        if (!level().isClientSide && age % 5 == 0) {
            applyGravitationalPull();
        }
        
        // Close-range immobilization
        if (!level().isClientSide) {
            applyImmobilization();
        }
        
        // Client-side void particles
        if (level().isClientSide) {
            spawnVoidParticles();
        }
    }
    
    // ========== Gravitational Pull ==========
    
    private void applyGravitationalPull() {
        // Find nearby living entities
        AABB searchBox = getBoundingBox().inflate(pullRadius);
        List<LivingEntity> nearbyEntities = level().getNearbyEntities(
                LivingEntity.class,
                TargetingConditions.DEFAULT,
                this,
                searchBox
        );
        
        int trackedCount = 0;
        Vec3 orbPos = position();
        
        for (LivingEntity entity : nearbyEntities) {
            if (trackedCount >= MAX_TRACKED_TARGETS) {
                break;
            }
            
            if (entity == getOwner() || entity instanceof EntityOrbBase) {
                continue;
            }
            
            // Calculate pull vector
            Vec3 entityPos = entity.position();
            Vec3 pullVector = orbPos.subtract(entityPos);
            double distance = pullVector.length();
            
            if (distance > 0.5) {
                pullVector = pullVector.normalize();
                
                // Apply pull force (stronger when farther away)
                double force = pullStrength * (1.0 - Math.min(distance / pullRadius, 0.8));
                entity.setDeltaMovement(entity.getDeltaMovement().add(
                        pullVector.x * force,
                        pullVector.y * force * 0.5, // Less vertical pull
                        pullVector.z * force
                ));
                
                trackedCount++;
            }
        }
    }
    
    // ========== Immobilization Effect ==========
    
    private void applyImmobilization() {
        AABB immobilizeBox = getBoundingBox().inflate(IMMOBILIZE_RANGE);
        List<LivingEntity> nearbyEntities = level().getNearbyEntities(
                LivingEntity.class,
                TargetingConditions.DEFAULT,
                this,
                immobilizeBox
        );
        
        for (LivingEntity entity : nearbyEntities) {
            if (entity == getOwner() || entity instanceof EntityOrbBase) {
                continue;
            }
            
            // Apply strong slow/root effect
            Vec3 entityPos = entity.position();
            Vec3 orbPos = position();
            double distance = entityPos.distanceTo(orbPos);
            
            if (distance < IMMOBILIZE_RANGE) {
                // Strong movement dampening
                entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.7, 0.5, 0.7));
                
                // Occasional damage
                if (age % 20 == 0) {
                    entity.hurt(damageSources().magic(), 1.0f);
                }
            }
        }
    }
    
    // ========== Particle Effects ==========
    
    @Override
    protected void spawnChargingParticles() {
        // Void-themed charging particles
        for (int i = 0; i < 5; i++) {
            level().addParticle(ModParticleTypes.VOID_ORB.get(),
                    this.getX() + (random.nextDouble() - 0.5) * getBbWidth(),
                    this.getY() + random.nextDouble() * getBbHeight(),
                    this.getZ() + (random.nextDouble() - 0.5) * getBbWidth(),
                    0.0, 0.03, 0.0);
        }
    }
    
    @Override
    protected void spawnTrailParticles() {
        // Void trail
        for (int i = 0; i < 2; i++) {
            level().addParticle(ParticleTypes.PORTAL,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    (random.nextDouble() - 0.5) * 0.1,
                    random.nextDouble() * 0.1,
                    (random.nextDouble() - 0.5) * 0.1);
        }
    }
    
    private void spawnVoidParticles() {
        // Ambient void particles around the orb
        for (int i = 0; i < 3; i++) {
            double angle = (age * 0.2 + i * (Math.PI * 2 / 3)) % (Math.PI * 2);
            double radius = 0.5;
            double x = this.getX() + Math.cos(angle) * radius;
            double z = this.getZ() + Math.sin(angle) * radius;
            double y = this.getY() + random.nextDouble() * getBbHeight();
            
            level().addParticle(ModParticleTypes.VOID_ORB.get(),
                    x, y, z,
                    0.0, 0.02, 0.0);
        }
    }
    
    @Override
    protected void spawnImpactParticles() {
        // Explosion particles
        for (int i = 0; i < 20; i++) {
            level().addParticle(ParticleTypes.EXPLOSION_EMITTER,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    (random.nextDouble() - 0.5) * 1.0,
                    (random.nextDouble() - 0.5) * 1.0,
                    (random.nextDouble() - 0.5) * 1.0);
        }
        
        // Additional void particles
        for (int i = 0; i < 15; i++) {
            level().addParticle(ModParticleTypes.VOID_ORB.get(),
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    (random.nextDouble() - 0.5) * 0.8,
                    (random.nextDouble() - 0.5) * 0.8,
                    (random.nextDouble() - 0.5) * 0.8);
        }
    }
    
    // ========== Sound Effects ==========
    
    @Override
    protected void playImpactSound() {
        level().playSound(null, this.getX(), this.getY(), this.getZ(),
                ModSounds.PROJECTILE_HIT.get(), SoundSource.HOSTILE,
                1.5F, 0.5F);
    }
    
    // ========== Collision Handling ==========
    
    @Override
    protected void onHit(HitResult result) {
        if (!level().isClientSide && !hasExploded) {
            hasExploded = true;
            entityData.set(DATA_EXPLODED, true);
            
            playImpactSound();
            spawnImpactParticles();
            
            // AOE damage
            applyExplosionDamage();
            
            this.discard();
        }
    }
    
    @Override
    protected void onHitEntity(EntityHitResult result) {
        // Direct hit damage (before explosion)
        Entity target = result.getEntity();
        if (target instanceof LivingEntity livingTarget && !hasExploded) {
            float damage = getCalculatedDamage() * 0.5f;
            target.hurt(damageSources().magic(), damage);
        }
    }
    
    // ========== Explosion Damage ==========
    
    private void applyExplosionDamage() {
        if (level().isClientSide) {
            return;
        }
        
        AABB explosionBox = getBoundingBox().inflate(EXPLOSION_RADIUS);
        List<LivingEntity> affectedEntities = level().getNearbyEntities(
                LivingEntity.class,
                TargetingConditions.DEFAULT,
                this,
                explosionBox
        );
        
        for (LivingEntity entity : affectedEntities) {
            if (entity == getOwner() || entity instanceof EntityOrbBase) {
                continue;
            }
            
            // Calculate damage based on distance
            double distance = entity.position().distanceTo(position());
            double damageFactor = 1.0 - (distance / EXPLOSION_RADIUS);
            
            if (damageFactor > 0) {
                float damage = EXPLOSION_DAMAGE * (float) damageFactor * damageMultiplier;
                entity.hurt(damageSources().magic(), damage);
                
                // Knockback away from orb
                Vec3 knockbackDir = entity.position().subtract(position()).normalize();
                entity.setDeltaMovement(entity.getDeltaMovement().add(
                        knockbackDir.x * 0.8,
                        0.4,
                        knockbackDir.z * 0.8
                ));
            }
        }
    }
    
    // ========== Getters and Setters ==========
    
    public int getPullRadius() {
        return this.entityData.get(DATA_PULL_RADIUS);
    }
    
    public void setPullRadius(int radius) {
        this.entityData.set(DATA_PULL_RADIUS, radius);
        this.pullRadius = radius;
    }
    
    public float getPullStrength() {
        return this.entityData.get(DATA_PULL_STRENGTH);
    }
    
    public void setPullStrength(float strength) {
        this.entityData.set(DATA_PULL_STRENGTH, strength);
        this.pullStrength = strength;
    }
    
    public boolean hasExploded() {
        return this.entityData.get(DATA_EXPLODED);
    }
    
    // ========== Base Damage ==========
    
    @Override
    protected float getBaseDamage() {
        return 8.0f;
    }
}
