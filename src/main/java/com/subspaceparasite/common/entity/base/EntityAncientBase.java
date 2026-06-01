package com.subspaceparasite.common.entity.base;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import java.util.List;

import com.subspaceparasite.api.parasite.EvolutionPath;
import com.subspaceparasite.api.parasite.GeneType;
import com.subspaceparasite.common.entity.ai.ParasiteGiveEffectsGoal;
import com.subspaceparasite.common.entity.ai.ParasiteSpecialSkillGoal;
import com.subspaceparasite.common.entity.ai.ParasiteSummonGoal;
import com.subspaceparasite.common.entity.ai.ParasiteWanderGoal;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/**
 * Abstract base class for Ancient-tier parasites.
 * <p>
 * Ancients are endgame entities — the oldest and most powerful forms.
 * They possess area-of-effect abilities, boss-tier behavior, and
 * significantly enhanced stats. They cannot despawn naturally.
 * <p>
 * Type category: {@link EvolutionPath#ANCIENT}
 * <p>
 * Base stats:
 * <ul>
 *   <li>Health: 200.0</li>
 *   <li>Attack Damage: 25.0</li>
 *   <li>Movement Speed: 0.30</li>
 *   <li>Armor: 20.0</li>
 *   <li>Follow Range: 64.0</li>
 *   <li>Knockback Resistance: 0.6</li>
 * </ul>
 * <p>
 * Area effects: Every 80 ticks, applies slowness and weakness to non-parasite
 * entities within 12 blocks, and heals nearby parasites.
 */
public abstract class EntityAncientBase extends EntityMalleableBase {

    /** Range of the area effect in blocks. */
    protected float areaEffectRange = 12.0F;

    /** Ticks between area effect applications. */
    protected int areaEffectInterval = 80;

    /** Internal tick counter for area effects. */
    protected int areaEffectTickCounter = 0;

    // ========== Constructor ==========

    protected EntityAncientBase(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.evolutionPath = EvolutionPath.ANCIENT;
        this.canDespawn = false;
        this.isLeaderFlag = true;
        this.adaptationGainRate = 0.08F;
        this.xpReward = 50;

        // Ancient-tier gene activations — all combat genes active
        geneBooleans[GeneType.SPECIAL_MOVE.getIndex()] = true;
        geneBooleans[GeneType.LOOK_WALL.getIndex()] = true;
        geneBooleans[GeneType.SPRINTING.getIndex()] = true;
        geneBooleans[GeneType.DAMAGE_CAP.getIndex()] = true;
        geneBooleans[GeneType.MIN_DAMAGE.getIndex()] = true;
        geneBooleans[GeneType.WATER_LEAP.getIndex()] = true;
        geneFloats[GeneType.REGEN_RATE.getIndex()] = Math.max(
                geneFloats[GeneType.REGEN_RATE.getIndex()], 0.5F);
        geneFloats[GeneType.ATTACK_SPEED.getIndex()] = Math.max(
                geneFloats[GeneType.ATTACK_SPEED.getIndex()], 1.5F);
        geneFloats[GeneType.ANTI_KNOCKBACK.getIndex()] = Math.max(
                geneFloats[GeneType.ANTI_KNOCKBACK.getIndex()], 0.5F);
        geneFloats[GeneType.POISON_HEALING.getIndex()] = Math.max(
                geneFloats[GeneType.POISON_HEALING.getIndex()], 0.5F);

        // Ancient-tier abilities — all combat abilities including self-destruct option
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.SUMMON, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.TELEPORT, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.AOE_SLAM, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.BLOCK_BREAK, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.BLOCK_PLACE, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.HEAL_AURA, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.INFECT_AURA, true);
    }

    // ========== AI Goals ==========

    /**
     * Registers goals for ancient-tier parasites.
     * Adds summon, devastating COTH/fear aura, and slow wander
     * on top of malleable base goals. Ancients are endgame entities
     * with boss-tier behavior and area control.
     */
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ParasiteSummonGoal(this, 120, 24, 10));
        this.goalSelector.addGoal(3, new ParasiteSpecialSkillGoal(this));
        this.goalSelector.addGoal(6, new ParasiteGiveEffectsGoal(this, 80, 12, new String[]{"COTH", "VIRULENCE", "FEAR", "CORROSION"}));
        this.goalSelector.addGoal(7, new ParasiteWanderGoal(this, 0.8, 0.6F, true));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    // ========== Area Effects ==========

    /**
     * Applies area-of-effect debuffs to nearby non-parasite entities and
     * buffs to nearby parasites. Called every {@link #areaEffectInterval} ticks.
     */
    protected void applyAreaEffects() {
        if (this.level().isClientSide) return;

        AABB area = this.getBoundingBox().inflate(areaEffectRange);

        // Debuff non-parasites
        List<LivingEntity> enemies = this.level().getEntitiesOfClass(
                LivingEntity.class, area, e -> !(e instanceof EntityParasiteBase) && e.isAlive());
        for (LivingEntity enemy : enemies) {
            enemy.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
            enemy.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0));
        }

        // Heal nearby parasites
        List<EntityParasiteBase> allies = this.level().getEntitiesOfClass(
                EntityParasiteBase.class, area, p -> p != this && p.isAlive());
        for (EntityParasiteBase ally : allies) {
            ally.heal(2.0F);
        }

        // Visual effect
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ASH,
                    this.getX(), this.getY() + 1.0, this.getZ(),
                    20, areaEffectRange * 0.5, 1.0, areaEffectRange * 0.5, 0.02);
        }
    }

    // ========== Tick Override ==========

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            areaEffectTickCounter++;
            if (areaEffectTickCounter >= areaEffectInterval) {
                areaEffectTickCounter = 0;
                applyAreaEffects();
            }
        }
    }

    // ========== Attribute Supplier ==========

    /**
     * Creates the default attribute supplier for ancient-tier parasites.
     * Ancient stats are boss-level with very high health and damage.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 200.0)
                .add(Attributes.MOVEMENT_SPEED, 0.30)
                .add(Attributes.ATTACK_DAMAGE, 25.0)
                .add(Attributes.ARMOR, 20.0)
                .add(Attributes.FOLLOW_RANGE, 64.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6)
                .add(Attributes.ATTACK_SPEED, 1.5);
    }
}
