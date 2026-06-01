package com.subspaceparasite.common.entity.monster.primitive;

import net.minecraft.world.entity.Entity;
import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityPrimitiveBase;
import com.subspaceparasite.common.entity.ai.ParasiteMeleeAttackGoal;
import com.subspaceparasite.core.ModEntities;
import com.subspaceparasite.core.ModSounds;
import com.subspaceparasite.core.ModEffects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
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
 * EntityBano - Primitive stage parasite with area effect abilities.
 * <p>
 * Original SRP 1.12.2: EntityBano in primitive stage.
 * Modernized for Forge 1.20.1 with component-based architecture.
 * <p>
 * Stats (configurable):
 * - Health: 60.0 (ZETMO_HEALTH)
 * - Attack Damage: 8.0 (ZETMO_ATTACK_DAMAGE)
 * - Speed: 0.19
 * - Armor: 4.0 (ZETMO_ARMOR)
 * - Knockback Resistance: High (ZETMO_KD_RESISTANCE)
 * <p>
 * Behavior:
 * - Area effect aura that applies Zetmo effects
 * - Can summon followers (up to 2 within 16 blocks)
 * - Evolves to BanoAdapted after reaching kill threshold
 * - On death, may spawn EntityLesh (Crude stage) if colonies are active
 * - Has variant skins (5 and 7) with different properties
 */
public class EntityBano extends EntityPrimitiveBase {

    // Configuration constants (should be moved to config system)
    private static final double BASE_HEALTH = 60.0;
    private static final double BASE_ATTACK_DAMAGE = 8.0;
    private static final double BASE_SPEED = 0.19;
    private static final double BASE_ARMOR = 4.0;
    private static final double BASE_KNOCKBACK_RESISTANCE = 0.8;
    private static final double FOLLOW_RANGE = 16.0;

    // Evolution thresholds
    private static final int KILLS_TO_EVOLVE_PRIMITIVE = 15;
    
    // Animation state
    private float bodyAnimationProgress = 0.0F;
    private float bodyAnimationTarget = 0.0F;

    public EntityBano(EntityType<? extends Monster> type, Level world) {
        super(type, world);
        this.xpReward = 15;
        this.setCanPickUpLoot(false);
        this.phaseCreated = EvoPhase.PHASE_1; // Phase 1
        this.parasiteType = ParasiteType.PRI_BANO;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        // Basic movement
        this.goalSelector.addGoal(0, new FloatGoal(this));

        // Combat - melee attack
        this.goalSelector.addGoal(2, new ParasiteMeleeAttackGoal(this, 1.0, true));

        // Area effect goal (Zetmo aura) - TODO: Implement as separate AI goal
        // this.goalSelector.addGoal(3, new ParasiteAreaEffectGoal(this, ModEffects.ZETMO_EFFECT, 4.0, 60));

        // Follower gathering
        // this.goalSelector.addGoal(6, new ParasiteGatherFollowersGoal(this, 2, 16));

        // Idle behavior
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.8));

        // Target selection
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Animal.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractGolem.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Raider.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            clientTick();
            return;
        }

        // Body animation logic (from original SRP)
        byte status = this.getParasiteStatus();
        if (status == 15) {
            this.bodyAnimationTarget = 0.07F;
        } else {
            this.bodyAnimationTarget = -0.07F;
        }

        // Smooth animation interpolation
        this.bodyAnimationProgress += (this.bodyAnimationTarget - this.bodyAnimationProgress) * 0.1F;

        // Evolution check: Primitive -> Adapted (disabled until Dispatcher system is implemented)
        // TODO: Implement proper evolution dispatcher system
        // if (this.srpTicks % 20 == 0 && this.killCount > KILLS_TO_EVOLVE_PRIMITIVE) {
        //     tryEvolveToAdapted();
        // }

        // Death transformation check: Primitive -> Crude (if colonies active)
        // TODO: Implement colony system check
        // if (this.getHealth() <= 0 && !this.level().isClientSide) {
        //     checkDeathTransformation();
        // }
    }

    /**
     * Client-side tick for visual effects.
     */
    @Override
    protected void clientTick() {
        super.clientTick();

        // Portal particles when dying
        if (this.getParasiteStatus() == 6) {
            for (int i = 0; i < 3; i++) {
                this.level().addParticle(ParticleTypes.PORTAL,
                        this.getX() + (this.random.nextDouble() - 0.5) * this.getBbWidth(),
                        this.getY() + this.random.nextDouble() * this.getBbHeight(),
                        this.getZ() + (this.random.nextDouble() - 0.5) * this.getBbWidth(),
                        0.0, 0.05, 0.0);
            }
        }
    }

    /**
     * Attempt to evolve to Adapted stage.
     * TODO: Implement when EntityDispatcher system is complete.
     */
    private void tryEvolveToAdapted() {
        // Disabled until Dispatcher system is implemented
        // See: https://github.com/Codestar-rgb/Qom-Inseac for original implementation
    }

    /**
     * Check if should transform to Crude stage on death.
     * TODO: Implement when Colony system is complete.
     */
    private void checkDeathTransformation() {
        // Disabled until Colony system is implemented
        // Original SRP: If colonies activated and at least 1 colony exists, spawn EntityLesh
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean result = super.doHurtTarget(target);

        if (result && target instanceof LivingEntity livingTarget) {
            // Apply VIRA_E effect on hit for skin variant 5
            if (this.getSkinVariant() == 5) {
                livingTarget.addEffect(new MobEffectInstance(ModEffects.VIRAL.get(), 40, 0));
            }
        }

        return result;
    }

    @Override
    public void knockback(double strength, double ratioX, double ratioZ) {
        // Reduced knockback due to high knockback resistance
        super.knockback(strength * (1.0 - BASE_KNOCKBACK_RESISTANCE), ratioX, ratioZ);
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("BodyAnimation")) {
            this.bodyAnimationProgress = tag.getFloat("BodyAnimation");
        }
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("BodyAnimation", this.bodyAnimationProgress);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.BANO_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.BANO_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.BANO_DEATH.get();
    }

    @Override
    protected float getStandingEyeHeight(net.minecraft.world.entity.Pose pose, net.minecraft.world.entity.EntityDimensions dimensions) {
        return 2.7F;
    }

    @Override
    public ParasiteType getParasiteType() {
        return ParasiteType.PRI_BANO;
    }

    @Override
    public EvoPhase getPhaseCreated() {
        return EvoPhase.PHASE_1;
    }

    /**
     * Creates attribute supplier for EntityBano.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return EntityPrimitiveBase.createAttributes()
                .add(Attributes.MAX_HEALTH, BASE_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, BASE_SPEED)
                .add(Attributes.ARMOR, BASE_ARMOR)
                .add(Attributes.KNOCKBACK_RESISTANCE, BASE_KNOCKBACK_RESISTANCE);
    }



    public float getBodyAnimationProgress() {
        return this.bodyAnimationProgress;
    }
}
