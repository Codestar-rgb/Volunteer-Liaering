package com.subspaceparasite.common.entity.base;

import javax.annotation.Nullable;

import com.subspaceparasite.api.parasite.EvolutionPath;
import com.subspaceparasite.api.parasite.GeneType;
import com.subspaceparasite.common.entity.ai.ParasiteWanderGoal;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Abstract base class for Feral-tier parasites.
 * <p>
 * Feral parasites are fully transformed host bodies — they no longer
 * disguise themselves as their original host species. Their stats are
 * variable depending on which vanilla mob type they were converted from.
 * <p>
 * Type category: {@link EvolutionPath#FERAL}
 * <p>
 * Base stats (variable — override in concrete subclasses):
 * <ul>
 *   <li>Health: 40.0 (base, varies by mob type)</li>
 *   <li>Attack Damage: 6.0 (base, varies by mob type)</li>
 *   <li>Movement Speed: 0.28</li>
 *   <li>Armor: 2.0</li>
 *   <li>Follow Range: 32.0</li>
 *   <li>Knockback Resistance: 0.1</li>
 * </ul>
 * <p>
 * Tracks the original vanilla mob type for conversion lookups.
 */
public abstract class EntityFeralBase extends EntityMalleableBase {

    /** The original vanilla entity type this feral parasite was converted from. */
    @Nullable
    protected String hostEntityType;

    // ========== Constructor ==========

    protected EntityFeralBase(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.evolutionPath = EvolutionPath.FERAL;

        // Feral-tier gene activations
        geneBooleans[GeneType.LOOK_WALL.getIndex()] = true;
        geneBooleans[GeneType.SPRINTING.getIndex()] = true;
        geneFloats[GeneType.REGEN_RATE.getIndex()] = Math.max(
                geneFloats[GeneType.REGEN_RATE.getIndex()], 0.15F);
        geneFloats[GeneType.ATTACK_SPEED.getIndex()] = Math.max(
                geneFloats[GeneType.ATTACK_SPEED.getIndex()], 1.1F);
    }

    // ========== AI Goals ==========

    /**
     * Registers goals for feral-tier parasites.
     * Adds aggressive melee and fast wander on top of malleable base goals.
     * Ferals are fully transformed — they charge and attack aggressively.
     */
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(7, new ParasiteWanderGoal(this, 1.2, 0.6F, true));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    // ========== Host Tracking ==========

    /**
     * Returns the original vanilla entity type string this feral was converted from.
     *
     * @return the host entity type string, or null if unknown
     */
    @Nullable
    public String getHostEntityType() {
        return hostEntityType;
    }

    /**
     * Sets the original vanilla entity type this feral was converted from.
     *
     * @param entityType the host entity type string
     */
    public void setHostEntityType(@Nullable String entityType) {
        this.hostEntityType = entityType;
    }

    // ========== Attribute Supplier ==========

    /**
     * Creates the default attribute supplier for feral-tier parasites.
     * Base stats are moderate — concrete subclasses should override with
     * mob-type-specific values.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.ARMOR, 2.0)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.1)
                .add(Attributes.ATTACK_SPEED, 1.0);
    }

    // ========== NBT Save/Load ==========

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (hostEntityType != null) {
            tag.putString("HostEntityType", hostEntityType);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("HostEntityType")) {
            hostEntityType = tag.getString("HostEntityType");
        }
    }
}
