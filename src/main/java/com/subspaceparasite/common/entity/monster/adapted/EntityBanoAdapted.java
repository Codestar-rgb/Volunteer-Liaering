package com.subspaceparasite.common.entity.monster.adapted;

import com.subspaceparasite.common.entity.base.InfectionComponent;
import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.common.entity.base.EntityAdaptedBase;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.core.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.level.Level;

/**
 * EntityBanoAdapted - Adapted stage parasite evolved from EntityBano.
 * <p>
 * Original SRP 1.12.2: EntityBanoAdapted in adapted stage.
 * Modernized for Forge 1.20.1 with component-based architecture.
 * <p>
 * Stats (configurable):
 * - Health: 85.0 - High durability, improved from primitive stage
 * - Attack Damage: 11.0 - High damage output
 * - Speed: 0.23 - Good movement speed
 * - Armor: 7.0 - High armor protection
 * <p>
 * Behavior:
 * - Enhanced version of primitive Bano with improved combat capabilities
 * - Maintains area effect abilities with increased potency
 * - Targets non-infected entities aggressively
 * - Part of the evolutionary chain from Primitive to Adapted stage
 */
public class EntityBanoAdapted extends EntityAdaptedBase {

    private static final double BASE_HEALTH = 85.0;
    private static final double BASE_ATTACK_DAMAGE = 11.0;
    private static final double BASE_SPEED = 0.23;
    private static final double BASE_ARMOR = 7.0;
    private static final double BASE_KNOCKBACK_RESISTANCE = 0.45;

    public EntityBanoAdapted(EntityType<? extends EntityAdaptedBase> entityType, Level worldIn) {
        super(entityType, worldIn);
        this.xpReward = 25;
        this.setPhaseCreated(EvoPhase.PHASE_2);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // Base floating behavior
        this.goalSelector.addGoal(2, new FloatGoal(this));
        // Melee attack behavior with good speed
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.15D, true));
        // Target selection: attack non-infected living entities within 16 blocks
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 16, true, false,
            living -> !InfectionComponent.isInfected(living)));
        // Counter-attack when hurt
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SUBSRP_ENTITY_BANO_ADAPTED_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSounds.SUBSRP_ENTITY_BANO_ADAPTED_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SUBSRP_ENTITY_BANO_ADAPTED_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, net.minecraft.world.level.block.state.BlockState blockIn) {
        // No footstep sounds, consistent with parasite characteristics
    }

    @Override
    protected float getStandingEyeHeight(net.minecraft.world.entity.Pose poseIn, net.minecraft.world.entity.EntityDimensions sizeIn) {
        return 1.8F;
    }

    @Override
    public void knockback(double strength, double ratioX, double ratioZ) {
        // Reduced knockback due to moderate-high knockback resistance
        super.knockback(strength * (1.0 - BASE_KNOCKBACK_RESISTANCE), ratioX, ratioZ);
    }

    /**
     * Creates attribute supplier for EntityBanoAdapted.
     * Uses static method to support Forge entity attribute registration system.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return EntityAdaptedBase.createAttributes()
                .add(Attributes.MAX_HEALTH, BASE_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, BASE_SPEED)
                .add(Attributes.ARMOR, BASE_ARMOR)
                .add(Attributes.KNOCKBACK_RESISTANCE, BASE_KNOCKBACK_RESISTANCE);
    }

    /**
     * Spawn rule check for EntityBanoAdapted.
     */
    public static boolean checkSpawnRules(EntityType<EntityBanoAdapted> type,
                                          net.minecraft.world.level.ServerLevelAccessor level,
                                          net.minecraft.world.entity.MobSpawnType spawnType,
                                          BlockPos pos, net.minecraft.util.RandomSource random) {
        return EntityParasiteBase.checkSpawnRules(type, level, spawnType, pos, random);
    }
}
