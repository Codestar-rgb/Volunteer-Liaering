package com.subspaceparasite.common.entity.projectile;

import com.subspaceparasite.core.ModSounds;
import com.subspaceparasite.core.ModParticleTypes;
import net.minecraft.core.particles.DustParticleOptions;
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
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/**
 * EntityOrbScary - Attack/Homing Orb Projectile
 * <p>
 * Aggressive orb that orbits around the parent entity and provides
 * area denial and offensive capabilities. Used primarily by Heblu.
 * <p>
 * Features:
 * - Orbital movement around parent entity
 * - Passive debuff application to nearby enemies
 * - Timed explosive damage
 * - Scary-themed particle effects
 */
public class EntityOrbScary extends EntityOrbBase {
    
    // ========== SynchedEntityData Accessors ==========
    private static final EntityDataAccessor<Integer> DATA_ORBIT_RADIUS =
            SynchedEntityData.defineId(EntityOrbScary.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_ORBIT_SPEED =
            SynchedEntityData.defineId(EntityOrbScary.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_DEBUFF_DURATION =
            SynchedEntityData.defineId(EntityOrbScary.class, EntityDataSerializers.INT);
    
    // ========== Core Fields ==========
    private int orbitRadius = 8; // blocks from parent
    private float orbitSpeed = 0.05f;
    private int debuffDuration = 60; // ticks (3 seconds)
    private float orbitAngle = 0.0f;
    private static final float BASE_DAMAGE = 6.0f;
    private static final double AOE_RADIUS = 5.0;
    private static final int DEBUFF_COOLDOWN = 40; // ticks between debuff applications
    
    // ========== Constructor ==========
    
    public EntityOrbScary(EntityType<? extends EntityOrbScary> type, Level level) {
        super(type, level);
        this.orbType = 1; // Scary orb type
    }
    
    public EntityOrbScary(Level level, LivingEntity shooter, int powerLevel) {
        super(level, shooter, 1, powerLevel); // Type 1 = Scary
        this.orbitRadius = 8;
        this.orbitSpeed = 0.05f + (powerLevel * 0.01f);
        this.damageMultiplier = 1.0F + (powerLevel * 0.25F);
    }
    
    // ========== Data Registration ==========
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ORBIT_RADIUS, 8);
        this.entityData.define(DATA_ORBIT_SPEED, 0.05f);
        this.entityData.define(DATA_DEBUFF_DURATION, 60);
    }
    
