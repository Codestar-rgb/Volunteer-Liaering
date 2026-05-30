package com.subspaceparasite.common.entity.monster.infected;

import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.core.ModEntities;
import com.subspaceparasite.core.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
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
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.common.Tags;

/**
 * EntityInfectedHuman - The basic infected human entity.
 * <p>
 * This is the first stage of infection for human villagers/players.
 * They retain some human characteristics but are fully controlled by the parasite.
 * <p>
 * Stats (configurable):
 * - Health: 20.0 (same as normal human)
 * - Attack Damage: 3.0
 * - Speed: 0.23 (slightly faster than normal)
 * - Armor: 2.0
 * <p>
 * Behavior:
 * - Attacks nearby non-parasite mobs and players
 * - Can spread infection on hit
 * - Burns in sunlight (optional, configurable)
 * - Evolves to Primitive stage after sufficient kills/time
 */
public class EntityInfectedHuman extends EntityParasiteBase {

    // Synched data for tracking infection progression
    private static final EntityDataAccessor<Integer> INFECTION_PROGRESS =
            SynchedEntityData.defineId(EntityInfectedHuman.class, EntityDataSerializers.INT);
    
    private static final EntityDataAccessor<Boolean> IS_BURNING_IN_SUN =
            SynchedEntityData.defineId(EntityInfectedHuman.class, EntityDataSerializers.BOOLEAN);

    // Configuration constants (should be moved to config system)
    private static final double BASE_HEALTH = 20.0;
    private static final double BASE_ATTACK_DAMAGE = 3.0;
    private static final double BASE_SPEED = 0.23;
    private static final double BASE_ARMOR = 2.0;
    
    // Evolution thresholds
    private static final int KILLS_TO_EVOLVE = 5;
    private static final int TICKS_TO_EVOLVE = 12000; // 10 minutes at 20tps

    public EntityInfectedHuman(EntityType<? extends EntityInfectedHuman> type, Level world) {
        super(type, world);
        this.setCanPickUpLoot(false);
        this.xpReward = 5;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        
        // Basic movement goals
        this.goalSelector.addGoal(0, new FloatGoal(this));
        
        // Combat goals
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, BASE_SPEED, true));
        
        // Wandering behavior
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.8));
        
        // Look behavior
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Mob.class, 8.0f));
        
        // Target goals
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Animal.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractGolem.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Raider.class, true));
        
        // Don't target other parasites
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, EntityParasiteBase.class, false));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(INFECTION_PROGRESS, 0);
        this.entityData.define(IS_BURNING_IN_SUN, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("InfectionProgress", this.getInfectionProgress());
        compound.putBoolean("BurningInSun", this.isBurningInSun());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setInfectionProgress(compound.getInt("InfectionProgress"));
        this.setBurningInSun(compound.getBoolean("BurningInSun"));
    }

    @Override
    public void tick() {
        super.tick();
        
        if (!this.level().isClientSide) {
            // Check sun burning
            boolean burning = this.shouldBurnInSun();
            if (burning != this.isBurningInSun()) {
                this.setBurningInSun(burning);
            }
            
            // Apply sun damage if burning
            if (this.isBurningInSun() && this.isSunBurnTick()) {
                this.setSecondsOnFire(8);
            }
            
            // Update infection progress
            this.updateInfectionProgress();
            
            // Check for evolution
            this.checkEvolution();
        }
    }

    /**
     * Update infection progress based on kills and time
     */
    private void updateInfectionProgress() {
        int progress = this.getInfectionProgress();
        
        // Increment progress slowly over time
        if (progress < 100) {
            this.setInfectionProgress(progress + 1);
        }
    }

    /**
     * Check if this entity should evolve to the next stage
     */
    private void checkEvolution() {
        if (this.getKillCount() >= KILLS_TO_EVOLVE || 
            this.tickCount >= TICKS_TO_EVOLVE) {
            this.triggerEvolution();
        }
    }

    /**
     * Trigger evolution to Primitive stage
     */
    private void triggerEvolution() {
        if (this.level().isClientSide) return;
        
        // Play evolution sound
        this.level().levelEvent(null, 1038, this.blockPosition(), 0);
        
        // TODO: Transform to Primitive entity (e.g., EntityBano or EntityCanra)
        // This will be handled by the Dispatcher system once implemented
        if (!this.level().isClientSide) {
            ((ServerLevel)this.level()).sendParticles(
                net.minecraft.core.particles.ParticleTypes.END_ROD,
                this.getX(), this.getY() + 1.0, this.getZ(),
                50, 0.5, 1.0, 0.5, 0.1
            );
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean result = super.doHurtTarget(target);
        
        if (result && target instanceof LivingEntity living) {
            // Apply infection effect on hit
            this.attemptInfect(living);
            
            // Play attack sound
            this.playSound(ModSounds.INFECTED_ATTACK.get(), 1.0f, 1.0f);
        }
        
        return result;
    }

    /**
     * Attempt to infect a living entity on hit
     */
    private void attemptInfect(LivingEntity target) {
        if (target instanceof EntityParasiteBase) return; // Don't infect other parasites
        
        // Get infection component and apply infection
        var infectionComponent = this.getInfectionComponent();
        if (infectionComponent != null) {
            infectionComponent.applyInfection(target, 0.3f); // 30% chance
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.INFECTED_HUMAN_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.INFECTED_HUMAN_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.INFECTED_HUMAN_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, net.minecraft.world.level.block.state.BlockState block) {
        this.playSound(ModSounds.PARASITE_STEP.get(), 0.15f, 1.0f);
    }

    @Override
    public ParasiteType getParasiteType() {
        return ParasiteType.INFECTED;
    }

    @Override
    public EvoPhase getEvoPhase() {
        return EvoPhase.INFECTED;
    }

    @Override
    public boolean canEvolve() {
        return true;
    }

    @Override
    public boolean shouldBurnInSun() {
        // Infected humans burn in sunlight unless configured otherwise
        return this.level().canSeeSky(this.blockPosition()) && 
               this.level().getBrightness(this.blockPosition()) > 0.5f;
    }

    @Override
    public boolean checkSpawnRules(ServerLevelAccessor world, MobSpawnType spawnReason) {
        return true;
    }

    @Override
    public boolean checkSpawnObstruction(ServerLevelAccessor world) {
        return world.isUnobstructed(this);
    }

    @Override
    public void finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty,
                              MobSpawnType reason, net.minecraft.world.entity.SpawnGroupData spawnData,
                              CompoundTag dataTag) {
        super.finalizeSpawn(world, difficulty, reason, spawnData, dataTag);
        
        // Set initial attributes
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HEALTH);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_ATTACK_DAMAGE);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(BASE_SPEED);
        this.getAttribute(Attributes.ARMOR).setBaseValue(BASE_ARMOR);
        
        // Set full health on spawn
        this.setHealth(this.getMaxHealth());
    }

    // ========== Getters and Setters ==========

    public int getInfectionProgress() {
        return this.entityData.get(INFECTION_PROGRESS);
    }

    public void setInfectionProgress(int progress) {
        this.entityData.set(INFECTION_PROGRESS, progress);
    }

    public boolean isBurningInSun() {
        return this.entityData.get(IS_BURNING_IN_SUN);
    }

    public void setBurningInSun(boolean burning) {
        this.entityData.set(IS_BURNING_IN_SUN, burning);
    }

    // ========== Static Methods ==========

    /**
     * Create attribute supplier for EntityInfectedHuman
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, BASE_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, BASE_SPEED)
                .add(Attributes.ARMOR, BASE_ARMOR)
                .add(Attributes.FOLLOW_RANGE, 35.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.1);
    }
}
