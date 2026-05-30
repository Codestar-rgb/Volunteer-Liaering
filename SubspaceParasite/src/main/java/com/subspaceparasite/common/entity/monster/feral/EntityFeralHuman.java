package com.subspaceparasite.common.entity.monster.feral;

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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

/**
 * EntityFeralHuman - Feral human parasite with enhanced agility and pack behavior.
 */
public class EntityFeralHuman extends EntityParasiteBase {

    private static final EntityDataAccessor<Integer> PACK_LEADER_ID =
            SynchedEntityData.defineId(EntityFeralHuman.class, EntityDataSerializers.INT);

    private static final double BASE_HEALTH = 30.0;
    private static final double BASE_ATTACK_DAMAGE = 6.0;
    private static final double BASE_SPEED = 0.35;
    private static final double BASE_ARMOR = 4.0;

    public EntityFeralHuman(EntityType<? extends EntityFeralHuman> type, Level world) {
        super(type, world);
        this.setCanPickUpLoot(false);
        this.xpReward = 12;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, BASE_SPEED, true));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.1));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 14.0f));
        
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Animal.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractGolem.class, true));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PACK_LEADER_ID, -1);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("PackLeaderId", this.getPackLeaderId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setPackLeaderId(compound.getInt("PackLeaderId"));
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean result = super.doHurtTarget(target);
        
        if (result && target instanceof net.minecraft.world.entity.LivingEntity living) {
            this.attemptInfect(living);
            this.playSound(ModSounds.FERAL_HUMAN_ATTACK.get(), 1.0f, this.getVoicePitch());
        }
        
        return result;
    }

    private void attemptInfect(net.minecraft.world.entity.LivingEntity target) {
        if (target instanceof EntityParasiteBase) return;
        
        var infectionComponent = this.getInfectionComponent();
        if (infectionComponent != null) {
            infectionComponent.applyInfection(target, 0.45f);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.FERAL_HUMAN_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.FERAL_HUMAN_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.FERAL_HUMAN_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, net.minecraft.world.level.block.state.BlockState block) {
        this.playSound(ModSounds.PARASITE_STEP.get(), 0.22f, 1.0f);
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
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (source.is(DamageTypeTags.IS_FIRE)) {
            return true;
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

    public int getPackLeaderId() {
        return this.entityData.get(PACK_LEADER_ID);
    }

    public void setPackLeaderId(int id) {
        this.entityData.set(PACK_LEADER_ID, id);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, BASE_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, BASE_SPEED)
                .add(Attributes.ARMOR, BASE_ARMOR)
                .add(Attributes.FOLLOW_RANGE, 45.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.15);
    }
}
