package com.subspaceparasite.common.entity.monster.primitive;

import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.core.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
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
 * EntityShyco - Shy Camouflager Primitive stage parasite.
 * <p>
 * Original SRP 1.12.2: EntityShyco in primitive stage.
 * Modernized for Forge 1.20.1 with component-based architecture.
 * <p>
 * Stats (configurable):
 * - Health: 24.0
 * - Attack Damage: 7.0
 * - Speed: 0.22 (slow but stealthy)
 * <p>
 * Behavior:
 * - Camouflages as blocks when idle
 * - Ambush predator that attacks when targets approach
 * - Moderate health with high damage output
 * - Spreads infection on death
 */
public class EntityShyco extends EntityParasiteBase {

    private static final double BASE_HEALTH = 24.0;
    private static final double BASE_ATTACK_DAMAGE = 7.0;
    private static final double BASE_SPEED = 0.22;
    
    // Camouflage state
    private boolean isCamouflaged = false;
    private int camouflageTimer = 0;
    private static final int CAMOUFLAGE_DELAY = 60; // 3 seconds to camouflage
    
    public EntityShyco(EntityType<? extends EntityShyco> type, Level world) {
        super(type, world);
        this.xpReward = 10;
        this.setCanPickUpLoot(false);
        this.phaseCreated = EvoPhase.ONE;
        this.parasiteType = ParasiteType.PRI_SHYCO;
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
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        // Minimal sound when camouflaged
        if (!this.isCamouflaged) {
            super.playStepSound(pos, blockIn);
        }
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, net.minecraft.world.entity.EntityDimensions sizeIn) {
        return 1.5F;
    }

    @Override
    public void tick() {
        super.tick();
        
        if (!this.level().isClientSide()) {
            // Handle camouflage logic
            if (this.getTarget() == null && !this.isCamouflaged) {
                camouflageTimer++;
                if (camouflageTimer >= CAMOUFLAGE_DELAY) {
                    this.isCamouflaged = true;
                }
            } else {
                this.isCamouflaged = false;
                camouflageTimer = 0;
            }
        }
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

    /**
     * Check if this entity is currently camouflaged
     */
    public boolean isCamouflaged() {
        return this.isCamouflaged;
    }

    /**
     * Get camouflage progress (0-100)
     */
    public int getCamouflageProgress() {
        return (int)((float)camouflageTimer / CAMOUFLAGE_DELAY * 100.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMonsterAttributes()
            .add(Attributes.FOLLOW_RANGE, 20.0)
            .add(Attributes.MAX_HEALTH, BASE_HEALTH)
            .add(Attributes.MOVEMENT_SPEED, BASE_SPEED)
            .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.4);
    }

    @Override
    public String getTextureName() {
        return "textures/entity/primitive/subsrp_shyco.png";
    }

    @Override
    public String getModelName() {
        return "shyco";
    }
    
    /**
     * Check if entity is infected (helper method for target selection)
     */
    private boolean isInfected(LivingEntity entity) {
        return this.infectionComponent != null && this.infectionComponent.isInfected(entity);
    }
}
