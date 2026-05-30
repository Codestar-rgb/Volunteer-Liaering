package com.subspaceparasite.common.entity.projectile;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.core.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * Base class for all Orb projectiles in the SubspaceParasite mod.
 * <p>
 * Orbs are specialized projectile entities used by derived-stage parasites
 * (Heblu, Kirin, etc.) for their ranged attacks. Each orb type has unique
 * behavior and effects on impact.
 * <p>
 * Architecture:
 * - Uses ThrowableProjectile as base for physics-based trajectory
 * - Implements synched data for orb type and power level
 * - Supports particle effects and sound on impact
 * - Designed for extensibility with multiple orb variants
 */
public abstract class EntityOrbBase extends ThrowableProjectile {
    
    // ========== SynchedEntityData Accessors ==========
    
    private static final EntityDataAccessor<Integer> DATA_ORB_TYPE =
            SynchedEntityData.defineId(EntityOrbBase.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_POWER_LEVEL =
            SynchedEntityData.defineId(EntityOrbBase.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_CHARGING =
            SynchedEntityData.defineId(EntityOrbBase.class, EntityDataSerializers.BOOLEAN);
    
    // ========== Core Fields ==========
    
    protected int orbType = 0;
    protected int powerLevel = 1;
    protected int lifetime = 600; // 30 seconds max
    protected int age = 0;
    protected boolean isCharging = false;
    protected float damageMultiplier = 1.0F;
    
    // ========== Constructor ==========
    
    protected EntityOrbBase(EntityType<? extends ThrowableProjectile> type, Level level) {
        super(type, level);
    }
    
    public EntityOrbBase(Level level, LivingEntity shooter, int orbType, int powerLevel) {
        this((EntityType<? extends EntityOrbBase>) getOrbEntityType(level), level);
        this.setOwner(shooter);
        this.orbType = orbType;
        this.powerLevel = powerLevel;
        this.damageMultiplier = 1.0F + (powerLevel * 0.2F);
        this.setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
    }
    
    private static EntityType<?> getOrbEntityType(Level level) {
        // Default to Scary orb type, subclasses should override
        return ModEntities.PROJECTILE_ORB_SCARY.get();
    }
    
    // ========== Data Registration ==========
    
    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_ORB_TYPE, 0);
        this.entityData.define(DATA_POWER_LEVEL, 1);
        this.entityData.define(DATA_CHARGING, false);
    }
    
    // ========== NBT Persistence ==========
    
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("OrbType", this.orbType);
        compound.putInt("PowerLevel", this.powerLevel);
        compound.putInt("Age", this.age);
        compound.putBoolean("Charging", this.isCharging);
        compound.putFloat("DamageMultiplier", this.damageMultiplier);
    }
    
    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("OrbType")) {
            this.orbType = compound.getInt("OrbType");
        }
        if (compound.contains("PowerLevel")) {
            this.powerLevel = compound.getInt("PowerLevel");
        }
        if (compound.contains("Age")) {
            this.age = compound.getInt("Age");
        }
        if (compound.contains("Charging")) {
            this.isCharging = compound.getBoolean("Charging");
        }
        if (compound.contains("DamageMultiplier")) {
            this.damageMultiplier = compound.getFloat("DamageMultiplier");
        }
    }
    
    // ========== Main Tick ==========
    
    @Override
    public void tick() {
        super.tick();
        age++;
        
        if (age >= lifetime) {
            this.discard();
            return;
        }
        
        // Handle charging state
        if (isCharging && level().isClientSide) {
            spawnChargingParticles();
        }
        
        // Client-side trail particles
        if (level().isClientSide) {
            spawnTrailParticles();
        }
    }
    
    // ========== Collision Handling ==========
    
    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        
        if (!level().isClientSide) {
            playImpactSound();
            
            if (result instanceof EntityHitResult entityHitResult) {
                onHitEntity(entityHitResult);
            } else {
                onHitBlock(result);
            }
            
            spawnImpactParticles();
            this.discard();
        }
    }
    
    /** Called when hitting an entity. Override in subclasses for specific behavior. */
    protected abstract void onHitEntity(EntityHitResult result);
    
    /** Called when hitting a block. Override in subclasses for specific behavior. */
    protected void onHitBlock(HitResult result) {
        // Default: no special block interaction
    }
    
    // ========== Particle Effects ==========
    
    /** Spawns particles while charging. Override for custom effects. */
    protected void spawnChargingParticles() {
        for (int i = 0; i < 3; i++) {
            level().addParticle(ParticleTypes.PORTAL,
                    this.getX() + (random.nextDouble() - 0.5) * getBbWidth(),
                    this.getY() + random.nextDouble() * getBbHeight(),
                    this.getZ() + (random.nextDouble() - 0.5) * getBbWidth(),
                    0.0, 0.02, 0.0);
        }
    }
    
    /** Spawns trail particles during flight. Override for custom effects. */
    protected void spawnTrailParticles() {
        level().addParticle(ParticleTypes.SMOKE,
                this.getX(),
                this.getY(),
                this.getZ(),
                0.0, 0.0, 0.0);
    }
    
    /** Spawns particles on impact. Override for custom effects. */
    protected void spawnImpactParticles() {
        for (int i = 0; i < 8; i++) {
            level().addParticle(ParticleTypes.EXPLOSION,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    (random.nextDouble() - 0.5) * 0.5,
                    (random.nextDouble() - 0.5) * 0.5,
                    (random.nextDouble() - 0.5) * 0.5);
        }
    }
    
    // ========== Sound Effects ==========
    
    /** Plays sound on impact. Override for custom sounds. */
    protected void playImpactSound() {
        level().playSound(null, this.getX(), this.getY(), this.getZ(),
                ModSounds.PROJECTILE_HIT.get(), SoundSource.HOSTILE,
                1.0F, 1.0F);
    }
    
    // ========== Getters and Setters ==========
    
    public int getOrbType() {
        return this.entityData.get(DATA_ORB_TYPE);
    }
    
    public void setOrbType(int type) {
        this.entityData.set(DATA_ORB_TYPE, type);
        this.orbType = type;
    }
    
    public int getPowerLevel() {
        return this.entityData.get(DATA_POWER_LEVEL);
    }
    
    public void setPowerLevel(int level) {
        this.entityData.set(DATA_POWER_LEVEL, level);
        this.powerLevel = level;
        this.damageMultiplier = 1.0F + (level * 0.2F);
    }
    
    public boolean isCharging() {
        return this.entityData.get(DATA_CHARGING);
    }
    
    public void setCharging(boolean charging) {
        this.entityData.set(DATA_CHARGING, charging);
        this.isCharging = charging;
    }
    
    public int getAge() {
        return age;
    }
    
    public float getDamageMultiplier() {
        return damageMultiplier;
    }
    
    // ========== Utility Methods ==========
    
    /** Returns the base damage of this orb before multipliers. */
    protected abstract float getBaseDamage();
    
    /** Returns the calculated damage with power level multiplier applied. */
    public final float getCalculatedDamage() {
        return getBaseDamage() * damageMultiplier;
    }
}
