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
 * Abstract base class for Primitive-tier parasites.
 * <p>
 * Primitives are the first true combat forms — colony defenders that have
 * moved beyond the crude and inborn stages. They possess the adaptation
 * system from {@link EntityMalleableBase} and gain additional combat AI.
 * <p>
 * Type category: {@link EvolutionPath#PRIMITIVE}
 * <p>
 * Base stats:
 * <ul>
 *   <li>Health: 30.0</li>
 *   <li>Attack Damage: 5.0</li>
 *   <li>Movement Speed: 0.25</li>
 *   <li>Armor: 4.0</li>
 *   <li>Follow Range: 32.0</li>
 *   <li>Knockback Resistance: 0.1</li>
 * </ul>
 */
public abstract class EntityPrimitiveBase extends EntityMalleableBase {

    protected EntityPrimitiveBase(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.evolutionPath = EvolutionPath.PRIMITIVE;

        // Primitive-tier gene activations
        geneBooleans[GeneType.LOOK_WALL.getIndex()] = true;
        geneFloats[GeneType.REGEN_RATE.getIndex()] = Math.max(
                geneFloats[GeneType.REGEN_RATE.getIndex()], 0.1F);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        // Primitive-specific AI: water avoidance, enhanced targeting
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    /**
     * Creates the default attribute supplier for primitive-tier parasites.
     * Primitive stats represent the baseline combat form — stronger than
     * inborn/crude but weaker than adapted.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.1)
                .add(Attributes.ATTACK_SPEED, 1.0);
    }
}
