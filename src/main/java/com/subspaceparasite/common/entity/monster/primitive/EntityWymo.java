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
 * EntityWymo - Primitive stage parasite with lower stats but higher mobility.
 * <p>
 * Original SRP 1.12.2: EntityWymo in primitive stage.
 * Modernized for Forge 1.20.1 with component-based architecture.
 * <p>
 * Stats (configurable):
 * - Health: 38.0
 * - Attack Damage: 5.0
 * - Speed: 0.15
 * - Armor: 3.0
 * <p>
 * Behavior:
 * - Lower health but more agile
 * - Faster movement and attack speed
 * - Targets players and animals
 * - Swarm-oriented behavior
 */
public class EntityWymo extends EntityParasiteBase {

    private static final double BASE_HEALTH = 38.0;
    private static final double BASE_ATTACK_DAMAGE = 5.0;
    private static final double BASE_SPEED = 0.15;
    private static final double BASE_ARMOR = 3.0;

    public EntityWymo(EntityType<? extends EntityWymo> type, Level world) {
        super(type, world);
        this.xpReward = 10;
        this.setCanPickUpLoot(false);
        this.phaseCreated = EvoPhase.ONE;
        this.parasiteType = ParasiteType.PRI_WYMO;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        // Basic movement
        this.goalSelector.addGoal(0, new FloatGoal(this));

        // Combat - melee attack with faster speed
        this.goalSelector.addGoal(2, new ParasiteMeleeAttackGoal(this, 1.2, true));

        // Idle behavior
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.9));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));

        // Target selection
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Monster.class, false));
    }

    @Override
    public void tick() {
        super.tick();
        // Wymo-specific logic can be added here
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SUBSRP_ENTITY_WYMO_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.SUBSRP_ENTITY_WYMO_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SUBSRP_ENTITY_WYMO_DEATH.get();
    }

    @Override
    public float getEyeHeight() {
        return 1.6F;
    }

    @Override
    public ParasiteType getParasiteType() {
        return ParasiteType.PRI_WYMO;
    }

    @Override
    public EvoPhase getPhaseCreated() {
        return EvoPhase.ONE;
    }

    @Override
    public int getParasiteIDRegister() {
        return 24;
    }

    /**
     * Creates attribute supplier for EntityWymo.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, BASE_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, BASE_SPEED)
                .add(Attributes.ARMOR, BASE_ARMOR);
    }

    /**
     * Spawn rule check for EntityWymo.
     */
    public static boolean checkSpawnRules(EntityType<? extends EntityWymo> type,
                                          ServerLevelAccessor level, MobSpawnType spawnType,
                                          BlockPos pos, net.minecraft.util.RandomSource random) {
        return checkMobSpawnRules(type, level, spawnType, pos, random);
    }
}
