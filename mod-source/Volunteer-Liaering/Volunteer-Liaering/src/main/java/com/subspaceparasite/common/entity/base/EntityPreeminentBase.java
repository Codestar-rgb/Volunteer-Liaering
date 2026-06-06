package com.subspaceparasite.common.entity.base;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import java.util.ArrayList;
import java.util.List;

import com.subspaceparasite.api.parasite.EvolutionPath;
import com.subspaceparasite.api.parasite.GeneType;
import com.subspaceparasite.common.entity.ai.ParasiteFollowLeaderGoal;
import com.subspaceparasite.common.entity.ai.ParasiteGiveEffectsGoal;
import com.subspaceparasite.common.entity.ai.ParasiteSpecialSkillGoal;
import com.subspaceparasite.common.entity.ai.ParasiteSummonGoal;
import com.subspaceparasite.common.entity.ai.ParasiteWanderGoal;
import com.subspaceparasite.common.entity.ai.misc.EntityCanColony;
import com.subspaceparasite.common.entity.ai.misc.EntityCanSummon;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/**
 * Abstract base class for Preeminent-tier parasites.
 * <p>
 * Preeminents are elite combat forms — commanders of lesser parasites.
 * They can summon entities, participate in the colony system, and emit an
 * aura that buffs nearby parasites with damage resistance and speed.
 * <p>
 * Type category: {@link EvolutionPath#PREEMINENT}
 * <p>
 * Base stats:
 * <ul>
 *   <li>Health: 120.0</li>
 *   <li>Attack Damage: 18.0</li>
 *   <li>Movement Speed: 0.35</li>
 *   <li>Armor: 16.0</li>
 *   <li>Follow Range: 48.0</li>
 *   <li>Knockback Resistance: 0.4</li>
 * </ul>
 * <p>
 * Aura system: Every 60 ticks (3 seconds), applies damage boost and speed
 * to all {@link EntityParasiteBase} entities within 16 blocks.
 */
