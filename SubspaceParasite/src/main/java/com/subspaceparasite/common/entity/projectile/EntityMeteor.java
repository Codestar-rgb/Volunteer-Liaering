package com.subspaceparasite.common.entity.projectile;

import com.subspaceparasite.core.ModEntities;
import com.subspaceparasite.core.ModSounds;
import com.subspaceparasite.core.ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
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
 * EntityMeteor - Meteor Projectile Entity
 * <p>
 * High-velocity celestial projectile launched during celestial events.
 * Creates massive explosion on impact with area damage and terrain destruction.
 * <p>
 * Features:
 * - High-speed descent trajectory
 * - Massive AOE explosion damage
 * - Terrain crater creation
 * - Fire effect on impact
 * - Celestial-themed particles
 */
public class EntityMeteor extends EntityOrbBase {
    
    // ========== SynchedEntityData Accessors ==========
    private static final EntityDataAccessor<Integer> DATA_METEOR_SIZE =
            SynchedEntityData.defineId(EntityMeteor.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_EXPLODED =
            SynchedEntityData.defineId(EntityMeteor.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_IMPACT_TIMER =
            SynchedEntityData.defineId(EntityMeteor.class, EntityDataSerializers.INT);
    
    // ========== Core Fields ==========
    private int meteorSize = 3; // 1=small, 2=medium, 3=large
    private boolean hasExploded = false;
    private int impactTimer = 0;
    private static final float BASE_DAMAGE_SMALL = 20.0f;
    private static final float BASE_DAMAGE_MEDIUM = 40.0f;
    private static final float BASE_DAMAGE_LARGE = 80.0f;
    private static final double EXPLOSION_RADIUS_SMALL = 4.0;
    private static final double EXPLOSION_RADIUS_MEDIUM = 7.0;
    private static final double EXPLOSION_RADIUS_LARGE = 12.0;
    private static final int FIRE_DURATION = 100; // ticks
    
    // ========== Constructor ==========
    
    public EntityMeteor(EntityType<? extends EntityMeteor> type, Level level) {
        super(type, level);
        this.orbType = 3; // Meteor orb type
    }
    
    public EntityMeteor(Level level, LivingEntity shooter, int size) {
        super(level, shooter, 3, size); // Type 3 = Meteor
        this.meteorSize = size;
        this.damageMultiplier = 1.0F + (size * 0.5F);
        
        // Set velocity for downward trajectory
        Vec3 motion = new Vec3(0, -2.5, 0);
        this.setDeltaMovement(motion);
    }
    
    public EntityMeteor(Level level, double x, double y, double z, int size) {
        this((EntityType<? extends EntityMeteor>) ModEntities.PROJECTILE_METEOR.get(), level);
        this.setPos(x, y, z);
        this.meteorSize = size;
        this.damageMultiplier = 1.0F + (size * 0.5F);
        
        Vec3 motion = new Vec3(0, -3.0, 0);
        this.setDeltaMovement(motion);
    }
    
    // ========== Data Registration ==========
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_METEOR_SIZE, 3);
        this.entityData.define(DATA_EXPLODED, false);
        this.entityData.define(DATA_IMPACT_TIMER, 0);
    }
    
