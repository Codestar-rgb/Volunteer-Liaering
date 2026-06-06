package com.subspaceparasite.common.entity.base;

import com.subspaceparasite.api.parasite.EvolutionPath;
import com.subspaceparasite.api.parasite.GeneType;
import com.subspaceparasite.common.entity.ai.ParasiteBlockInfestGoal;
import com.subspaceparasite.common.entity.ai.ParasiteEvadeGoal;
import com.subspaceparasite.common.entity.ai.ParasiteSkillGoal;
import com.subspaceparasite.common.entity.ai.ParasiteSpecialSkillGoal;
import com.subspaceparasite.common.entity.ai.ParasiteWanderGoal;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Abstract base class for Derived-tier parasites.
 * <p>
 * Derived parasites are biome-specific evolved forms — special evolutionary
 * offshoots that have adapted to particular environments. Their stats and
 * abilities are boosted when in their native biome.
 * <p>
 * Type category: {@link EvolutionPath#DERIVED}
 * <p>
 * Base stats:
 * <ul>
 *   <li>Health: 80.0</li>
 *   <li>Attack Damage: 14.0</li>
 *   <li>Movement Speed: 0.32</li>
 *   <li>Armor: 8.0</li>
 *   <li>Follow Range: 40.0</li>
 *   <li>Knockback Resistance: 0.3</li>
 * </ul>
 * <p>
 * Biome boost: When in their native biome, derived parasites gain +20% health,
 * +15% damage, and +10% speed through attribute modifiers.
 */
public abstract class EntityDerivedBase extends EntityMalleableBase {

    /** Whether this entity is currently in its native biome. */
    protected boolean inNativeBiome = false;

    /** Multiplier applied to health when in native biome. */
    protected static final float BIOME_HEALTH_BOOST = 0.20F;

    /** Multiplier applied to damage when in native biome. */
    protected static final float BIOME_DAMAGE_BOOST = 0.15F;

    /** Multiplier applied to speed when in native biome. */
    protected static final float BIOME_SPEED_BOOST = 0.10F;

    // ========== Constructor ==========

    protected EntityDerivedBase(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.evolutionPath = EvolutionPath.DERIVED;
        this.canDespawn = false;

        // Derived-tier gene activations — highly evolved
        geneBooleans[GeneType.SPECIAL_MOVE.getIndex()] = true;
        geneBooleans[GeneType.LOOK_WALL.getIndex()] = true;
        geneBooleans[GeneType.SPRINTING.getIndex()] = true;
        geneBooleans[GeneType.DAMAGE_CAP.getIndex()] = true;
        geneBooleans[GeneType.WATER_LEAP.getIndex()] = true;
        geneFloats[GeneType.REGEN_RATE.getIndex()] = Math.max(
                geneFloats[GeneType.REGEN_RATE.getIndex()], 0.3F);
        geneFloats[GeneType.ATTACK_SPEED.getIndex()] = Math.max(
                geneFloats[GeneType.ATTACK_SPEED.getIndex()], 1.3F);
        geneFloats[GeneType.LEAP_POWER.getIndex()] = Math.max(
                geneFloats[GeneType.LEAP_POWER.getIndex()], 0.5F);

        // Derived-tier abilities — AOE slam, block break, teleport, infection aura
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.AOE_SLAM, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.BLOCK_BREAK, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.TELEPORT, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.INFECT_AURA, true);
    }

    // ========== AI Goals ==========

    /**
     * Registers goals for derived-tier parasites.
     * Adds block infestation, evasion, and special skill goals on top of
     * malleable base goals. Derived parasites have biome-adapted abilities
     * with evasive maneuvers and environmental manipulation.
     */
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new ParasiteBlockInfestGoal(this, 3));
        this.goalSelector.addGoal(3, new ParasiteSpecialSkillGoal(this));
        this.goalSelector.addGoal(4, new ParasiteSkillGoal(this, 100, 4.0F, 12.0F, (byte) 2, true));
        this.goalSelector.addGoal(5, new ParasiteEvadeGoal(this, 60, 40, 4.0));
        this.goalSelector.addGoal(7, new ParasiteWanderGoal(this, 1.0, 0.8F, true));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    // ========== Biome Checks ==========

    /**
     * Checks whether this entity is currently in its native biome.
     * Override in concrete subclasses to define the specific biome check.
     *
     * @return true if the entity is in its native biome
     */
    protected abstract boolean checkNativeBiome();

    /**
     * Returns the native biome name for this derived type.
     * Override in concrete subclasses.
     *
     * @return the native biome resource location string, or null if none
     */
    protected String getNativeBiomeName() {
        return null;
    }

    // ========== Tick Override ==========

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide && this.srpTicks % 40 == 0) {
            boolean wasNative = inNativeBiome;
            inNativeBiome = checkNativeBiome();
            if (wasNative != inNativeBiome) {
                applyBiomeBoost();
            }
        }
    }

    /**
     * Applies or removes biome-specific attribute boosts based on current
     * native biome status.
     */
    protected void applyBiomeBoost() {
        // Subclasses can override for custom boost behavior
        // The base implementation relies on the phase bonus system
    }

    // ========== Attribute Supplier ==========

    /**
     * Creates the default attribute supplier for derived-tier parasites.
     * Derived stats are high but lower than ancient/preeminent.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 80.0)
                .add(Attributes.MOVEMENT_SPEED, 0.32)
                .add(Attributes.ATTACK_DAMAGE, 14.0)
                .add(Attributes.ARMOR, 8.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3)
                .add(Attributes.ATTACK_SPEED, 1.3);
    }

    // ========== Accessors ==========

    /** Returns whether this entity is currently in its native biome. */
    public boolean isInNativeBiome() { return inNativeBiome; }
}