public abstract class EntityPreeminentBase extends EntityMalleableBase
        implements EntityCanSummon, EntityCanColony {

    // ========== Summon System ==========

    protected final List<Integer> mobID = new ArrayList<>();
    protected final List<Integer> mobPT = new ArrayList<>();
    protected int totalParasites = 8;
    protected int actualParasites = 0;

    // ========== Colony ==========

    protected boolean onlySpawnInside = false;

    // ========== Aura System ==========

    /** Range of the passive buff aura in blocks. */
    protected float auraRange = 16.0F;

    /** Ticks between aura applications. */
    protected int auraInterval = 60;

    /** Internal tick counter for aura application. */
    protected int auraTickCounter = 0;

    // ========== Constructor ==========

    protected EntityPreeminentBase(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.evolutionPath = EvolutionPath.PREEMINENT;
        this.isLeaderFlag = true;
        this.adaptationGainRate = 0.07F;

        // Preeminent-tier gene activations
        geneBooleans[GeneType.SPECIAL_MOVE.getIndex()] = true;
        geneBooleans[GeneType.LOOK_WALL.getIndex()] = true;
        geneBooleans[GeneType.SPRINTING.getIndex()] = true;
        geneBooleans[GeneType.DAMAGE_CAP.getIndex()] = true;
        geneBooleans[GeneType.MIN_DAMAGE.getIndex()] = true;
        geneFloats[GeneType.REGEN_RATE.getIndex()] = Math.max(
                geneFloats[GeneType.REGEN_RATE.getIndex()], 0.4F);
        geneFloats[GeneType.ATTACK_SPEED.getIndex()] = Math.max(
                geneFloats[GeneType.ATTACK_SPEED.getIndex()], 1.4F);
        geneFloats[GeneType.ANTI_KNOCKBACK.getIndex()] = Math.max(
                geneFloats[GeneType.ANTI_KNOCKBACK.getIndex()], 0.3F);

        // Preeminent-tier abilities — full combat suite
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.SUMMON, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.TELEPORT, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.AOE_SLAM, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.BLOCK_BREAK, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.HEAL_AURA, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.INFECT_AURA, true);
    }

    // ========== AI Goals ==========

    /**
     * Registers goals for preeminent-tier parasites.
     * Adds summon, enhanced COTH/damage aura, and leader follow goals
     * on top of malleable base goals. Preeminents are elite combat forms
     * that command lesser parasites and buff nearby allies.
     */
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ParasiteSummonGoal(this, 160, 20, 8));
        this.goalSelector.addGoal(3, new ParasiteSpecialSkillGoal(this));
        this.goalSelector.addGoal(5, new ParasiteFollowLeaderGoal(this, 1.0, 3.0, 16.0, true));
        this.goalSelector.addGoal(6, new ParasiteGiveEffectsGoal(this, 60, 16, new String[]{"COTH", "VIRULENCE", "FEAR"}));
        this.goalSelector.addGoal(7, new ParasiteWanderGoal(this, 1.0, 0.8F, true));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    // ========== Aura System ==========

    /**
     * Applies buff effects to nearby parasite entities.
     * Called every {@link #auraInterval} ticks.
     * Grants damage boost and movement speed to allies within range.
     */
    protected void applyAura() {
        if (this.level().isClientSide) return;

        AABB area = this.getBoundingBox().inflate(auraRange);
        List<EntityParasiteBase> nearby = this.level().getEntitiesOfClass(
                EntityParasiteBase.class, area, p -> p != this && p.isAlive());

        for (EntityParasiteBase parasite : nearby) {
            // Damage Boost I for 3 seconds
            parasite.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60, 0));
            // Movement Speed I for 3 seconds
            parasite.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 0));
        }
    }

    // ========== Tick Override ==========

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            auraTickCounter++;
            if (auraTickCounter >= auraInterval) {
                auraTickCounter = 0;
                applyAura();
            }
        }
    }

    // ========== EntityCanSummon Implementation ==========

    @Override
    public void addID(int id, int points) {
        mobID.add(id);
        mobPT.add(points);
    }

    @Override
    public boolean IDable() {
        return !mobID.isEmpty();
    }

    @Override
    public boolean checkID() {
        return !mobID.isEmpty() && actualParasites < totalParasites;
    }

    @Override
    public List<Integer> getIDList() { return mobID; }

    @Override
    public List<Integer> getPointList() { return mobPT; }

    @Override
    public int getTotalParasites() { return totalParasites; }

    @Override
    public int getActualParasites() { return actualParasites; }

    @Override
    public void setActualParasites(int count) { this.actualParasites = count; }

    // ========== EntityCanColony Implementation ==========

    @Override
    public boolean onlySpawnInside() { return onlySpawnInside; }

    public void setOnlySpawnInside(boolean value) { this.onlySpawnInside = value; }

    // ========== Attribute Supplier ==========

    /**
     * Creates the default attribute supplier for preeminent-tier parasites.
     * Preeminent stats are roughly 1.5× the pure baseline.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 120.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.ATTACK_DAMAGE, 18.0)
                .add(Attributes.ARMOR, 16.0)
                .add(Attributes.FOLLOW_RANGE, 48.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.4)
                .add(Attributes.ATTACK_SPEED, 1.4);
    }

    // ========== NBT Save/Load ==========

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        tag.putInt("TotalParasites", totalParasites);
        tag.putInt("ActualParasites", actualParasites);
        tag.putBoolean("OnlySpawnInside", onlySpawnInside);
        tag.putFloat("AuraRange", auraRange);
        tag.putInt("AuraInterval", auraInterval);
        tag.putInt("AuraTickCounter", auraTickCounter);

        tag.putInt("MobIDCount", mobID.size());
        for (int i = 0; i < mobID.size(); i++) {
            tag.putInt("MobID" + i, mobID.get(i));
            tag.putInt("MobPT" + i, mobPT.get(i));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        totalParasites = tag.getInt("TotalParasites");
        actualParasites = tag.getInt("ActualParasites");
        onlySpawnInside = tag.getBoolean("OnlySpawnInside");
        auraRange = tag.contains("AuraRange") ? tag.getFloat("AuraRange") : 16.0F;
        auraInterval = tag.contains("AuraInterval") ? tag.getInt("AuraInterval") : 60;
        auraTickCounter = tag.getInt("AuraTickCounter");

        mobID.clear();
        mobPT.clear();
        int count = tag.getInt("MobIDCount");
        for (int i = 0; i < count; i++) {
            mobID.add(tag.getInt("MobID" + i));
            mobPT.add(tag.getInt("MobPT" + i));
        }
    }
}
