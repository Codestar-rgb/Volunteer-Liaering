package com.subspaceparasite.common.entity.base;

import com.subspaceparasite.api.parasite.EvolutionPath;
import com.subspaceparasite.api.parasite.GeneType;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Abstract base class for Stationary parasites.
 * <p>
 * Stationary parasites do not wander or follow — they remain in place,
 * often buried or anchored to a structure. They have a buried mechanic
 * with peek, retreat, and relocate phases.
 * <p>
 * This class removes wander and follow AI goals and adds buried behavior.
 * Subclasses ({@link EntityStationaryArchitectBase} and its children)
 * add structure generation, summoning, and other nexus behaviors.
 * <p>
 * Base stats:
 * <ul>
 *   <li>Health: 60.0</li>
 *   <li>Attack Damage: 4.0</li>
 *   <li>Movement Speed: 0.02 (very slow)</li>
 *   <li>Armor: 8.0</li>
 *   <li>Follow Range: 32.0</li>
 *   <li>Knockback Resistance: 0.5</li>
 * </ul>
 */
public abstract class EntityStationaryBase extends EntityMalleableBase {

    // ========== Buried Mechanic ==========

    /** Whether this entity is currently buried (hidden underground). */
    protected boolean isBuried = false;

    /** Timer for peek behavior (exposing part of the entity). */
    protected int peekTimer = 0;

    /** Cooldown before the entity can relocate to a new position. */
    protected int relocateCooldown = 0;

    /** Maximum duration of a peek before retreating. */
    protected static final int PEEK_DURATION = 100;

    /** Cooldown for relocation after retreating. */
    protected static final int RELOCATE_COOLDOWN = 600;

    // ========== Constructor ==========

    protected EntityStationaryBase(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.evolutionPath = EvolutionPath.NEXUS;
        this.canDespawn = false;
        this.isBuried = true;

        // Stationary-tier gene activations
        geneBooleans[GeneType.DAMAGE_CAP.getIndex()] = true;
        geneBooleans[GeneType.MIN_DAMAGE.getIndex()] = true;
        geneFloats[GeneType.REGEN_RATE.getIndex()] = Math.max(
                geneFloats[GeneType.REGEN_RATE.getIndex()], 0.3F);
        geneFloats[GeneType.ANTI_KNOCKBACK.getIndex()] = Math.max(
                geneFloats[GeneType.ANTI_KNOCKBACK.getIndex()], 0.5F);
        geneFloats[GeneType.INFECTIOUSNESS.getIndex()] = Math.max(
                geneFloats[GeneType.INFECTIOUSNESS.getIndex()], 0.3F);
    }

    // ========== AI Goals Override ==========

    /**
     * Registers goals for a stationary entity. Removes wander and follow
     * goals that would cause the entity to move from its position.
     */
    @Override
    protected void registerGoals() {
        // No movement goals — stationary entities don't wander
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    // ========== Buried Behavior ==========

    /**
     * Initiates a peek — the entity briefly exposes itself from its
     * buried position to attack or observe.
     */
    protected void peek() {
        if (!isBuried) return;
        isBuried = false;
        peekTimer = PEEK_DURATION;
    }

    /**
     * Retreats back into the buried position, becoming harder to hit.
     */
    protected void retreat() {
        isBuried = true;
        peekTimer = 0;
        relocateCooldown = RELOCATE_COOLDOWN;
    }

    /**
     * Attempts to relocate to a nearby position.
     * Only possible after the relocate cooldown has expired.
     *
     * @return true if relocation was successful
     */
    protected boolean tryRelocate() {
        if (relocateCooldown > 0) return false;
        // Relocation logic — subclasses may override for specific behavior
        return false;
    }

    // ========== Tick Override ==========

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            // Handle peek timer
            if (!isBuried && peekTimer > 0) {
                peekTimer--;
                if (peekTimer <= 0) {
                    retreat();
                }
            }

            // Handle relocate cooldown
            if (relocateCooldown > 0) {
                relocateCooldown--;
            }
        }
    }

    // ========== Movement Override ==========

    /**
     * Stationary entities cannot be pushed. This prevents knockback
     * and other movement-inducing effects.
     */
    @Override
    public boolean isPushable() {
        return false;
    }

    /**
     * When buried, the entity is much harder to detect and hit.
     */
    public boolean isBuried() {
        return isBuried;
    }

    // ========== Attribute Supplier ==========

    /**
     * Creates the default attribute supplier for stationary parasites.
     * Very low movement speed, high armor and knockback resistance.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 60.0)
                .add(Attributes.MOVEMENT_SPEED, 0.02)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.ARMOR, 8.0)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
                .add(Attributes.ATTACK_SPEED, 1.0);
    }

    // ========== NBT Save/Load ==========

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("IsBuried", isBuried);
        tag.putInt("PeekTimer", peekTimer);
        tag.putInt("RelocateCooldown", relocateCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        isBuried = tag.getBoolean("IsBuried");
        peekTimer = tag.getInt("PeekTimer");
        relocateCooldown = tag.getInt("RelocateCooldown");
    }
}
