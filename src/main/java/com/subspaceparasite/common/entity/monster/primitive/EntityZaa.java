package com.subspaceparasite.common.entity.monster.primitive;

import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

/**
 * EntityZaa - Primitive stage parasite with highest durability and damage.
 * <p>
 * Original SRP 1.12.2: EntityZaa in primitive stage.
 * Modernized for Forge 1.20.1 with component-based architecture.
 * <p>
 * Stats (configurable):
 * - Health: 70.0
 * - Attack Damage: 10.0
 * - Speed: 0.22
 * - Armor: 4.5
 * - Knockback Resistance: Very High
 * <p>
 * Behavior:
 * - Elite tank-type parasite with highest stats among primitives
 * - Extremely high durability and damage output
 * - Strong knockback resistance
 * - Aggressive toward all hostile targets
 */
public class EntityZaa extends EntityParasiteBase {

    private static final double BASE_HEALTH = 70.0;
    private static final double BASE_ATTACK_DAMAGE = 10.0;
    private static final double BASE_SPEED = 0.22;
    private static final double BASE_ARMOR = 4.5;
    private static final double BASE_KNOCKBACK_RESISTANCE = 0.85;

    public EntityZaa(EntityType<? extends EntityZaa> type, Level world) {
        super(type, world);
        this.xpReward = 16;
        this.setCanPickUpLoot(false);
        this.phaseCreated = EvoPhase.ONE;
        this.parasiteType = ParasiteType.PRI_ZAA;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        // Basic movement
        this.goalSelector.addGoal(0, new FloatGoal(this));

        // Combat - melee attack with high priority
        this.goalSelector.addGoal(1, new ParasiteMeleeAttackGoal(this, 1.0, true));

        // Idle behavior
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 10.0F));

        // Target selection
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Monster.class, false));
    }

    @Override
    public void tick() {
        super.tick();
        // Zaa-specific logic can be added here
    }

    @Override
    public void knockback(double strength, double ratioX, double ratioZ) {
        // Greatly reduced knockback due to very high knockback resistance
        super.knockback(strength * (1.0 - BASE_KNOCKBACK_RESISTANCE), ratioX, ratioZ);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SUBSRP_ENTITY_ZAA_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.SUBSRP_ENTITY_ZAA_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SUBSRP_ENTITY_ZAA_DEATH.get();
    }

    @Override
    public float getEyeHeight() {
        return 2.5F;
    }

    @Override
    public ParasiteType getParasiteType() {
        return ParasiteType.PRI_ZAA;
    }

    @Override
    public EvoPhase getPhaseCreated() {
        return EvoPhase.ONE;
    }

    @Override
    public int getParasiteIDRegister() {
        return 25;
    }

    /**
     * Creates attribute supplier for EntityZaa.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, BASE_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, BASE_SPEED)
                .add(Attributes.ARMOR, BASE_ARMOR)
                .add(Attributes.KNOCKBACK_RESISTANCE, BASE_KNOCKBACK_RESISTANCE);
    }

    /**
     * Spawn rule check for EntityZaa.
     */
    public static boolean checkSpawnRules(EntityType<? extends EntityZaa> type,
                                          ServerLevelAccessor level, MobSpawnType spawnType,
                                          BlockPos pos, net.minecraft.util.RandomSource random) {
        return checkMobSpawnRules(type, level, spawnType, pos, random);
    }
}
