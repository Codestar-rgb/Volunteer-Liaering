package com.subspaceparasite.common.entity.monster.primitive;

import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.common.entity.ai.ParasiteFlightAttackGoal;
import com.subspaceparasite.common.entity.ai.ParasiteMeleeAttackGoal;
import com.subspaceparasite.core.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

/**
 * EntityCanra - Flying Primitive stage parasite.
 * <p>
 * Original SRP 1.12.2: EntityCanra in primitive stage.
 * Modernized for Forge 1.20.1 with component-based architecture.
 * <p>
 * Stats (configurable):
 * - Health: 55.0
 * - Attack Damage: 7.5
 * - Speed: 0.18 (flight speed)
 * - Armor: 3.8
 * - Knockback Resistance: Moderate
 * <p>
 * Behavior:
 * - Flying mob that prioritizes attacking chickens
 * - Spreads infection on death (radius 6 blocks, max 2 mobs)
 * - Can summon followers within range
 */
public class EntityCanra extends EntityParasiteBase {

    private static final double BASE_HEALTH = 55.0;
    private static final double BASE_ATTACK_DAMAGE = 7.5;
    private static final double BASE_SPEED = 0.18;
    private static final double BASE_ARMOR = 3.8;
    private static final double BASE_KNOCKBACK_RESISTANCE = 0.6;
    
    public EntityCanra(EntityType<? extends EntityCanra> type, Level world) {
        super(type, world);
        this.xpReward = 12;
        this.setCanPickUpLoot(false);
        this.phaseCreated = EvoPhase.ONE;
        this.parasiteType = ParasiteType.PRI_CANRA;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ParasiteFlightAttackGoal(this, 1.0, 20.0F));
        this.goalSelector.addGoal(2, new ParasiteMeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Chicken.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Monster.class, false));
    }

    @Override
    public boolean canFly() {
        return true;
    }

    @Override
    public boolean isFlapping() {
        return this.isFlying() && this.tickCount % 10 == 0;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SUBSRP_ENTITY_CANRA_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.SUBSRP_ENTITY_CANRA_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SUBSRP_ENTITY_CANRA_DEATH.get();
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide()) {
            this.spreadInfectionOnDeath(6.0, 2);
        }
        super.die(source);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.updateSwingTime();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.FOLLOW_RANGE, 24.0)
            .add(Attributes.MAX_HEALTH, BASE_HEALTH)
            .add(Attributes.MOVEMENT_SPEED, BASE_SPEED)
            .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
            .add(Attributes.ARMOR, BASE_ARMOR)
            .add(Attributes.KNOCKBACK_RESISTANCE, BASE_KNOCKBACK_RESISTANCE)
            .add(Attributes.FLYING_SPEED, BASE_SPEED * 1.5);
    }

    @Override
    public String getTextureName() {
        return "textures/entity/primitive/subsrp_canra.png";
    }

    @Override
    public String getModelName() {
        return "canra";
    }
}
