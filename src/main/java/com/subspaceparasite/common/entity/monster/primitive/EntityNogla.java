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
 * EntityNogla - Primitive stage parasite with high mobility and leap attacks.
 * <p>
 * Original SRP 1.12.2: EntityNogla in primitive stage.
 * Modernized for Forge 1.20.1 with component-based architecture.
 * <p>
 * Stats (configurable):
 * - Health: 45.0
 * - Attack Damage: 9.0
 * - Speed: 0.28 (high mobility)
 * - Armor: 2.0
 * - Jump strength: Enhanced for leap attacks
 * <p>
 * Behavior:
 * - High movement speed and jump capability
 * - Leap attack to close distance quickly
 * - Targets players and animals aggressively
 * - Can climb walls (TODO: Implement wall climbing AI)
 */
public class EntityNogla extends EntityParasiteBase {

    private static final double BASE_HEALTH = 45.0;
    private static final double BASE_ATTACK_DAMAGE = 9.0;
    private static final double BASE_SPEED = 0.28;
    private static final double BASE_ARMOR = 2.0;
    private static final double BASE_JUMP_STRENGTH = 1.2;

    private int leapCooldown = 0;
    private static final int LEAP_COOLDOWN_MAX = 40;

    public EntityNogla(EntityType<? extends EntityNogla> type, Level world) {
        super(type, world);
        this.xpReward = 12;
        this.setCanPickUpLoot(false);
        this.phaseCreated = EvoPhase.ONE;
        this.parasiteType = ParasiteType.PRI_NOGLA;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        // Basic movement
        this.goalSelector.addGoal(0, new FloatGoal(this));

        // Combat - melee attack with high priority
        this.goalSelector.addGoal(1, new ParasiteMeleeAttackGoal(this, 1.3, true));

        // Idle behavior
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 10.0F));

        // Target selection
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Monster.class, false));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            return;
        }

        // Leap cooldown management
        if (this.leapCooldown > 0) {
            this.leapCooldown--;
        }

        // Leap attack logic when target is visible and at distance
        if (this.getTarget() != null && this.leapCooldown <= 0) {
            double distToTarget = this.distanceToSqr(this.getTarget());
            if (distToTarget > 9.0 && distToTarget < 144.0) {
                performLeap();
            }
        }
    }

    /**
     * Perform a leap toward the target.
     */
    private void performLeap() {
        if (this.getTarget() == null) return;

        double dx = this.getTarget().getX() - this.getX();
        double dz = this.getTarget().getZ() - this.getZ();
        double horizontalDist = Math.sqrt(dx * dx + dz * dz);

        if (horizontalDist > 0) {
            double boostX = (dx / horizontalDist) * BASE_JUMP_STRENGTH * 0.8;
            double boostZ = (dz / horizontalDist) * BASE_JUMP_STRENGTH * 0.8;
            double boostY = BASE_JUMP_STRENGTH * 0.5;

            this.setDeltaMovement(this.getDeltaMovement().add(boostX, boostY, boostZ));
            this.leapCooldown = LEAP_COOLDOWN_MAX;
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean result = super.doHurtTarget(target);

        if (result && this.isInLeap()) {
            // Extra damage on leap attack impact
            target.setDeltaMovement(target.getDeltaMovement().add(0, 0.5, 0));
        }

        return result;
    }

    /**
     * Check if entity is currently in a leap.
     */
    private boolean isInLeap() {
        return this.leapCooldown > LEAP_COOLDOWN_MAX - 10;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SUBSRP_ENTITY_NOGLA_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.SUBSRP_ENTITY_NOGLA_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SUBSRP_ENTITY_NOGLA_DEATH.get();
    }

    @Override
    public float getEyeHeight() {
        return 1.8F;
    }

    @Override
    public ParasiteType getParasiteType() {
        return ParasiteType.PRI_NOGLA;
    }

    @Override
    public EvoPhase getPhaseCreated() {
        return EvoPhase.ONE;
    }

    @Override
    public int getParasiteIDRegister() {
        return 21;
    }

    /**
     * Creates attribute supplier for EntityNogla.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, BASE_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, BASE_SPEED)
                .add(Attributes.ARMOR, BASE_ARMOR)
                .add(Attributes.JUMP_STRENGTH, BASE_JUMP_STRENGTH);
    }

    /**
     * Spawn rule check for EntityNogla.
     */
    public static boolean checkSpawnRules(EntityType<? extends EntityNogla> type,
                                          ServerLevelAccessor level, MobSpawnType spawnType,
                                          BlockPos pos, net.minecraft.util.RandomSource random) {
        return checkMobSpawnRules(type, level, spawnType, pos, random);
    }
}
