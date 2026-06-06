package com.subspaceparasite.common.entity.monster.primitive;

import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityPrimitiveBase;
import com.subspaceparasite.core.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * EntityRanrac - Burrowing Assault Primitive stage parasite.
 * <p>
 * Original SRP 1.12.2: EntityRanrac in primitive stage.
 * Modernized for Forge 1.20.1 with component-based architecture.
 * <p>
 * Stats (configurable):
 * - Health: 28.0
 * - Attack Damage: 6.0
 * - Speed: 0.32 (high ground mobility)
 * <p>
 * Behavior:
 * - High-speed underground movement
 * - Excavation/digging ability
 * - Ambush predator that emerges from ground
 * - Spreads infection on death
 */
public class EntityRanrac extends EntityPrimitiveBase {

    private static final double BASE_HEALTH = 28.0;
    private static final double BASE_ATTACK_DAMAGE = 6.0;
    private static final double BASE_SPEED = 0.32;
    
    public EntityRanrac(EntityType<? extends Monster> type, Level world) {
        super(type, world);
        this.xpReward = 10;
        this.setCanPickUpLoot(false);
        this.phaseCreated = EvoPhase.PHASE_1;
        this.parasiteType = ParasiteType.PRI_RANRAC;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2, true));
        
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
            living -> !this.isInfected(living)));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SUBSRP_ENTITY_RANRAC_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.SUBSRP_ENTITY_RANRAC_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SUBSRP_ENTITY_RANRAC_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        // No footstep sounds for burrowing mob
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, net.minecraft.world.entity.EntityDimensions sizeIn) {
        return 1.5F;
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide()) {
            this.spreadInfectionOnDeath(5.0, 2);
        }
        super.die(source);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.updateSwingTime();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityPrimitiveBase.createAttributes()
            .add(Attributes.FOLLOW_RANGE, 24.0)
            .add(Attributes.MAX_HEALTH, BASE_HEALTH)
            .add(Attributes.MOVEMENT_SPEED, BASE_SPEED)
            .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.3);
    }

    public String getTextureName() {
        return "textures/entity/primitive/subsrp_ranrac.png";
    }

    public String getModelName() {
        return "ranrac";
    }
    
    /**
     * Check if entity is infected (helper method for target selection)
     */
    private boolean isInfected(LivingEntity entity) {
        return this.infectionComponent != null && this.infectionComponent.isInfected(entity);
    }
}
