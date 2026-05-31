package com.subspaceparasite.common.entity.monster.inborn;

import com.subspaceparasite.api.core.component.infection.InfectionComponent;
import com.subspaceparasite.api.parasite.EvoPhase;
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
 * EntityRathol - Inborn stage parasite with aggressive pack hunting behavior.
 * <p>
 * Original SRP 1.12.2: EntityRathol in inborn stage.
 * Modernized for Forge 1.20.1 with component-based architecture.
 * <p>
 * Stats (configurable):
 * - Health: 60.0 - Moderate durability
 * - Attack Damage: 9.0 - Good damage output
 * - Speed: 0.27 - High movement speed for pack coordination
 * - Armor: 5.0 - Light armor protection
 * <p>
 * Behavior:
 * - Fast and agile pack hunter
 * - Coordinates attacks with other parasites
 * - Highly aggressive toward non-infected targets
 */
public class EntityRathol extends EntityParasiteBase {

    private static final double BASE_HEALTH = 60.0;
    private static final double BASE_ATTACK_DAMAGE = 9.0;
    private static final double BASE_SPEED = 0.27;
    private static final double BASE_ARMOR = 5.0;
    private static final double BASE_KNOCKBACK_RESISTANCE = 0.4;

    public EntityRathol(EntityType<? extends EntityParasiteBase> entityType, Level worldIn) {
        super(entityType, worldIn);
        this.xpReward = 17;
        this.setPhaseCreated(EvoPhase.TWO);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // Base floating behavior
        this.goalSelector.addGoal(2, new FloatGoal(this));
        // Melee attack behavior with high speed
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.25D, true));
        // Target selection: attack non-infected living entities within 14 blocks
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 14, true, false,
            living -> !InfectionComponent.isInfected(living)));
        // Counter-attack when hurt
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SUBSRP_ENTITY_RATHOL_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSounds.SUBSRP_ENTITY_RATHOL_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SUBSRP_ENTITY_RATHOL_DEATH.get();
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
        // Reduced knockback due to moderate knockback resistance
        super.knockback(strength * (1.0 - BASE_KNOCKBACK_RESISTANCE), ratioX, ratioZ);
    }

    /**
     * Creates attribute supplier for EntityRathol.
     * Uses static method to support Forge entity attribute registration system.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, BASE_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, BASE_SPEED)
                .add(Attributes.ARMOR, BASE_ARMOR)
                .add(Attributes.KNOCKBACK_RESISTANCE, BASE_KNOCKBACK_RESISTANCE);
    }

    /**
     * Spawn rule check for EntityRathol.
     */
    public static boolean checkSpawnRules(EntityType<? extends EntityRathol> type,
                                          net.minecraft.world.level.ServerLevelAccessor level,
                                          net.minecraft.world.entity.MobSpawnType spawnType,
                                          BlockPos pos, net.minecraft.util.RandomSource random) {
        return checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }
}
