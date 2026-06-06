package com.subspaceparasite.common.entity.base;

import java.util.List;

import com.subspaceparasite.api.parasite.EvolutionPath;
import com.subspaceparasite.api.parasite.GeneType;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.phys.AABB;

/**
 * Abstract base class for Crude-tier parasites.
 * <p>
 * Crude parasites are early colony structures and immature forms.
 * They can spread a COTH aura to nearby entities, acting as the
 * infection spreaders of the colony. They are stronger than inborn
 * but weaker than primitive forms.
 * <p>
 * Unlike malleable parasites, crude do NOT extend {@link EntityMalleableBase}
 * — they extend {@link EntityParasiteBase} directly.
 * <p>
 * Type category: {@link EvolutionPath#CRUDE}
 * <p>
 * Base stats:
 * <ul>
 *   <li>Health: 25.0</li>
 *   <li>Attack Damage: 4.0</li>
 *   <li>Movement Speed: 0.25</li>
 *   <li>Armor: 2.0</li>
 *   <li>Follow Range: 28.0</li>
 *   <li>Knockback Resistance: 0.0</li>
 * </ul>
 */
public abstract class EntityCrudeBase extends EntityParasiteBase {

    /** Range of the COTH aura in blocks. */
    protected float cothAuraRange = 6.0F;

    /** Ticks between COTH aura applications. */
    protected int cothAuraInterval = 40;

    // ========== Constructor ==========

    protected EntityCrudeBase(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.evolutionPath = EvolutionPath.CRUDE;

        // Crude-tier gene activations — moderate combat, strong infection
        geneBooleans[GeneType.MIN_DAMAGE.getIndex()] = true;
        geneFloats[GeneType.INFECTIOUSNESS.getIndex()] = Math.max(
                geneFloats[GeneType.INFECTIOUSNESS.getIndex()], 0.4F);
        geneFloats[GeneType.REGEN_RATE.getIndex()] = Math.max(
                geneFloats[GeneType.REGEN_RATE.getIndex()], 0.1F);

        // Crude-tier abilities — COTH spreading
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.INFECT_AURA, true);
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

    // ========== COTH Aura ==========

    /**
     * Spreads COTH infection to nearby non-parasite entities.
     * Called periodically based on {@link #cothAuraInterval}.
     */
    protected void spreadCOTHAura() {
        if (this.level().isClientSide) return;
        if (infectionComponent == null) return;
        if (!infectionComponent.canSpread()) return;

        AABB area = this.getBoundingBox().inflate(cothAuraRange);
        List<LivingEntity> nearby = this.level().getEntitiesOfClass(
                LivingEntity.class, area,
                e -> e != this && e.isAlive() && !(e instanceof EntityParasiteBase));

        for (LivingEntity target : nearby) {
            infectionComponent.spreadCOTH(target);
        }
    }

    // ========== Tick Override ==========

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide && this.srpTicks % cothAuraInterval == 0) {
            spreadCOTHAura();
        }
    }

    // ========== Attribute Supplier ==========

    /**
     * Creates the default attribute supplier for crude-tier parasites.
     * Moderate stats — stronger than inborn but weaker than primitive.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 25.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.ARMOR, 2.0)
                .add(Attributes.FOLLOW_RANGE, 28.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.0)
                .add(Attributes.ATTACK_SPEED, 1.0);
    }

    // ========== Accessors ==========

    /** Returns the COTH aura range in blocks. */
    public float getCothAuraRange() { return cothAuraRange; }

    /** Sets the COTH aura range. */
    public void setCothAuraRange(float range) { this.cothAuraRange = range; }
}
