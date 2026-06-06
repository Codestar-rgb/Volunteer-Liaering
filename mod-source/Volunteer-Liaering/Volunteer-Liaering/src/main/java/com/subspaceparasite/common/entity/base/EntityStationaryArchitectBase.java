package com.subspaceparasite.common.entity.base;

import net.minecraft.world.entity.Entity;
import java.util.ArrayList;
import java.util.List;

import com.subspaceparasite.api.parasite.GeneType;
import com.subspaceparasite.common.entity.ai.ParasiteRangedAttackGoal;
import com.subspaceparasite.common.entity.ai.ParasiteSummonGoal;
import com.subspaceparasite.common.entity.ai.misc.EntityCanSummon;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
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
 * Abstract base class for Stationary Architect parasites.
 * <p>
 * Architect parasites are stationary entities that can generate structures
 * and summon other parasites. They form the core of colony nexus systems.
 * <p>
 * Extends {@link EntityStationaryBase} and adds:
 * <ul>
 *   <li>Structure generation capability</li>
 *   <li>Entity summoning via {@link EntityCanSummon}</li>
 *   <li>Stage system for progressive growth</li>
 *   <li>Bomb spawning on taking damage</li>
 * </ul>
 * <p>
 * Base stats:
 * <ul>
 *   <li>Health: 80.0</li>
 *   <li>Attack Damage: 6.0</li>
 *   <li>Movement Speed: 0.00</li>
 *   <li>Armor: 6.0</li>
 *   <li>Follow Range: 32.0</li>
 *   <li>Knockback Resistance: 0.8</li>
 * </ul>
 */
public abstract class EntityStationaryArchitectBase extends EntityStationaryBase
        implements EntityCanSummon {

    // ========== Summon System ==========

    /** Available entity IDs for summoning. */
    protected final List<Integer> mobID = new ArrayList<>();

    /** Point costs for each summonable entity. */
    protected final List<Integer> mobPT = new ArrayList<>();

    /** Total point budget for summoned parasites. */
    protected int totalParasites = 10;

    /** Current number of active summoned parasites. */
    protected int actualParasites = 0;

    // ========== Stage System ==========

    /** Current growth stage (0–4). Higher stages summon stronger entities. */
    protected int stage = 0;

    /** Maximum stage this architect can reach. */
    protected int maxStage = 4;

    // ========== Bomb Spawning ==========

    /** Chance (0.0–1.0) to spawn an orb/bomb when damaged. */
    protected float bombSpawnChance = 0.3F;

    // ========== Constructor ==========

    protected EntityStationaryArchitectBase(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.isLeaderFlag = true;

        // Architect-tier gene activations
        geneBooleans[GeneType.SPECIAL_MOVE.getIndex()] = true;
        geneFloats[GeneType.REGEN_RATE.getIndex()] = Math.max(
                geneFloats[GeneType.REGEN_RATE.getIndex()], 0.5F);
        geneFloats[GeneType.PROJECTILE_SPEED.getIndex()] = Math.max(
                geneFloats[GeneType.PROJECTILE_SPEED.getIndex()], 1.3F);
    }

    // ========== AI Goals ==========

    /**
     * Registers goals for stationary architect-tier parasites.
     * Adds summon and ranged attack goals on top of stationary base goals.
     * Architects don't wander — they stay in place and defend their position
     * by summoning defenders and shooting projectiles.
     */
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new ParasiteSummonGoal(this, 200, 24, 10));
        this.goalSelector.addGoal(2, new ParasiteRangedAttackGoal(this, 60, 30, 1, true));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
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

    // ========== Stage System ==========

    /**
     * Returns the current growth stage of this architect.
     *
     * @return the stage (0–maxStage)
     */
    public int getStage() { return stage; }

    /**
     * Sets the growth stage and updates summon capabilities.
     *
     * @param stage the new stage value
     */
    public void setStage(int stage) {
        this.stage = Math.max(0, Math.min(stage, maxStage));
        onStageChanged();
    }

    /**
     * Advances to the next stage if possible.
     *
     * @return true if the stage was advanced
     */
    public boolean advanceStage() {
        if (stage < maxStage) {
            stage++;
            onStageChanged();
            return true;
        }
        return false;
    }

    /**
     * Called when the stage changes. Override to update stats,
     * summon lists, and structure generation.
     */
    protected void onStageChanged() {
        // Default: increase total parasite budget per stage
        totalParasites = 10 + stage * 5;
    }

    // ========== Structure Generation ==========

    /**
     * Generates a structure at the given position.
     * Override in concrete subclasses for structure-specific generation.
     *
     * @param pos the center position for structure generation
     */
    protected void generateStructure(BlockPos pos) {
        // Default no-op — subclasses implement specific structures
    }

    // ========== Bomb Spawning ==========

    /**
     * Called when the architect takes damage. Has a chance to spawn
     * a defensive bomb/orb projectile at the attacker's location.
     */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean result = super.hurt(source, amount);

        if (result && !this.level().isClientSide && source.getEntity() instanceof LivingEntity attacker) {
            if (this.random.nextFloat() < bombSpawnChance) {
                spawnDefensiveBomb(attacker);
            }
        }

        return result;
    }

    /**
     * Spawns a defensive bomb/orb targeting the given entity.
     * Override in subclasses for specific projectile types.
     *
     * @param target the entity to target with the bomb
     */
    protected void spawnDefensiveBomb(LivingEntity target) {
        // Default: no-op — subclasses implement specific bomb spawning
    }

    // ========== Attribute Supplier ==========

    /**
     * Creates the default attribute supplier for architect-tier parasites.
     * Zero movement speed, high armor and knockback resistance.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 80.0)
                .add(Attributes.MOVEMENT_SPEED, 0.00)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
                .add(Attributes.ATTACK_SPEED, 1.0);
    }

    // ========== NBT Save/Load ==========

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        tag.putInt("TotalParasites", totalParasites);
        tag.putInt("ActualParasites", actualParasites);
        tag.putInt("Stage", stage);
        tag.putInt("MaxStage", maxStage);
        tag.putFloat("BombSpawnChance", bombSpawnChance);

        tag.putInt("MobIDCount", mobID.size());
        for (int i = 0; i < mobID.size(); i++) {
            tag.putInt("ArchMobID" + i, mobID.get(i));
            tag.putInt("ArchMobPT" + i, mobPT.get(i));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        totalParasites = tag.getInt("TotalParasites");
        actualParasites = tag.getInt("ActualParasites");
        stage = tag.getInt("Stage");
        maxStage = tag.contains("MaxStage") ? tag.getInt("MaxStage") : 4;
        bombSpawnChance = tag.contains("BombSpawnChance") ? tag.getFloat("BombSpawnChance") : 0.3F;

        mobID.clear();
        mobPT.clear();
        int count = tag.getInt("MobIDCount");
        for (int i = 0; i < count; i++) {
            mobID.add(tag.getInt("ArchMobID" + i));
            mobPT.add(tag.getInt("ArchMobPT" + i));
        }
    }
}
