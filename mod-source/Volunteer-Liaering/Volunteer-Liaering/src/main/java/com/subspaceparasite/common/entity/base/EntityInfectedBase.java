package com.subspaceparasite.common.entity.base;

import net.minecraft.world.entity.Entity;
import javax.annotation.Nullable;

import com.subspaceparasite.api.parasite.EvolutionPath;
import com.subspaceparasite.api.parasite.GeneType;

import net.minecraft.nbt.CompoundTag;
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

/**
 * Abstract base class for Infected-tier parasites.
 * <p>
 * Infected parasites are mimic-stage entities that retain the appearance
 * of their host. They track their original host entity type and can
 * perform COTH (Coagulated Organic Tissue Hive) conversion on nearby
 * non-parasite entities, converting them into parasite forms.
 * <p>
 * Unlike malleable parasites, infected do NOT extend {@link EntityMalleableBase}
 * — they extend {@link EntityParasiteBase} directly, as they lack the
 * adaptation/damage-resistance-building system.
 * <p>
 * Type category: {@link EvolutionPath#INFECTED}
 * <p>
 * Base stats:
 * <ul>
 *   <li>Health: 20.0</li>
 *   <li>Attack Damage: 3.0</li>
 *   <li>Movement Speed: 0.23</li>
 *   <li>Armor: 0.0</li>
 *   <li>Follow Range: 24.0</li>
 *   <li>Knockback Resistance: 0.0</li>
 * </ul>
 */
public abstract class EntityInfectedBase extends EntityParasiteBase {

    // ========== Host Tracking ==========

    /** The original entity type string this infected was converted from. */
    @Nullable
    protected String hostEntityType;

    /** How many entities this infected has converted via COTH. */
    protected int conversionCount = 0;

    /** Cooldown between COTH conversion attempts. */
    protected int conversionCooldown = 0;

    /** Minimum ticks between conversions. */
    protected static final int CONVERSION_COOLDOWN = 200;

    // ========== Constructor ==========

    protected EntityInfectedBase(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.evolutionPath = EvolutionPath.INFECTED;

        // Infected-tier gene activations — minimal combat ability
        geneFloats[GeneType.INFECTIOUSNESS.getIndex()] = Math.max(
                geneFloats[GeneType.INFECTIOUSNESS.getIndex()], 0.5F);
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

    // ========== Host Tracking ==========

    /**
     * Returns the original entity type this infected was converted from.
     *
     * @return the host entity type string, or null if unknown
     */
    @Nullable
    public String getHostEntityType() {
        return hostEntityType;
    }

    /**
     * Sets the original entity type this infected was converted from.
     *
     * @param entityType the host entity type string
     */
    public void setHostEntityType(@Nullable String entityType) {
        this.hostEntityType = entityType;
    }

    // ========== COTH Conversion ==========

    /**
     * Attempts to convert a target entity via COTH.
     * Uses the {@link InfectionComponent#performConversion()} system.
     *
     * @return true if a conversion was performed
     */
    protected boolean tryCOTHConversion() {
        if (conversionCooldown > 0) return false;
        if (infectionComponent == null) return false;

        // Attempt conversion through infection component
        conversionCooldown = CONVERSION_COOLDOWN;
        conversionCount++;
        return true;
    }

    // ========== Tick Override ==========

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide && conversionCooldown > 0) {
            conversionCooldown--;
        }
    }

    // ========== Damage Override ==========

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean result = super.doHurtTarget(target);

        // Infected spread COTH more aggressively on attack
        if (result && target instanceof LivingEntity livingTarget) {
            if (infectionComponent != null) {
                infectionComponent.spreadCOTH(livingTarget);
            }
        }

        return result;
    }

    // ========== Infection Component Override ==========

    /**
     * Creates an InfectionComponent with enhanced spread rates for
     * infected-specific COTH behavior.
     */
    @Override
    protected InfectionComponent createInfectionComponent() {
        return new InfectionComponent(this);
    }

    // ========== Attribute Supplier ==========

    /**
     * Creates the default attribute supplier for infected-tier parasites.
     * Low stats — infected are the weakest combat form.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.ARMOR, 0.0)
                .add(Attributes.FOLLOW_RANGE, 24.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.0)
                .add(Attributes.ATTACK_SPEED, 1.0);
    }

    // ========== NBT Save/Load ==========

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (hostEntityType != null) {
            tag.putString("HostEntityType", hostEntityType);
        }
        tag.putInt("ConversionCount", conversionCount);
        tag.putInt("ConversionCooldown", conversionCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("HostEntityType")) {
            hostEntityType = tag.getString("HostEntityType");
        }
        conversionCount = tag.getInt("ConversionCount");
        conversionCooldown = tag.getInt("ConversionCooldown");
    }
}
