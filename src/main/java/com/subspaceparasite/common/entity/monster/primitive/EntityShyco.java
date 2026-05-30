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
 * EntityShyco - Primitive stage parasite with balanced combat capabilities.
 * <p>
 * Original SRP 1.12.2: EntityShyco in primitive stage.
 * Modernized for Forge 1.20.1 with component-based architecture.
 * <p>
 * Stats (configurable):
 * - Health: 52.0
 * - Attack Damage: 7.2
 * - Speed: 0.178
 * - Armor: 3.7
 * <p>
 * Behavior:
 * - Balanced offensive and defensive capabilities
 * - Moderate movement speed and attack power
 * - Targets players and hostile mobs
 * - Versatile combat behavior
 */
public class EntityShyco extends EntityParasiteBase {

    private static final double BASE_HEALTH = 52.0;
    private static final double BASE_ATTACK_DAMAGE = 7.2;
    private static final double BASE_SPEED = 0.178;
    private static final double BASE_ARMOR = 3.7;

    public EntityShyco(EntityType<? extends EntityShyco> type, Level world) {
        super(type, world);
        this.xpReward = 13;
        this.setCanPickUpLoot(false);
        this.phaseCreated = EvoPhase.ONE;
        this.parasiteType = ParasiteType.PRI_SHYCO;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        // Basic movement
        this.goalSelector.addGoal(0, new FloatGoal(this));

        // Combat - melee attack with balanced speed
        this.goalSelector.addGoal(2, new ParasiteMeleeAttackGoal(this, 1.1, true));

        // Idle behavior
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));

        // Target selection
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Monster.class, false));
    }

    @Override
    public void tick() {
        super.tick();
        // Shyco-specific logic can be added here
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SUBSRP_ENTITY_SHYCO_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.SUBSRP_ENTITY_SHYCO_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SUBSRP_ENTITY_SHYCO_DEATH.get();
    }

    @Override
    public float getEyeHeight() {
        return 2.0F;
    }

    @Override
    public ParasiteType getParasiteType() {
        return ParasiteType.PRI_SHYCO;
    }

    @Override
    public EvoPhase getPhaseCreated() {
        return EvoPhase.ONE;
    }

    @Override
    public int getParasiteIDRegister() {
        return 23;
    }

    /**
     * Creates attribute supplier for EntityShyco.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, BASE_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, BASE_SPEED)
                .add(Attributes.ARMOR, BASE_ARMOR);
    }

    /**
     * Spawn rule check for EntityShyco.
     */
    public static boolean checkSpawnRules(EntityType<? extends EntityShyco> type,
                                          ServerLevelAccessor level, MobSpawnType spawnType,
                                          BlockPos pos, net.minecraft.util.RandomSource random) {
        return checkMobSpawnRules(type, level, spawnType, pos, random);
    }
}
