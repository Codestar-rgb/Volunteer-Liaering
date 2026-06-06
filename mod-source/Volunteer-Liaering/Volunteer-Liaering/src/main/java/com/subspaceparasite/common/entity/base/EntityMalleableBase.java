package com.subspaceparasite.common.entity.base;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.api.parasite.GeneType;
import com.subspaceparasite.common.entity.ai.ParasiteGiveEffectsGoal;
import com.subspaceparasite.common.entity.ai.ParasiteWanderGoal;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Abstract base class for malleable parasites — those capable of building
 * damage resistance through repeated exposure to the same damage source.
 * <p>
 * This is the second tier in the entity hierarchy, sitting directly above
 * {@link EntityParasiteBase}. All combat-focused parasite types (Primitive,
 * Adapted, Pure, Preeminent, Ancient, Feral, Hijacked, Derived, Abomination,
 * and Stationary) extend this class.
 * <p>
 * Adaptation system: When hit by the same type of damage source repeatedly,
 * this entity builds up resistance to that damage type. The adaptation tracking
 * is delegated to {@link CombatComponent} which maintains a per-source-type map.
 * Adaptation gain rate scales with EvoPhase and the ADAPTATION_SPEED gene.
 * Adaptation decays over time when not being hit.
 * <p>
 * Malleable genes activated on construction:
 * <ul>
 *   <li>{@code geneAdaptation} — enables the adaptation resistance system</li>
 *   <li>{@code geneBlocksearch} — enables block-breaking/searching behavior</li>
 *   <li>{@code geneResidue} — enables residue/decay effects on surroundings</li>
 *   <li>{@code geneOrbbox} — enables orb/orbital projectile behavior</li>
 * </ul>
 * These are mapped to existing {@link GeneType} entries during construction.
 */
public abstract class EntityMalleableBase extends EntityParasiteBase {

    // ========== Malleable Gene Flags ==========

    /** Gene: adaptation — enables damage resistance building from repeated hits. */
    protected boolean geneAdaptation = true;

    /** Gene: blocksearch — enables block-breaking and searching behavior. */
    protected boolean geneBlocksearch = false;

    /** Gene: residue — enables residue/decay effects left on surroundings. */
    protected boolean geneResidue = false;

    /** Gene: orbbox — enables orb/orbital projectile attack behavior. */
    protected boolean geneOrbbox = false;

    // ========== Constructor ==========

    protected EntityMalleableBase(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        activateMalleableGenes();
    }

    // ========== AI Goals ==========

    /**
     * Registers goals for malleable-tier parasites.
     * Adds float goal, COTH aura (ParasiteGiveEffectsGoal), wander, and look at player
     * on top of the base EntityParasiteBase goals (melee, follow owner, targeting).
     */
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(6, new ParasiteGiveEffectsGoal(this, 80, 8, new String[]{"COTH"}));
        this.goalSelector.addGoal(7, new ParasiteWanderGoal(this, 1.0, 0.8F, true));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    // ========== Malleable Gene Activation ==========

    /**
     * Activates the four malleable-tier genes.
     * Maps malleable gene concepts to existing GeneType entries:
     * <ul>
     *   <li>geneAdaptation → activates {@link GeneType#DAMAGE_CAP} (enables damage cap and adaptation)</li>
     *   <li>geneBlocksearch → activates {@link GeneType#MIN_DAMAGE} (enables environmental interaction)</li>
     *   <li>geneResidue → activates {@link GeneType#INFECTIOUSNESS} with 0.2 (leaves infectious residue)</li>
     *   <li>geneOrbbox → activates {@link GeneType#PROJECTILE_SPEED} with 1.2 (orb projectile speed)</li>
     * </ul>
     * Override in subclasses to change which GeneType entries are activated.
     */
    protected void activateMalleableGenes() {
        if (geneAdaptation) {
            geneBooleans[GeneType.DAMAGE_CAP.getIndex()] = true;
        }
        if (geneBlocksearch) {
            geneBooleans[GeneType.MIN_DAMAGE.getIndex()] = true;
        }
        if (geneResidue) {
            geneFloats[GeneType.INFECTIOUSNESS.getIndex()] = Math.max(
                    geneFloats[GeneType.INFECTIOUSNESS.getIndex()], 0.2F);
        }
        if (geneOrbbox) {
            geneFloats[GeneType.PROJECTILE_SPEED.getIndex()] = Math.max(
                    geneFloats[GeneType.PROJECTILE_SPEED.getIndex()], 1.2F);
        }
    }

    // ========== Component Override ==========

    /**
     * Creates a CombatComponent with adaptation enabled.
     * Malleable entities use the standard CombatComponent which now tracks
     * per-source-type adaptation via its internal map.
     */
    @Override
    protected CombatComponent createCombatComponent() {
        return new CombatComponent(this);
    }

