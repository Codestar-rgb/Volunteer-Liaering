package com.subspaceparasite.common.entity.monster.inborn;

import com.subspaceparasite.common.entity.base.InfectionComponent;
import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.common.entity.base.EntityInbornBase;
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
 * EntityKol - Inborn stage parasite with balanced combat capabilities.
 * <p>
 * Original SRP 1.12.2: EntityKol in inborn stage.
 * Modernized for Forge 1.20.1 with component-based architecture.
 * <p>
 * Stats (configurable):
 * - Health: 70.0 - Moderate-high durability
 * - Attack Damage: 9.0 - Balanced damage output
 * - Speed: 0.25 - Good movement speed
 * - Armor: 6.0 - Moderate armor protection
 * <p>
 * Behavior:
 * - Versatile melee attacker with good mobility
 * - Targets non-infected entities aggressively
 * - Supports pack hunting behavior with other inborn parasites
 */
public class EntityKol extends EntityInbornBase {

    private static final double BASE_HEALTH = 70.0;
    private static final double BASE_ATTACK_DAMAGE = 9.0;
    private static final double BASE_SPEED = 0.25;
    private static final double BASE_ARMOR = 6.0;
    private static final double BASE_KNOCKBACK_RESISTANCE = 0.4;

    public EntityKol(EntityType<? extends EntityInbornBase> entityType, Level worldIn) {
        super(entityType, worldIn);
        this.xpReward = 18;
        this.setPhaseCreated(EvoPhase.PHASE_3);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // Base floating behavior
        this.goalSelector.addGoal(2, new FloatGoal(this));
        // Melee attack behavior with good speed
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.1D, true));
        // Target selection: attack non-infected living entities within 14 blocks
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 14, true, false,
            living -> !InfectionComponent.isInfected(living)));
        // Counter-attack when hurt
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SUBSRP_ENTITY_KOL_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSounds.SUBSRP_ENTITY_KOL_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SUBSRP_ENTITY_KOL_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, net.minecraft.world.level.block.state.BlockState blockIn) {
        // No footstep sounds, consistent with parasite characteristics
    }

    @Override
    protected float getStandingEyeHeight(net.minecraft.world.entity.Pose poseIn, net.minecraft.world.entity.EntityDimensions sizeIn) {
        return 1.9F;
    }

    @Override
    public void knockback(double strength, double ratioX, double ratioZ) {
        // Reduced knockback due to moderate knockback resistance
        super.knockback(strength * (1.0 - BASE_KNOCKBACK_RESISTANCE), ratioX, ratioZ);
    }

    /**
     * Creates attribute supplier for EntityKol.
     * Uses static method to support Forge entity attribute registration system.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return EntityInbornBase.createAttributes()
                .add(Attributes.MAX_HEALTH, BASE_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, BASE_SPEED)
                .add(Attributes.ARMOR, BASE_ARMOR)
                .add(Attributes.KNOCKBACK_RESISTANCE, BASE_KNOCKBACK_RESISTANCE);
    }

    /**
     * Spawn rule check for EntityKol.
     */
}
