package com.subspaceparasite.common.entity.monster.primitive;

import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.core.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;

/**
 * EntityWymo - Frost Hunter Primitive stage parasite.
 * <p>
 * Original SRP 1.12.2: EntityWymo in primitive stage.
 * Modernized for Forge 1.20.1 with component-based architecture.
 * <p>
 * Stats (configurable):
 * - Health: 26.0
 * - Attack Damage: 5.5
 * - Speed: 0.29
 * <p>
 * Behavior:
 * - Gains bonuses in snow/cold biomes
 * - Freezing attack that applies slowness
 * - Enhanced movement on ice and snow blocks
 * - Spreads infection on death
 */
public class EntityWymo extends EntityParasiteBase {

    private static final double BASE_HEALTH = 26.0;
    private static final double BASE_ATTACK_DAMAGE = 5.5;
    private static final double BASE_SPEED = 0.29;
    
    // Frost attack cooldown
    private int frostAttackCooldown = 0;
    private static final int FROST_ATTACK_COOLDOWN = 40;
    
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
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2, true));
        
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
            living -> !this.isInfected(living)));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
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
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        super.playStepSound(pos, blockIn);
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, net.minecraft.world.entity.EntityDimensions sizeIn) {
        return 1.5F;
    }

    @Override
    public void tick() {
        super.tick();
        
        if (!this.level().isClientSide()) {
            // Frost attack cooldown
            if (frostAttackCooldown > 0) {
                frostAttackCooldown--;
            }
            
            // Cold biome bonus check
            if (this.isInColdBiome()) {
                this.setGlowingTag(true);
            } else {
                this.setGlowingTag(false);
            }
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (super.doHurtTarget(target)) {
            // Apply freezing effect on hit
            if (target instanceof LivingEntity living && frostAttackCooldown <= 0) {
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
                living.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 40, 0));
                frostAttackCooldown = FROST_ATTACK_COOLDOWN;
            }
            return true;
        }
        return false;
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
     * Check if currently in a cold/snow biome
     */
    private boolean isInColdBiome() {
        Biome biome = this.level().getBiome(this.blockPosition()).value();
        return biome.getTemperature(this.blockPosition()) < 0.15F;
    }

    /**
     * Get movement speed bonus when on ice/snow
     */
    @Override
    public float getWalkTargetValue(BlockPos pos) {
        var blockState = this.level().getBlockState(pos);
        // Ice and snow blocks give movement bonus
        if (blockState.is(net.minecraft.world.level.block.Blocks.ICE) ||
            blockState.is(net.minecraft.world.level.block.Blocks.PACKED_ICE) ||
            blockState.is(net.minecraft.world.level.block.Blocks.BLUE_ICE) ||
            blockState.is(net.minecraft.world.level.block.Blocks.SNOW_BLOCK) ||
            blockState.is(net.minecraft.world.level.block.Blocks.SNOW)) {
            return 1.5F;
        }
        return super.getWalkTargetValue(pos);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMonsterAttributes()
            .add(Attributes.FOLLOW_RANGE, 24.0)
            .add(Attributes.MAX_HEALTH, BASE_HEALTH)
            .add(Attributes.MOVEMENT_SPEED, BASE_SPEED)
            .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.3);
    }

    @Override
    public String getTextureName() {
        return "textures/entity/primitive/subsrp_wymo.png";
    }

    @Override
    public String getModelName() {
        return "wymo";
    }
    
    /**
     * Check if entity is infected (helper method for target selection)
     */
    private boolean isInfected(LivingEntity entity) {
        return this.infectionComponent != null && this.infectionComponent.isInfected(entity);
    }
}