    // ========== NBT Persistence ==========
    
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("MeteorSize", this.meteorSize);
        compound.putBoolean("HasExploded", this.hasExploded);
        compound.putInt("ImpactTimer", this.impactTimer);
    }
    
    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("MeteorSize")) {
            this.meteorSize = compound.getInt("MeteorSize");
        }
        if (compound.contains("HasExploded")) {
            this.hasExploded = compound.getBoolean("HasExploded");
        }
        if (compound.contains("ImpactTimer")) {
            this.impactTimer = compound.getInt("ImpactTimer");
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
        
        impactTimer++;
        
        // Server-side effects
        if (!level().isClientSide) {
            // Check for impact
            if (this.onGround() || impactTimer > 200) {
                explode();
            }
        }
        
        // Client-side particles
        if (level().isClientSide) {
            spawnMeteorParticles();
        }
    }
    
    // ========== Explosion Logic ==========
    
    private void explode() {
        if (level().isClientSide || hasExploded) {
            return;
        }
        
        hasExploded = true;
        entityData.set(DATA_EXPLODED, true);
        
        // Play explosion sound
        playImpactSound();
        
        // Spawn explosion particles
        spawnImpactParticles();
        
        // Apply AOE damage
        applyExplosionDamage();
        
        // Create fire effects
        createFireEffects();
        
        // Create crater (terrain deformation)
        createCrater();
        
        this.discard();
    }
    
    private void applyExplosionDamage() {
        double explosionRadius = getExplosionRadius();
        AABB explosionBox = getBoundingBox().inflate(explosionRadius);
        
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
            double damageFactor = 1.0 - (distance / explosionRadius);
            
            if (damageFactor > 0) {
                float baseDamage = getBaseDamage();
                float damage = baseDamage * (float) damageFactor * damageMultiplier;
                entity.hurt(damageSources().explosion(this, getOwner()), damage);
                
                // Strong knockback away from impact point
                Vec3 knockbackDir = entity.position().subtract(position()).normalize();
                double knockbackStrength = 2.0 + (meteorSize * 0.5);
                entity.setDeltaMovement(entity.getDeltaMovement().add(
                        knockbackDir.x * knockbackStrength,
                        knockbackStrength * 0.5,
                        knockbackDir.z * knockbackStrength
                ));
                
                // Set on fire
                entity.setSecondsOnFire(FIRE_DURATION / 20);
            }
        }
    }
    
    private void createFireEffects() {
        if (level().isClientSide) {
            return;
        }
        
        double fireRadius = getExplosionRadius() * 0.7;
        int fireCount = meteorSize * 5;
        
        for (int i = 0; i < fireCount; i++) {
            double angle = (i / (double) fireCount) * Math.PI * 2;
            double radius = random.nextDouble() * fireRadius;
            double x = getX() + Math.cos(angle) * radius;
            double z = getZ() + Math.sin(angle) * radius;
            double y = getY();
            
            BlockPos firePos = new BlockPos((int) x, (int) y, (int) z);
            
            // Try to set fire at position
            if (level().getBlockState(firePos).isAir() && 
                !level().getBlockState(firePos.below()).isAir()) {
                // Would need block interaction here
                // For now, just spawn fire particles
                spawnFireParticles(x, y + 1, z);
            }
        }
    }
    
    private void spawnFireParticles(double x, double y, double z) {
        for (int i = 0; i < 5; i++) {
            level().addParticle(ParticleTypes.FLAME,
                    x + (random.nextDouble() - 0.5) * 0.5,
                    y + random.nextDouble() * 0.5,
                    z + (random.nextDouble() - 0.5) * 0.5,
                    0.0, 0.02, 0.0);
        }
    }
    
    private void createCrater() {
        if (level().isClientSide) {
            return;
        }
        
        double craterRadius = getExplosionRadius() * 0.5;
        int craterDepth = meteorSize;
        
        // Simplified crater creation - would need proper block manipulation
        // This is a placeholder for full terrain deformation
        BlockPos impactPos = new BlockPos((int) getX(), (int) getY(), (int) getZ());
        
        // Send crater creation data to clients
        // Actual block breaking would happen server-side
    }
    
    // ========== Particle Effects ==========
    
    private void spawnMeteorParticles() {
        // Trail particles during descent
        for (int i = 0; i < 8; i++) {
            level().addParticle(ModParticleTypes.VOID_ORB.get(),
                    this.getX() + (random.nextDouble() - 0.5) * getBbWidth(),
                    this.getY() + random.nextDouble() * getBbHeight(),
                    this.getZ() + (random.nextDouble() - 0.5) * getBbWidth(),
                    (random.nextDouble() - 0.5) * 0.2,
                    0.3,
                    (random.nextDouble() - 0.5) * 0.2);
        }
        
        // Smoke trail
        for (int i = 0; i < 4; i++) {
            level().addParticle(ParticleTypes.SMOKE,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    0.0, 0.1, 0.0);
        }
    }
    
    @Override
    protected void spawnChargingParticles() {
        // Glowing hot particles
        for (int i = 0; i < 6; i++) {
            level().addParticle(ParticleTypes.FLAME,
                    this.getX() + (random.nextDouble() - 0.5) * getBbWidth(),
                    this.getY() + random.nextDouble() * getBbHeight(),
                    this.getZ() + (random.nextDouble() - 0.5) * getBbWidth(),
                    0.0, 0.05, 0.0);
        }
    }
    
    @Override
    protected void spawnTrailParticles() {
        // Already handled in spawnMeteorParticles
    }
    
    @Override
    protected void spawnImpactParticles() {
        // Massive explosion particles
        int particleCount = 50 + (meteorSize * 20);
        
        for (int i = 0; i < particleCount; i++) {
            double speed = 2.0 + random.nextDouble() * 3.0;
            level().addParticle(ParticleTypes.EXPLOSION_EMITTER,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    (random.nextDouble() - 0.5) * speed,
                    (random.nextDouble() - 0.5) * speed,
                    (random.nextDouble() - 0.5) * speed);
        }
        
        // Additional fire particles
        for (int i = 0; i < 30; i++) {
            level().addParticle(ParticleTypes.FLAME,
                    this.getX() + (random.nextDouble() - 0.5) * 5,
                    this.getY() + (random.nextDouble() - 0.5) * 5,
                    this.getZ() + (random.nextDouble() - 0.5) * 5,
                    (random.nextDouble() - 0.5) * 0.5,
                    (random.nextDouble() - 0.5) * 0.5,
                    (random.nextDouble() - 0.5) * 0.5);
        }
        
        // Void/celestial particles
        for (int i = 0; i < 20; i++) {
            level().addParticle(ModParticleTypes.VOID_ORB.get(),
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    (random.nextDouble() - 0.5) * 1.5,
                    (random.nextDouble() - 0.5) * 1.5,
                    (random.nextDouble() - 0.5) * 1.5);
        }
    }
    
    // ========== Sound Effects ==========
    
    @Override
    protected void playImpactSound() {
        level().playSound(null, this.getX(), this.getY(), this.getZ(),
                ModSounds.METEOR_IMPACT.get(), SoundSource.HOSTILE,
                2.0F, 0.8F);
    }
    
    // ========== Collision Handling ==========
    
    @Override
    protected void onHit(HitResult result) {
        if (!level().isClientSide && !hasExploded) {
            explode();
        }
    }
    
    @Override
    protected void onHitEntity(EntityHitResult result) {
        // Direct hit before explosion
        if (!hasExploded && result.getEntity() instanceof LivingEntity livingTarget) {
            float damage = getBaseDamage() * 0.3f * damageMultiplier;
            livingTarget.hurt(damageSources().fall(), damage);
        }
    }
    
    // ========== Getters and Setters ==========
    
    public int getMeteorSize() {
        return this.entityData.get(DATA_METEOR_SIZE);
    }
    
    public void setMeteorSize(int size) {
        this.entityData.set(DATA_METEOR_SIZE, size);
        this.meteorSize = size;
    }
    
    public boolean hasExploded() {
        return this.entityData.get(DATA_EXPLODED);
    }
    
    // ========== Utility Methods ==========
    
    private float getBaseDamage() {
        return switch (meteorSize) {
            case 1 -> BASE_DAMAGE_SMALL;
            case 2 -> BASE_DAMAGE_MEDIUM;
            default -> BASE_DAMAGE_LARGE;
        };
    }
    
    private double getExplosionRadius() {
        return switch (meteorSize) {
            case 1 -> EXPLOSION_RADIUS_SMALL;
            case 2 -> EXPLOSION_RADIUS_MEDIUM;
            default -> EXPLOSION_RADIUS_LARGE;
        };
    }
    
    @Override
    protected float getBaseDamage() {
        return getBaseDamage();
    }
}
