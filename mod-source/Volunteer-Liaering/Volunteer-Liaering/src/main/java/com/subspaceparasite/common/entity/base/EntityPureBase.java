package com.subspaceparasite.common.entity.base;

import java.util.ArrayList;
import java.util.List;

import com.subspaceparasite.api.parasite.EvolutionPath;
import com.subspaceparasite.api.parasite.GeneType;
import com.subspaceparasite.common.entity.ai.ParasiteGiveEffectsGoal;
import com.subspaceparasite.common.entity.ai.ParasiteSpecialSkillGoal;
import com.subspaceparasite.common.entity.ai.ParasiteSummonGoal;
import com.subspaceparasite.common.entity.ai.ParasiteWanderGoal;
import com.subspaceparasite.common.entity.ai.misc.EntityCanColony;
import com.subspaceparasite.common.entity.ai.misc.EntityCanSummon;

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
 * Abstract base class for Pure-tier parasites.
 * <p>
 * Pure parasites are fully self-actualised combat forms. They can summon
 * lesser parasites via the {@link EntityCanSummon} interface and participate
 * in the colony system via {@link EntityCanColony}.
 * <p>
 * Type category: {@link EvolutionPath#PURE}
 * <p>
 * Base stats:
 * <ul>
 *   <li>Health: 80.0</li>
 *   <li>Attack Damage: 12.0</li>
 *   <li>Movement Speed: 0.30</li>
 *   <li>Armor: 12.0</li>
 *   <li>Follow Range: 40.0</li>
 *   <li>Knockback Resistance: 0.3</li>
 * </ul>
 */
public abstract class EntityPureBase extends EntityMalleableBase
        implements EntityCanSummon, EntityCanColony {

    // ========== Summon System ==========

    /** Available entity IDs for summoning. */
    protected final List<Integer> mobID = new ArrayList<>();

    /** Point costs for each summonable entity. */
    protected final List<Integer> mobPT = new ArrayList<>();

    /** Total point budget for summoned parasites. */
    protected int totalParasites = 6;

    /** Current number of active summoned parasites. */
    protected int actualParasites = 0;

    // ========== Colony ==========

    /** Whether this entity should only spawn inside colony structures. */
    protected boolean onlySpawnInside = false;

    // ========== Constructor ==========

    protected EntityPureBase(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.evolutionPath = EvolutionPath.PURE;

        // Pure-tier gene activations
        geneBooleans[GeneType.SPECIAL_MOVE.getIndex()] = true;
        geneBooleans[GeneType.LOOK_WALL.getIndex()] = true;
        geneBooleans[GeneType.SPRINTING.getIndex()] = true;
        geneBooleans[GeneType.DAMAGE_CAP.getIndex()] = true;
        geneFloats[GeneType.REGEN_RATE.getIndex()] = Math.max(
                geneFloats[GeneType.REGEN_RATE.getIndex()], 0.3F);
        geneFloats[GeneType.ATTACK_SPEED.getIndex()] = Math.max(
                geneFloats[GeneType.ATTACK_SPEED.getIndex()], 1.3F);
        geneFloats[GeneType.INFECTIOUSNESS.getIndex()] = Math.max(
                geneFloats[GeneType.INFECTIOUSNESS.getIndex()], 0.3F);

        // Pure-tier abilities — summon, AOE slam, block break, teleport, infection aura
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.SUMMON, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.AOE_SLAM, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.BLOCK_BREAK, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.TELEPORT, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.HEAL_AURA, true);
        setAbility(com.subspaceparasite.api.ICanAbility.AbilityType.INFECT_AURA, true);
    }

    // ========== AI Goals ==========

    /**
     * Registers goals for pure-tier parasites.
     * Adds summon lesser parasites, COTH aura, and enhanced wander
     * on top of malleable base goals (melee, base COTH aura, targeting).
     * Pure parasites are fully self-actualised combat forms that can
     * command lesser parasites.
     */
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ParasiteSummonGoal(this, 200, 16, 6));
        this.goalSelector.addGoal(3, new ParasiteSpecialSkillGoal(this));
        this.goalSelector.addGoal(6, new ParasiteGiveEffectsGoal(this, 60, 12, new String[]{"COTH", "VIRULENCE"}));
        this.goalSelector.addGoal(7, new ParasiteWanderGoal(this, 1.0, 0.8F, true));
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
    public List<Integer> getIDList() {
        return mobID;
    }

    @Override
    public List<Integer> getPointList() {
        return mobPT;
    }

    @Override
    public int getTotalParasites() {
        return totalParasites;
    }

    @Override
    public int getActualParasites() {
        return actualParasites;
    }

    @Override
    public void setActualParasites(int count) {
        this.actualParasites = count;
    }

    // ========== EntityCanColony Implementation ==========

    @Override
    public boolean onlySpawnInside() {
        return onlySpawnInside;
    }

    /** Sets whether this entity should only spawn inside colony structures. */
    public void setOnlySpawnInside(boolean value) {
        this.onlySpawnInside = value;
    }

    // ========== Attribute Supplier ==========

    /**
     * Creates the default attribute supplier for pure-tier parasites.
     * Pure stats are roughly 1.5× the adapted baseline.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 80.0)
                .add(Attributes.MOVEMENT_SPEED, 0.30)
                .add(Attributes.ATTACK_DAMAGE, 12.0)
                .add(Attributes.ARMOR, 12.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3)
                .add(Attributes.ATTACK_SPEED, 1.3);
    }

    // ========== NBT Save/Load ==========

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        tag.putInt("TotalParasites", totalParasites);
        tag.putInt("ActualParasites", actualParasites);
        tag.putBoolean("OnlySpawnInside", onlySpawnInside);

        // Save summon lists
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

        // Load summon lists
        mobID.clear();
        mobPT.clear();
        int count = tag.getInt("MobIDCount");
        for (int i = 0; i < count; i++) {
            mobID.add(tag.getInt("MobID" + i));
            mobPT.add(tag.getInt("MobPT" + i));
        }
    }
}