    // ========== Adaptation API (delegated to CombatComponent) ==========

    /**
     * Returns the current adaptation level for the given damage source.
     * Delegates to CombatComponent's per-source adaptation map.
     *
     * @param source the damage source to query
     * @return adaptation level from 0.0 (no resistance) to max (configurable)
     */
    public float getAdaptationLevel(DamageSource source) {
        if (combatComponent != null) {
            return combatComponent.getAdaptationLevel(source);
        }
        return 0.0F;
    }

    /**
     * Returns the adaptation level for a specific damage source message ID.
     *
     * @param msgId the damage source message ID
     * @return adaptation level from 0.0 to max
     */
    public float getAdaptationLevel(String msgId) {
        if (combatComponent != null) {
            return combatComponent.getAdaptationLevel(msgId);
        }
        return 0.0F;
    }

    /**
     * Returns the overall average adaptation level across all tracked sources.
     */
    public float getAverageAdaptationLevel() {
        if (combatComponent != null) {
            return combatComponent.getAverageAdaptationLevel();
        }
        return 0.0F;
    }

    /**
     * Clears all adaptation tracking, resetting resistance to zero for all
     * damage sources. Called on certain status changes or evolution events.
     */
    public void resetAdaptation() {
        if (combatComponent != null) {
            combatComponent.resetAdaptation();
        }
    }

    /**
     * Returns true if this entity has significant adaptation to any damage type.
     * Used by AI to decide whether to use adapted tactics.
     */
    public boolean isAdapted() {
        if (combatComponent != null) {
            return combatComponent.isAdapted();
        }
        return false;
    }

    /**
     * Returns true if this entity has high adaptation to the specific damage source type.
     *
     * @param source the damage source to check
     * @return true if adaptation to this source exceeds 0.4
     */
    public boolean isHighlyAdaptedTo(DamageSource source) {
        if (combatComponent != null) {
            return combatComponent.isHighlyAdaptedTo(source);
        }
        return false;
    }

    // ========== Tick Override ==========

    @Override
    public void tick() {
        super.tick();
        // Adaptation ticking is now handled by CombatComponent.tick() 
        // which is called from EntityParasiteBase.tick()
    }

    // ========== Damage Override ==========

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // Apply adaptation-based damage reduction from CombatComponent
        // (CombatComponent.onHurt() is called after super.hurt() succeeds
        //  in EntityParasiteBase, but the adaptation reduction is applied 
        //  in CombatComponent.applyDamageCap() which is called BEFORE super.hurt())
        // The per-source adaptation in CombatComponent handles the reduction.
        
        // For malleable entities with geneAdaptation, ensure the adaptation
        // reduction is also applied here as an additional defense layer
        if (geneAdaptation && source != null && combatComponent != null) {
            float adaptation = combatComponent.getAdaptationLevel(source);
            if (adaptation > 0.0F) {
                amount *= (1.0F - adaptation);
            }
        }

        return super.hurt(source, amount);
    }

    // ========== Attribute Supplier ==========

    /**
     * Creates the default attribute supplier for malleable parasite entities.
     * Provides slightly stronger base stats than EntityParasiteBase.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.ARMOR, 2.0)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.0)
                .add(Attributes.ATTACK_SPEED, 1.0);
    }

    // ========== NBT Save/Load ==========

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        // Malleable gene flags (adaptation map is saved by CombatComponent via EntityParasiteBase)
        tag.putBoolean("GeneAdaptation", geneAdaptation);
        tag.putBoolean("GeneBlocksearch", geneBlocksearch);
        tag.putBoolean("GeneResidue", geneResidue);
        tag.putBoolean("GeneOrbbox", geneOrbbox);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        // Load malleable gene flags (adaptation map is loaded by CombatComponent via EntityParasiteBase)
        if (tag.contains("GeneAdaptation")) geneAdaptation = tag.getBoolean("GeneAdaptation");
        if (tag.contains("GeneBlocksearch")) geneBlocksearch = tag.getBoolean("GeneBlocksearch");
        if (tag.contains("GeneResidue")) geneResidue = tag.getBoolean("GeneResidue");
        if (tag.contains("GeneOrbbox")) geneOrbbox = tag.getBoolean("GeneOrbbox");
    }

    // ========== Accessors ==========

    /** Returns whether the adaptation gene is active. */
    public boolean hasGeneAdaptation() { return geneAdaptation; }

    /** Returns whether the blocksearch gene is active. */
    public boolean hasGeneBlocksearch() { return geneBlocksearch; }

    /** Returns whether the residue gene is active. */
    public boolean hasGeneResidue() { return geneResidue; }

    /** Returns whether the orbbox gene is active. */
    public boolean hasGeneOrbbox() { return geneOrbbox; }
}
