package com.subspaceparasite.common.entity.base;

import com.subspaceparasite.api.parasite.EvolutionPath;
import com.subspaceparasite.api.parasite.GeneType;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Abstract base class for Inborn-tier parasites.
 * <p>
 * Inborn parasites are born from the colony — they are not converted hosts
 * but naturally occurring parasite forms. They represent the baseline
 * colony-produced entities with minimal combat ability but foundational
 * behavior patterns.
 * <p>
 * Unlike malleable parasites, inborn do NOT extend {@link EntityMalleableBase}
 * — they extend {@link EntityParasiteBase} directly.
 * <p>
 * Type category: {@link EvolutionPath#INBORN}
 * <p>
 * Base stats:
 * <ul>
 *   <li>Health: 20.0</li>
 *   <li>Attack Damage: 3.0</li>
 *   <li>Movement Speed: 0.25</li>
 *   <li>Armor: 0.0</li>
 *   <li>Follow Range: 24.0</li>
 *   <li>Knockback Resistance: 0.0</li>
 * </ul>
 */
public abstract class EntityInbornBase extends EntityParasiteBase {

    // ========== Constructor ==========

    protected EntityInbornBase(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.evolutionPath = EvolutionPath.INBORN;

        // Inborn-tier gene activations — base combat genes
        geneBooleans[GeneType.MIN_DAMAGE.getIndex()] = true;
        geneBooleans[GeneType.DAMAGE_CAP.getIndex()] = true;
        geneFloats[GeneType.REGEN_RATE.getIndex()] = Math.max(
                geneFloats[GeneType.REGEN_RATE.getIndex()], 0.05F);

        // Inborn-tier abilities — passive healing aura
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.HEAL_AURA, true);
    }

    // ========== AI Goals ==========

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    // ========== Attribute Supplier ==========

    /**
     * Creates the default attribute supplier for inborn-tier parasites.
     * Minimal stats — inborn are the weakest combat-capable forms.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.ARMOR, 0.0)
                .add(Attributes.FOLLOW_RANGE, 24.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.0)
                .add(Attributes.ATTACK_SPEED, 1.0);
    }
}
