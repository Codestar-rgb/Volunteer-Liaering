package com.subspaceparasite.common.entity.base;

import com.subspaceparasite.api.parasite.EvolutionPath;
import com.subspaceparasite.api.parasite.GeneType;
import com.subspaceparasite.common.entity.ai.ParasiteGiveEffectsGoal;
import com.subspaceparasite.common.entity.ai.ParasiteSpecialSkillGoal;
import com.subspaceparasite.common.entity.ai.ParasiteSummonGoal;
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
 * Abstract base class for Abomination-tier parasites.
 * <p>
 * Abominations are multi-part boss entities — the ultimate evolution.
 * They cannot despawn, are always leaders, and possess devastating
 * combat capabilities. They are typically composed of multiple hitbox parts.
 * <p>
 * Type category: {@link EvolutionPath#ABOMINATION}
 * <p>
 * Base stats:
 * <ul>
 *   <li>Health: 300.0</li>
 *   <li>Attack Damage: 30.0</li>
 *   <li>Movement Speed: 0.25</li>
 *   <li>Armor: 24.0</li>
 *   <li>Follow Range: 64.0</li>
 *   <li>Knockback Resistance: 0.8</li>
 * </ul>
 */
public abstract class EntityAbominationBase extends EntityMalleableBase {

    // ========== Constructor ==========

    protected EntityAbominationBase(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.evolutionPath = EvolutionPath.ABOMINATION;
        this.canDespawn = false;
        this.isLeaderFlag = true;
        this.adaptationGainRate = 0.10F;
        this.xpReward = 100;

        // Abomination-tier: all genes active at high values
        geneBooleans[GeneType.SPECIAL_MOVE.getIndex()] = true;
        geneBooleans[GeneType.LOOK_WALL.getIndex()] = true;
        geneBooleans[GeneType.SPRINTING.getIndex()] = true;
        geneBooleans[GeneType.DAMAGE_CAP.getIndex()] = true;
        geneBooleans[GeneType.MIN_DAMAGE.getIndex()] = true;
        geneBooleans[GeneType.WATER_LEAP.getIndex()] = true;
        geneFloats[GeneType.REGEN_RATE.getIndex()] = Math.max(
                geneFloats[GeneType.REGEN_RATE.getIndex()], 1.0F);
        geneFloats[GeneType.ATTACK_SPEED.getIndex()] = Math.max(
                geneFloats[GeneType.ATTACK_SPEED.getIndex()], 1.6F);
        geneFloats[GeneType.ANTI_KNOCKBACK.getIndex()] = Math.max(
                geneFloats[GeneType.ANTI_KNOCKBACK.getIndex()], 0.7F);
        geneFloats[GeneType.POISON_HEALING.getIndex()] = Math.max(
                geneFloats[GeneType.POISON_HEALING.getIndex()], 1.0F);
        geneFloats[GeneType.LEAP_POWER.getIndex()] = Math.max(
                geneFloats[GeneType.LEAP_POWER.getIndex()], 0.8F);
        geneFloats[GeneType.INFECTIOUSNESS.getIndex()] = Math.max(
                geneFloats[GeneType.INFECTIOUSNESS.getIndex()], 0.5F);
        geneFloats[GeneType.PROJECTILE_SPEED.getIndex()] = Math.max(
                geneFloats[GeneType.PROJECTILE_SPEED.getIndex()], 1.5F);

        // Abomination-tier abilities — every ability available
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.SUMMON, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.TELEPORT, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.SELF_DESTRUCT, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.AOE_SLAM, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.BLOCK_BREAK, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.BLOCK_PLACE, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.HEAL_AURA, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.INFECT_AURA, true);
    }

    // ========== AI Goals ==========

    /**
     * Registers goals for abomination-tier parasites.
     * Adds summon, devastating COTH/fear/corrosion aura, and slow wander
     * on top of malleable base goals. Abominations are the ultimate evolution
     * with devastating combat capabilities and area denial.
     */
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ParasiteSummonGoal(this, 100, 28, 12));
        this.goalSelector.addGoal(3, new ParasiteSpecialSkillGoal(this));
        this.goalSelector.addGoal(6, new ParasiteGiveEffectsGoal(this, 60, 16, new String[]{"COTH", "VIRULENCE", "FEAR", "CORROSION", "BLEED"}));
        this.goalSelector.addGoal(7, new ParasiteWanderGoal(this, 0.7, 0.5F, true));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    // ========== Attribute Supplier ==========

    /**
     * Creates the default attribute supplier for abomination-tier parasites.
     * Boss-level stats with extreme health, damage, and armor.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 300.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 30.0)
                .add(Attributes.ARMOR, 24.0)
                .add(Attributes.FOLLOW_RANGE, 64.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
                .add(Attributes.ATTACK_SPEED, 1.6);
    }
}
