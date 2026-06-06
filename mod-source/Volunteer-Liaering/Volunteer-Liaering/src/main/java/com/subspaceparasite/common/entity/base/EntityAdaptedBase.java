package com.subspaceparasite.common.entity.base;

import com.subspaceparasite.api.parasite.EvolutionPath;
import com.subspaceparasite.api.parasite.GeneType;
import com.subspaceparasite.common.entity.ai.ParasiteBlockInfestGoal;
import com.subspaceparasite.common.entity.ai.ParasiteSkillGoal;
import com.subspaceparasite.common.entity.ai.ParasiteSpecialSkillGoal;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Abstract base class for Adapted-tier parasites.
 * <p>
 * Adapted parasites are evolved primitives — stronger in every stat and
 * possessing a higher adaptation rate. They have been through enough combat
 * to have refined their damage resistance learning.
 * <p>
 * Type category: {@link EvolutionPath#ADAPTED}
 * <p>
 * Base stats:
 * <ul>
 *   <li>Health: 50.0</li>
 *   <li>Attack Damage: 8.0</li>
 *   <li>Movement Speed: 0.30</li>
 *   <li>Armor: 8.0</li>
 *   <li>Follow Range: 36.0</li>
 *   <li>Knockback Resistance: 0.2</li>
 * </ul>
 * <p>
 * Adaptation gain rate is 50% higher than primitive tier (0.06 vs 0.04).
 */
public abstract class EntityAdaptedBase extends EntityMalleableBase {

    protected EntityAdaptedBase(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.evolutionPath = EvolutionPath.ADAPTED;

        // Higher adaptation rate for adapted tier
        this.adaptationGainRate = 0.06F;

        // Adapted-tier gene activations
        geneBooleans[GeneType.SPECIAL_MOVE.getIndex()] = true;
        geneBooleans[GeneType.LOOK_WALL.getIndex()] = true;
        geneBooleans[GeneType.SPRINTING.getIndex()] = true;
        geneFloats[GeneType.REGEN_RATE.getIndex()] = Math.max(
                geneFloats[GeneType.REGEN_RATE.getIndex()], 0.2F);
        geneFloats[GeneType.ATTACK_SPEED.getIndex()] = Math.max(
                geneFloats[GeneType.ATTACK_SPEED.getIndex()], 1.2F);

        // Adapted-tier abilities — AOE slam (SPECIAL_MOVE gene) and block breaking
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.AOE_SLAM, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.BLOCK_BREAK, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.INFECT_AURA, true);
    }

    // ========== AI Goals ==========

    /**
     * Registers goals for adapted-tier parasites.
     * Adds block infestation and special skill goals on top of
     * malleable base goals (melee, COTH aura, wander, targeting).
     */
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new ParasiteBlockInfestGoal(this, 2));
        this.goalSelector.addGoal(3, new ParasiteSpecialSkillGoal(this));
        this.goalSelector.addGoal(4, new ParasiteSkillGoal(this, 120, 3.0F, 8.0F, (byte) 1, true));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    /**
     * Creates the default attribute supplier for adapted-tier parasites.
     * Adapted stats are approximately 1.5–2× the primitive baseline.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 50.0)
                .add(Attributes.MOVEMENT_SPEED, 0.30)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.ARMOR, 8.0)
                .add(Attributes.FOLLOW_RANGE, 36.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.2)
                .add(Attributes.ATTACK_SPEED, 1.2);
    }
}