    // ========== NBT Persistence ==========
    
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("OrbitRadius", this.orbitRadius);
        compound.putFloat("OrbitSpeed", this.orbitSpeed);
        compound.putInt("DebuffDuration", this.debuffDuration);
    }
    
    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("OrbitRadius")) {
            this.orbitRadius = compound.getInt("OrbitRadius");
        }
        if (compound.contains("OrbitSpeed")) {
            this.orbitSpeed = compound.getFloat("OrbitSpeed");
        }
        if (compound.contains("DebuffDuration")) {
            this.debuffDuration = compound.getInt("DebuffDuration");
        }
    }
    
    // ========== Main Tick ==========
    
    @Override
    public void tick() {
        super.tick();
        
        // Update orbit position
        if (!level().isClientSide && getOwner() != null) {
            updateOrbitPosition();
        }
        
        // Apply debuffs to nearby enemies
        if (!level().isClientSide && age % DEBUFF_COOLDOWN == 0) {
            applyDebuffToNearbyEnemies();
        }
        
        // Client-side scary particles
        if (level().isClientSide) {
            spawnScaryParticles();
        }
        
        // Increment orbit angle
        orbitAngle += orbitSpeed;
    }
    
    // ========== Orbital Movement ==========
    
    private void updateOrbitPosition() {
        Entity owner = getOwner();
        if (owner == null || owner.isRemoved()) {
            return;
        }
        
        // Calculate orbit position
        double orbitX = owner.getX() + Math.cos(orbitAngle) * orbitRadius;
        double orbitZ = owner.getZ() + Math.sin(orbitAngle) * orbitRadius;
        double orbitY = owner.getY() + 2.0; // Slightly above owner
        
        // Smooth movement towards orbit position
        double dx = orbitX - getX();
        double dy = orbitY - getY();
        double dz = orbitZ - getZ();
        
        setDeltaMovement(getDeltaMovement().add(dx * 0.1, dy * 0.1, dz * 0.1));
        
        // Limit maximum speed
        Vec3 currentMotion = getDeltaMovement();
        double maxSpeed = 1.5;
        if (currentMotion.length() > maxSpeed) {
            setDeltaMovement(currentMotion.normalize().scale(maxSpeed));
        }
    }
    
    // ========== Debuff Application ==========
    
    private void applyDebuffToNearbyEnemies() {
        LivingEntity ownerEntity = getOwner() instanceof LivingEntity le ? le : null;
        var nearbyEntities = level().getNearbyEntities(
                LivingEntity.class,
                net.minecraft.world.entity.ai.targeting.TargetingConditions.DEFAULT,
                ownerEntity,
                getBoundingBox().inflate(AOE_RADIUS)
        );
        
        for (LivingEntity entity : nearbyEntities) {
            if (entity == getOwner()) {
                continue;
            }
            
            // Check if enemy is hostile to owner
            if (getOwner() instanceof LivingEntity ownerLiving && 
                !ownerLiving.isAlliedTo(entity)) {
                
                // Apply cooldown reduction debuff (simulated via damage)
                entity.hurt(damageSources().magic(), getCalculatedDamage() * 0.2f);
                
                // Could add custom mob effects here if configured
            }
        }
    }
    
    // ========== Particle Effects ==========
    
    @Override
    protected void spawnChargingParticles() {
        // Red/scary charging particles
        for (int i = 0; i < 4; i++) {
            level().addParticle(new DustParticleOptions(new Vector3f(1.0F, 0.0F, 0.0F), 1.0F),
                    this.getX() + (random.nextDouble() - 0.5) * getBbWidth(),
                    this.getY() + random.nextDouble() * getBbHeight(),
                    this.getZ() + (random.nextDouble() - 0.5) * getBbWidth(),
                    0.0, 0.0, 0.0);
        }
    }
    
    @Override
    protected void spawnTrailParticles() {
        // Red trail
        for (int i = 0; i < 2; i++) {
            level().addParticle(ParticleTypes.SMOKE,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    0.0, 0.02, 0.0);
        }
    }
    
    private void spawnScaryParticles() {
        // Ambient scary particles
        for (int i = 0; i < 2; i++) {
            level().addParticle(new DustParticleOptions(new Vector3f(1.0F, 0.0F, 0.0F), 1.0F),
                    this.getX() + (random.nextDouble() - 0.5) * getBbWidth(),
                    this.getY() + random.nextDouble() * getBbHeight(),
                    this.getZ() + (random.nextDouble() - 0.5) * getBbWidth(),
                    0.0, 0.0, 0.0);
        }
    }
    
    @Override
    protected void spawnImpactParticles() {
        // Explosion with red tint
        for (int i = 0; i < 15; i++) {
            level().addParticle(ParticleTypes.EXPLOSION,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    (random.nextDouble() - 0.5) * 0.8,
                    (random.nextDouble() - 0.5) * 0.8,
                    (random.nextDouble() - 0.5) * 0.8);
        }
        
        // Additional red particles
        for (int i = 0; i < 10; i++) {
            level().addParticle(new DustParticleOptions(new Vector3f(1.0F, 0.0F, 0.0F), 1.0F),
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    0.0, 0.0, 0.0);
        }
    }
    
    // ========== Sound Effects ==========
    
    @Override
    protected void playImpactSound() {
        level().playSound(null, this.getX(), this.getY(), this.getZ(),
                ModSounds.PROJECTILE_HIT.get(), SoundSource.HOSTILE,
                1.2F, 1.2F);
    }
    
    // ========== Collision Handling ==========
    
    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        if (target instanceof LivingEntity livingTarget) {
            float damage = getCalculatedDamage();
            target.hurt(damageSources().magic(), damage);
            
            // Apply knockback
            double knockbackStrength = 0.5;
            target.setDeltaMovement(target.getDeltaMovement().add(
                    target.getX() - getX() * knockbackStrength * 0.1,
                    knockbackStrength,
                    target.getZ() - getZ() * knockbackStrength * 0.1
            ));
        }
    }
    
    // ========== Getters and Setters ==========
    
    public int getOrbitRadius() {
        return this.entityData.get(DATA_ORBIT_RADIUS);
    }
    
    public void setOrbitRadius(int radius) {
        this.entityData.set(DATA_ORBIT_RADIUS, radius);
        this.orbitRadius = radius;
    }
    
    public float getOrbitSpeed() {
        return this.entityData.get(DATA_ORBIT_SPEED);
    }
    
    public void setOrbitSpeed(float speed) {
        this.entityData.set(DATA_ORBIT_SPEED, speed);
        this.orbitSpeed = speed;
    }
    
    public int getDebuffDuration() {
        return this.entityData.get(DATA_DEBUFF_DURATION);
    }
    
    public void setDebuffDuration(int duration) {
        this.entityData.set(DATA_DEBUFF_DURATION, duration);
        this.debuffDuration = duration;
    }
    
    // ========== Base Damage ==========
    
    @Override
    protected float getBaseDamage() {
        return BASE_DAMAGE;
    }
}
