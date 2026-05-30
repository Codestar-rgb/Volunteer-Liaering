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
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

/**
 * EntityNogla - Primitive stage parasite with stealth and ambush capabilities.
 * <p>
 * Original SRP 1.12.2: EntityNogla in primitive stage.
 * Modernized for Forge 1.20.1 with component-based architecture.
 * <p>
 * Stats (configurable):
 * - Health: 50.0
 * - Attack Damage: 7.0
 * - Speed: 0.22
 * - Armor: 3.0
 * - Special: Cloaking ability, ambush attacks
 * <p>
 * Behavior:
 * - Can cloak/turn invisible when stationary
 * - Deals bonus damage when attacking from cloaked state
 * - Evolves to adapted stage after kill threshold
 * - Highly agile with quick movement
 */
public class EntityNogla extends EntityParasiteBase {

    private static final double BASE_HEALTH = 50.0;
    private static final double BASE_ATTACK_DAMAGE = 7.0;
    private static final double BASE_SPEED = 0.22;
    private static final double BASE_ARMOR = 3.0;
    private static final double FOLLOW_RANGE = 20.0;

    private static final int KILLS_TO_EVOLVE = 12;

    private float bodyAnimationProgress = 0.0F;
    private float bodyAnimationTarget = 0.0F;

    public EntityNogla(EntityType<? extends EntityNogla> type, Level world) {
        super(type, world);
        this.xpReward = 12;
        this.setCanPickUpLoot(false);
        this.phaseCreated = EvoPhase.ONE;
        this.parasiteType = ParasiteType.NOGLA;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new ParasiteMeleeAttackGoal(this, 1.2, true));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(9, new RandomStrollGoal(this, 1.0));

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

        // Body animation logic
        byte status = this.getParasiteStatus();
        if (status == 15) {
            this.bodyAnimationTarget = 0.08F;
        } else {
            this.bodyAnimationTarget = -0.08F;
        }

        this.bodyAnimationProgress += (this.bodyAnimationTarget - this.bodyAnimationProgress) * 0.1F;

        // Cloaking behavior: cloak when stationary for 2 seconds
        if (this.getDeltaMovement().lengthSqr() < 0.01 && !this.isAggressive()) {
            if (this.tickCount % 40 == 0) {
                this.setCloakingLevel(Math.min(this.getCloakingLevel() + 0.1F, 0.9F));
            }
        } else {
            this.setCloakingLevel(Math.max(this.getCloakingLevel() - 0.05F, 0.0F));
        }
    }

    @Override
    protected void clientTick() {
        super.clientTick();
        // Client-side visual effects can be added here
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean result = super.doHurtTarget(target);

        if (result && this.getCloakingLevel() > 0.5F) {
            // Bonus damage when attacking from cloaked state
            target.hurt(this.damageSources().mobAttack(this), 3.0F);
            this.setCloakingLevel(0.0F); // Break cloak on attack
        }

        return result;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SUBSRP_ZETMO_GROWL.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return this.random.nextBoolean() && this.getHurtFlashTimer() > 0
                ? ModSounds.SUBSRP_MOB_SILENCE.get()
                : ModSounds.SUBSRP_ZETMO_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SUBSRP_ZETMO_DEATH.get();
    }

    @Override
    public float getEyeHeight() {
        return 2.4F;
    }

    @Override
    public ParasiteType getParasiteType() {
        return ParasiteType.NOGLA;
    }

    @Override
    public EvoPhase getPhaseCreated() {
        return EvoPhase.ONE;
    }

    @Override
    public int getParasiteIDRegister() {
        return 23;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, BASE_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, BASE_SPEED)
                .add(Attributes.ARMOR, BASE_ARMOR);
    }

    public static boolean checkSpawnRules(EntityType<? extends EntityNogla> type,
                                          ServerLevelAccessor level, MobSpawnType spawnType,
                                          BlockPos pos, net.minecraft.util.RandomSource random) {
        return checkSpawnRules(type, level, spawnType, pos, random);
    }

    public float getBodyAnimationProgress() {
        return this.bodyAnimationProgress;
    }
}
