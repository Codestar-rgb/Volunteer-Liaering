package com.subspaceparasite.common.entity.monster.assimilated;

import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

/**
 * EntityAssimilatedHuman - The assimilated human entity (Feral stage).
 * <p>
 * This represents a fully transformed human host that has lost all human characteristics.
 * More aggressive and stronger than the infected stage.
 * <p>
 * Stats (configurable):
 * - Health: 30.0
 * - Attack Damage: 6.0
 * - Speed: 0.32
 * - Armor: 4.0
 * <p>
 * Behavior:
 * - Highly aggressive towards all non-parasite entities
 * - Can spread infection on hit with higher chance
 * - Immune to fire damage
 * - Can evolve to Primitive stage after sufficient kills
 */
public class EntityAssimilatedHuman extends EntityParasiteBase {

    private static final EntityDataAccessor<Integer> ASSIMILATION_PROGRESS =
            SynchedEntityData.defineId(EntityAssimilatedHuman.class, EntityDataSerializers.INT);
    
    private static final EntityDataAccessor<Boolean> IS_ENRAGED =
            SynchedEntityData.defineId(EntityAssimilatedHuman.class, EntityDataSerializers.BOOLEAN);

    private static final double BASE_HEALTH = 30.0;
    private static final double BASE_ATTACK_DAMAGE = 6.0;
    private static final double BASE_SPEED = 0.32;
    private static final double BASE_ARMOR = 4.0;
    
    private static final int KILLS_TO_EVOLVE = 10;
    private static final int TICKS_TO_EVOLVE = 24000; // 20 minutes at 20tps

    public EntityAssimilatedHuman(EntityType<? extends EntityAssimilatedHuman> type, Level world) {
        super(type, world);
        this.setCanPickUpLoot(false);
        this.xpReward = 10;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, BASE_SPEED, true));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 12.0f));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Mob.class, 12.0f));
        
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Animal.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractGolem.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Raider.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, EntityParasiteBase.class, false));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ASSIMILATION_PROGRESS, 0);
        this.entityData.define(IS_ENRAGED, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("AssimilationProgress", this.getAssimilationProgress());
        compound.putBoolean("Enraged", this.isEnraged());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setAssimilationProgress(compound.getInt("AssimilationProgress"));
        this.setEnraged(compound.getBoolean("Enraged"));
    }

    @Override
    public void tick() {
        super.tick();
        
        if (!this.level().isClientSide) {
            // Check enrage state based on nearby threats
            boolean shouldEnrage = this.checkEnrageCondition();
            if (shouldEnrage != this.isEnraged()) {
                this.setEnraged(shouldEnrage);
            }
            
            // Apply enrage bonuses
            if (this.isEnraged()) {
                this.applyEnrageEffects();
            }
            
            // Update assimilation progress
            this.updateAssimilationProgress();
            
            // Check for evolution
            this.checkEvolution();
        }
    }

    /**
     * Check if entity should be enraged (multiple enemies nearby or low health)
     */
    private boolean checkEnrageCondition() {
        int nearbyEnemies = this.level().getNearbyEntities(
            LivingEntity.class,
            e -> !(e instanceof EntityParasiteBase) && e.isAlive(),
            this,
            this.getBoundingBox().inflate(8.0)
        ).size();
        
        return nearbyEnemies >= 3 || this.getHealth() < this.getMaxHealth() * 0.3f;
    }

    /**
     * Apply enrage effects (speed and damage boost)
     */
    private void applyEnrageEffects() {
        if (this.tickCount % 20 == 0) {
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_ATTACK_DAMAGE * 1.5);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(BASE_SPEED * 1.3);
        }
    }

    /**
     * Update assimilation progress based on kills and time
     */
    private void updateAssimilationProgress() {
        int progress = this.getAssimilationProgress();
        if (progress < 100) {
            this.setAssimilationProgress(progress + 1);
        }
    }

    /**
     * Check if this entity should evolve to Primitive stage
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
        
        this.level().levelEvent(null, 1038, this.blockPosition(), 0);
        if (!this.level().isClientSide) {
            ((net.minecraft.server.level.ServerLevel)this.level()).sendParticles(
                net.minecraft.core.particles.ParticleTypes.END_ROD,
                this.getX(), this.getY() + 1.0, this.getZ(),
                50, 0.5, 1.0, 0.5, 0.1
            );
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean result = super.doHurtTarget(target);
        
        if (result && target instanceof LivingEntity living) {
            this.attemptInfect(living);
            this.playSound(ModSounds.ASSIMILATED_ATTACK.get(), 1.0f, 1.0f);
        }
        
        return result;
    }

    /**
     * Attempt to infect a living entity on hit (higher chance than infected)
     */
    private void attemptInfect(LivingEntity target) {
        if (target instanceof EntityParasiteBase) return;
        
        var infectionComponent = this.getInfectionComponent();
        if (infectionComponent != null) {
            infectionComponent.applyInfection(target, 0.5f); // 50% chance
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.ASSIMILATED_HUMAN_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.ASSIMILATED_HUMAN_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.ASSIMILATED_HUMAN_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, net.minecraft.world.level.block.state.BlockState block) {
        this.playSound(ModSounds.PARASITE_STEP.get(), 0.2f, 1.0f);
    }

    @Override
    public ParasiteType getParasiteType() {
        return ParasiteType.FER_HUMAN;
    }

    @Override
    public EvoPhase getEvoPhase() {
        return EvoPhase.TWO;
    }

    @Override
    public boolean canEvolve() {
        return true;
    }

    @Override
    public boolean shouldBurnInSun() {
        return false; // Assimilated humans are immune to sun damage
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (source.is(DamageTypeTags.IS_FIRE)) {
            return true; // Fire immune
        }
        return super.isInvulnerableTo(source);
    }

    @Override
    public void finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty,
                              MobSpawnType reason, net.minecraft.world.entity.SpawnGroupData spawnData,
                              CompoundTag dataTag) {
        super.finalizeSpawn(world, difficulty, reason, spawnData, dataTag);
        
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HEALTH);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_ATTACK_DAMAGE);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(BASE_SPEED);
        this.getAttribute(Attributes.ARMOR).setBaseValue(BASE_ARMOR);
        this.setHealth(this.getMaxHealth());
    }

    public int getAssimilationProgress() {
        return this.entityData.get(ASSIMILATION_PROGRESS);
    }

    public void setAssimilationProgress(int progress) {
        this.entityData.set(ASSIMILATION_PROGRESS, progress);
    }

    public boolean isEnraged() {
        return this.entityData.get(IS_ENRAGED);
    }

    public void setEnraged(boolean enraged) {
        this.entityData.set(IS_ENRAGED, enraged);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, BASE_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, BASE_SPEED)
                .add(Attributes.ARMOR, BASE_ARMOR)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.2);
    }
}
