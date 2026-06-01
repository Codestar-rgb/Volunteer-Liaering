package com.subspaceparasite.common.entity.base;

import javax.annotation.Nullable;

import com.subspaceparasite.api.parasite.EvolutionPath;
import com.subspaceparasite.api.parasite.GeneType;
import com.subspaceparasite.common.entity.ai.ParasiteRangedAttackGoal;
import com.subspaceparasite.common.entity.ai.ParasiteWanderGoal;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Abstract base class for Hijacked-tier parasites.
 * <p>
 * Hijacked parasites are non-standard hosts whose abilities have been
 * co-opted by the parasite. They retain special attack patterns from
 * their original vanilla mob abilities (e.g., Blaze fireballs,
 * Golem knockback, Skeleton archery).
 * <p>
 * Type category: {@link EvolutionPath#HIJACKED}
 * <p>
 * Base stats (enhanced from vanilla mob stats):
 * <ul>
 *   <li>Health: 50.0</li>
 *   <li>Attack Damage: 8.0</li>
 *   <li>Movement Speed: 0.28</li>
 *   <li>Armor: 4.0</li>
 *   <li>Follow Range: 36.0</li>
 *   <li>Knockback Resistance: 0.2</li>
 * </ul>
 */
public abstract class EntityHijackedBase extends EntityMalleableBase {

    /** The original vanilla entity type this hijacked parasite was converted from. */
    @Nullable
    protected String hijackedFromType;

    // ========== Constructor ==========

    protected EntityHijackedBase(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.evolutionPath = EvolutionPath.HIJACKED;

        // Hijacked-tier gene activations — retain host abilities
        geneBooleans[GeneType.SPECIAL_MOVE.getIndex()] = true;
        geneBooleans[GeneType.LOOK_WALL.getIndex()] = true;
        geneFloats[GeneType.REGEN_RATE.getIndex()] = Math.max(
                geneFloats[GeneType.REGEN_RATE.getIndex()], 0.15F);
        geneFloats[GeneType.INFECTIOUSNESS.getIndex()] = Math.max(
                geneFloats[GeneType.INFECTIOUSNESS.getIndex()], 0.4F);
    }

    // ========== AI Goals ==========

    /**
     * Registers goals for hijacked-tier parasites.
     * Adds ranged attack (mimicking vanilla host abilities) and wander
     * on top of malleable base goals. Hijacked entities retain their
     * host's attack patterns via performRangedAttack/performHijackedSpecialAttack.
     */
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new ParasiteRangedAttackGoal(this, 40, 20, 1, false));
        this.goalSelector.addGoal(7, new ParasiteWanderGoal(this, 1.0, 0.8F, true));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    // ========== Hijacked Host Tracking ==========

    /**
     * Returns the vanilla entity type this parasite hijacked.
     *
     * @return the hijacked entity type string, or null if unknown
     */
    @Nullable
    public String getHijackedFromType() {
        return hijackedFromType;
    }

    /**
     * Sets the vanilla entity type this parasite hijacked.
     *
     * @param entityType the hijacked entity type string
     */
    public void setHijackedFromType(@Nullable String entityType) {
        this.hijackedFromType = entityType;
    }

    /**
     * Executes a special attack pattern derived from the hijacked host's
     * original abilities. Override in concrete subclasses for type-specific
     * special attacks (e.g., Blaze fireballs, Golem throws).
     *
     * @param target the target entity for the special attack
     */
    protected void performHijackedSpecialAttack(LivingEntity target) {
        // Default: no special attack — subclasses override
    }

    @Override
    public void performRangedAttack(net.minecraft.world.entity.LivingEntity target, float velocity) {
        // Default: delegate to hijacked special attack
        performHijackedSpecialAttack(target);
    }

    // ========== Attribute Supplier ==========

    /**
     * Creates the default attribute supplier for hijacked-tier parasites.
     * Stats are enhanced from vanilla mob baselines.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 50.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.FOLLOW_RANGE, 36.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.2)
                .add(Attributes.ATTACK_SPEED, 1.0);
    }

    // ========== NBT Save/Load ==========

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (hijackedFromType != null) {
            tag.putString("HijackedFromType", hijackedFromType);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("HijackedFromType")) {
            hijackedFromType = tag.getString("HijackedFromType");
        }
    }
}
