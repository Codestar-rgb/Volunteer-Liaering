package com.subspaceparasite.common.entity.base;

import java.util.ArrayList;
import java.util.List;

import com.subspaceparasite.api.parasite.GeneType;
import com.subspaceparasite.common.entity.ai.ParasiteRangedAttackGoal;
import com.subspaceparasite.common.entity.ai.ParasiteSummonGoal;

import net.minecraft.core.BlockPos;
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
 * Abstract base class for Dispatcher-tier parasites.
 * <p>
 * Dispatchers are colony nexus structures that store and recall parasites.
 * They generate NexusProtection1 structures and can spawn crude, infected,
 * flesh, and mangler types from their internal reserves.
 * <p>
 * Extends {@link EntityStationaryArchitectBase} and adds:
 * <ul>
 *   <li>Parasite storage — can store and recall specific parasite types</li>
 *   <li>NexusProtection1 structure generation</li>
 *   <li>Ranged attack capability for defense</li>
 * </ul>
 * <p>
 * Base stats:
 * <ul>
 *   <li>Health: 100.0</li>
 *   <li>Attack Damage: 6.0</li>
 *   <li>Movement Speed: 0.00</li>
 *   <li>Armor: 6.0</li>
 *   <li>Follow Range: 48.0</li>
 *   <li>Knockback Resistance: 0.8</li>
 * </ul>
 */
public abstract class EntityDispatcherBase extends EntityStationaryArchitectBase {

    // ========== Stored Parasites ==========

    /** List of stored parasite entity type IDs that can be recalled/spawned. */
    protected final List<Integer> storedParasites = new ArrayList<>();

    /** Count of stored crude-type parasites. */
    protected int storedCrude = 0;

    /** Count of stored infected-type parasites. */
    protected int storedInfected = 0;

    /** Count of stored flesh-type parasites. */
    protected int storedFlesh = 0;

    /** Count of stored mangler-type parasites. */
    protected int storedMangler = 0;

    /** Maximum total stored parasites. */
    protected int maxStoredParasites = 20;

    // ========== Constructor ==========

    protected EntityDispatcherBase(EntityType<? extends Monster> type, Level level) {
        super(type, level);

        // Dispatcher-tier gene activations
        geneBooleans[GeneType.SPECIAL_MOVE.getIndex()] = true;
        geneFloats[GeneType.REGEN_RATE.getIndex()] = Math.max(
                geneFloats[GeneType.REGEN_RATE.getIndex()], 0.6F);
        geneFloats[GeneType.PROJECTILE_SPEED.getIndex()] = Math.max(
                geneFloats[GeneType.PROJECTILE_SPEED.getIndex()], 1.5F);
    }

    // ========== AI Goals ==========

    /**
     * Registers goals for dispatcher-tier parasites.
     * Adds enhanced ranged attack and summon goals on top of
     * stationary architect base goals. Dispatchers store and recall
     * parasites, defending their position with ranged attacks.
     */
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ParasiteSummonGoal(this, 180, 20, 10));
        this.goalSelector.addGoal(2, new ParasiteRangedAttackGoal(this, 40, 20, 2, true));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    // ========== Storage API ==========

    /**
     * Stores a parasite entity type ID for later recall.
     *
     * @param typeId the parasite type ID to store
     * @return true if the parasite was stored (capacity available)
     */
    public boolean storeParasite(int typeId) {
        if (storedParasites.size() >= maxStoredParasites) return false;
        storedParasites.add(typeId);

        // Categorize by type range
        if (typeId >= 0x50 && typeId <= 0x5F) storedCrude++;
        else if (typeId >= 0x00 && typeId <= 0x1F) storedInfected++;
        else if (typeId == 0x45) storedFlesh++;
        else if (typeId == 0x47) storedMangler++;

        return true;
    }

    /**
     * Recalls (removes and returns) a stored parasite type ID.
     *
     * @return the recalled type ID, or -1 if empty
     */
    public int recallParasite() {
        if (storedParasites.isEmpty()) return -1;
        int typeId = storedParasites.remove(storedParasites.size() - 1);

        // Decategorize
        if (typeId >= 0x50 && typeId <= 0x5F) storedCrude = Math.max(0, storedCrude - 1);
        else if (typeId >= 0x00 && typeId <= 0x1F) storedInfected = Math.max(0, storedInfected - 1);
        else if (typeId == 0x45) storedFlesh = Math.max(0, storedFlesh - 1);
        else if (typeId == 0x47) storedMangler = Math.max(0, storedMangler - 1);

        return typeId;
    }

    /**
     * Returns the total number of stored parasites.
     */
    public int getStoredCount() {
        return storedParasites.size();
    }

    /**
     * Returns whether the dispatcher can store more parasites.
     */
    public boolean canStoreMore() {
        return storedParasites.size() < maxStoredParasites;
    }

    // ========== Structure Generation ==========

    /**
     * Generates a NexusProtection1 structure around this dispatcher.
     * Override in concrete subclasses for stage-specific structures.
     *
     * @param pos the center position
     */
    protected void generateNexusProtection1(BlockPos pos) {
        generateStructure(pos);
    }

    // ========== Attribute Supplier ==========

    /**
     * Creates the default attribute supplier for dispatcher-tier parasites.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 100.0)
                .add(Attributes.MOVEMENT_SPEED, 0.00)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.FOLLOW_RANGE, 48.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
                .add(Attributes.ATTACK_SPEED, 1.0);
    }

    // ========== NBT Save/Load ==========

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        tag.putInt("StoredCrude", storedCrude);
        tag.putInt("StoredInfected", storedInfected);
        tag.putInt("StoredFlesh", storedFlesh);
        tag.putInt("StoredMangler", storedMangler);
        tag.putInt("MaxStoredParasites", maxStoredParasites);

        tag.putInt("StoredParasitesCount", storedParasites.size());
        for (int i = 0; i < storedParasites.size(); i++) {
            tag.putInt("StoredP" + i, storedParasites.get(i));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        storedCrude = tag.getInt("StoredCrude");
        storedInfected = tag.getInt("StoredInfected");
        storedFlesh = tag.getInt("StoredFlesh");
        storedMangler = tag.getInt("StoredMangler");
        maxStoredParasites = tag.contains("MaxStoredParasites") ? tag.getInt("MaxStoredParasites") : 20;

        storedParasites.clear();
        int count = tag.getInt("StoredParasitesCount");
        for (int i = 0; i < count; i++) {
            storedParasites.add(tag.getInt("StoredP" + i));
        }
    }
}
