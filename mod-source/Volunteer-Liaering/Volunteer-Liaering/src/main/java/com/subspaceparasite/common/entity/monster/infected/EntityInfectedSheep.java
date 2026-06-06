package com.subspaceparasite.common.entity.monster.infected;

import net.minecraft.world.entity.Entity;
import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityInfectedBase;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.core.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
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
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;

/**
 * EntityInfectedSheep - Infected sheep entity.
 * <p>
 * Former sheep transformed by the parasite. Loses wool but gains
 * aggressive behavior and infection spreading capabilities.
 * <p>
 * Stats:
 * - Health: 24.0
 * - Attack Damage: 3.5
 * - Speed: 0.26
 * - Armor: 2.0
 */
public class EntityInfectedSheep extends EntityInfectedBase {

    private static final EntityDataAccessor<Integer> INFECTION_PROGRESS =
            SynchedEntityData.defineId(EntityInfectedSheep.class, EntityDataSerializers.INT);
    
    private static final EntityDataAccessor<Boolean> HAS_LOST_WOOL =
            SynchedEntityData.defineId(EntityInfectedSheep.class, EntityDataSerializers.BOOLEAN);

    private static final double BASE_HEALTH = 24.0;
    private static final double BASE_ATTACK_DAMAGE = 3.5;
    private static final double BASE_SPEED = 0.26;
    private static final double BASE_ARMOR = 2.0;

    public EntityInfectedSheep(EntityType<? extends Monster> type, Level world) {
        super(type, world);
        this.setCanPickUpLoot(false);
        this.xpReward = 5;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, BASE_SPEED, true));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.75));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Animal.class, 8.0f));
        
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Animal.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractGolem.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Sheep.class, true));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(INFECTION_PROGRESS, 0);
        this.entityData.define(HAS_LOST_WOOL, true); // Infected sheep lose their wool
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("InfectionProgress", this.getInfectionProgress());
        compound.putBoolean("HasLostWool", this.hasLostWool());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setInfectionProgress(compound.getInt("InfectionProgress"));
        this.setHasLostWool(compound.getBoolean("HasLostWool"));
    }

    @Override
    public void tick() {
        super.tick();
        
        if (!this.level().isClientSide) {
            this.updateInfectionProgress();
            this.checkEvolution();
        }
    }

    private void updateInfectionProgress() {
        int progress = this.getInfectionProgress();
        if (progress < 100) {
            this.setInfectionProgress(progress + 1);
        }
    }

    private void checkEvolution() {
        if (this.getKillCount() >= 5 || this.tickCount >= 12000) {
            this.triggerEvolution();
        }
    }

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
        
        if (result && target instanceof net.minecraft.world.entity.LivingEntity living) {
            this.attemptInfect(living);
            this.playSound(ModSounds.INFECTED_ATTACK.get(), 1.0f, 1.1f);
        }
        
        return result;
    }

    private void attemptInfect(net.minecraft.world.entity.LivingEntity target) {
        if (target instanceof EntityParasiteBase) return;
        
        var infectionComponent = this.getInfectionComponent();
        if (infectionComponent != null) {
            infectionComponent.spreadCOTH(target);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.INFECTED_SHEEP_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.INFECTED_SHEEP_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.INFECTED_SHEEP_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, net.minecraft.world.level.block.state.BlockState block) {
        this.playSound(ModSounds.PARASITE_STEP.get(), 0.15f, 1.0f);
    }

    @Override
    public ParasiteType getParasiteType() {
        return ParasiteType.SIM_SHEEP;
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor world, MobSpawnType spawnReason) {
        return true;
    }

    @Override
    public boolean checkSpawnObstruction(net.minecraft.world.level.LevelReader world) {
        return true;
    }

    @Override
    public net.minecraft.world.entity.SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty,
                              MobSpawnType reason, net.minecraft.world.entity.SpawnGroupData spawnData,
                              CompoundTag dataTag) {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HEALTH);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_ATTACK_DAMAGE);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(BASE_SPEED);
        this.getAttribute(Attributes.ARMOR).setBaseValue(BASE_ARMOR);
        
        this.setHealth(this.getMaxHealth());

        return super.finalizeSpawn(world, difficulty, reason, spawnData, dataTag);
    }

    public int getInfectionProgress() {
        return this.entityData.get(INFECTION_PROGRESS);
    }

    public void setInfectionProgress(int progress) {
        this.entityData.set(INFECTION_PROGRESS, progress);
    }

    public boolean hasLostWool() {
        return this.entityData.get(HAS_LOST_WOOL);
    }

    public void setHasLostWool(boolean lost) {
        this.entityData.set(HAS_LOST_WOOL, lost);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityInfectedBase.createAttributes()
                .add(Attributes.MAX_HEALTH, BASE_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, BASE_SPEED)
                .add(Attributes.ARMOR, BASE_ARMOR)
                .add(Attributes.FOLLOW_RANGE, 30.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.1);
    }
}
