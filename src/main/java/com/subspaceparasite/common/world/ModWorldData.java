package com.subspaceparasite.common.world;

import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.config.ModConfigSystems;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SavedData extension for per-dimension world state tracking.
 * Manages the global evolution phase, kill statistics, colony data,
 * phase progression timers, and SRP difficulty setting.
 */
public class ModWorldData extends SavedData {

    private static final String DATA_NAME = "subspaceparasite_worlddata";

    private EvoPhase currentPhase;
    private int phaseProgressionTimer;
    private final Map<String, Double> killStats;
    private final Map<UUID, BlockPos> colonies;
    private final Map<BlockPos, Integer> colonyUnits;
    private long tickCounter;

    /** Per-world SRP difficulty setting. */
    private SRPDifficultySetting srpDifficulty;

    public ModWorldData() {
        this.currentPhase = EvoPhase.PHASE_0;
        this.phaseProgressionTimer = 0;
        this.killStats = new ConcurrentHashMap<>();
        this.colonies = new ConcurrentHashMap<>();
        this.colonyUnits = new ConcurrentHashMap<>();
        this.tickCounter = 0;
        this.srpDifficulty = SRPDifficultySetting.NORMAL;
    }

    public static ModWorldData load(CompoundTag tag) {
        ModWorldData data = new ModWorldData();
        data.currentPhase = EvoPhase.getByPhaseNumber(tag.getInt("CurrentPhase"));
        data.phaseProgressionTimer = tag.getInt("PhaseProgressionTimer");
        data.tickCounter = tag.getLong("TickCounter");

        // Load SRP difficulty setting
        if (tag.contains("SRPDifficulty")) {
            data.srpDifficulty = SRPDifficultySetting.getById(tag.getInt("SRPDifficulty"));
        } else {
            data.srpDifficulty = SRPDifficultySetting.NORMAL;
        }

        CompoundTag killsTag = tag.getCompound("KillStats");
        for (String key : killsTag.getAllKeys()) {
            data.killStats.put(key, killsTag.getDouble(key));
        }

        ListTag colonyList = tag.getList("Colonies", Tag.TAG_COMPOUND);
        for (int i = 0; i < colonyList.size(); i++) {
            CompoundTag colonyTag = colonyList.getCompound(i);
            UUID leaderId = colonyTag.getUUID("LeaderUUID");
            BlockPos center = new BlockPos(
                    colonyTag.getInt("CenterX"), colonyTag.getInt("CenterY"), colonyTag.getInt("CenterZ"));
            data.colonies.put(leaderId, center);
            if (colonyTag.contains("UnitCount")) {
                data.colonyUnits.put(center, colonyTag.getInt("UnitCount"));
            }
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putInt("CurrentPhase", currentPhase.getPhaseNumber());
        tag.putInt("PhaseProgressionTimer", phaseProgressionTimer);
        tag.putLong("TickCounter", tickCounter);
        tag.putInt("SRPDifficulty", srpDifficulty.getId());

        CompoundTag killsTag = new CompoundTag();
        for (Map.Entry<String, Double> entry : killStats.entrySet()) {
            killsTag.putDouble(entry.getKey(), entry.getValue());
        }
        tag.put("KillStats", killsTag);

        ListTag colonyList = new ListTag();
        for (Map.Entry<UUID, BlockPos> entry : colonies.entrySet()) {
            CompoundTag colonyTag = new CompoundTag();
            colonyTag.putUUID("LeaderUUID", entry.getKey());
            BlockPos center = entry.getValue();
            colonyTag.putInt("CenterX", center.getX());
            colonyTag.putInt("CenterY", center.getY());
            colonyTag.putInt("CenterZ", center.getZ());
            colonyTag.putInt("UnitCount", colonyUnits.getOrDefault(center, 0));
            colonyList.add(colonyTag);
        }
        tag.put("Colonies", colonyList);

        return tag;
    }

    public static ModWorldData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                ModWorldData::load, ModWorldData::new, DATA_NAME);
    }

    /**
     * Static register method called during mod common setup.
     * SavedData auto-registers on first access via computeIfAbsent(),
     * so this method is a no-op placeholder for any future initialization.
     */
    public static void register() {
        // SavedData auto-registers on first get() call via computeIfAbsent().
        // This method exists for explicit registration if needed in the future.
    }

    public void tick() {
        tickCounter++;
        phaseProgressionTimer++;

        // NOTE: Phase progression checking is handled by ForgeEventHandler.onServerTick()
        // which uses setCurrentPhase(ServerLevel, EvoPhase) to properly sync phase changes
        // to ModSaveData. Do NOT check phase progression here, as this method only has
        // access to setCurrentPhase(EvoPhase) which does not sync, causing a desync
        // between ModWorldData and ModSaveData/CelestialManager.

        // Only mark dirty at intervals to reduce I/O overhead
        // (setDirty() triggers a save to disk, so avoid calling every tick)
        if (tickCounter % 600 == 0) {
            this.setDirty();
        }
    }

    /**
     * Checks whether the phase should advance based on total kill statistics.
     * <p>
     * <b>DEAD CODE — DO NOT CALL.</b> This method is NOT called from tick()
     * because phase progression is handled by
     * {@link com.subspaceparasite.handler.ForgeEventHandler#checkPhaseProgression}
     * which properly syncs phase changes to ModSaveData/CelestialManager.
     * Calling this method directly would cause a phase desync between
     * ModWorldData and ModSaveData.
     * <p>
     * Kept for reference; the ForgeEventHandler version uses
     * {@link #setCurrentPhase(ServerLevel, EvoPhase)} for proper sync.
     *
     * @deprecated Use ForgeEventHandler.checkPhaseProgression() instead.
     */
    @Deprecated
    private void checkPhaseProgression() {
        int totalKills = (int) getKillStat("total");
        EvoPhase targetPhase = computePhaseForKills(totalKills);

        if (targetPhase.getPhaseNumber() > currentPhase.getPhaseNumber()) {
            setCurrentPhase(targetPhase);
        }
    }

    /**
     * Computes the appropriate phase for the given total kill count.
     * Uses configurable thresholds from ModConfig.
     *
     * @param totalKills the total number of kills
     * @return the highest phase whose threshold is met
     * @deprecated Only used by the deprecated {@link #checkPhaseProgression()}.
     */
    @Deprecated
    private EvoPhase computePhaseForKills(int totalKills) {
        // Check from highest to lowest phase to find the highest qualifying phase
        // Phases 0-4: Pre-assimilation, Phase 1, Phase 2, Phase 3, Phase 4
        for (int phase = EvoPhase.getMaxPhaseNumber(); phase >= 0; phase--) {
            int threshold = ModConfigSystems.getPhaseKillThreshold(EvoPhase.getByPhaseNumber(phase));
            if (totalKills >= threshold) {
                return EvoPhase.getByPhaseNumber(phase);
            }
        }
        return EvoPhase.PHASE_0;
    }

    public EvoPhase getCurrentPhase() { return currentPhase; }

    public void setCurrentPhase(EvoPhase phase) {
        if (this.currentPhase != phase) {
            this.currentPhase = phase;
            this.phaseProgressionTimer = 0;
            this.setDirty();
            if (ModConfigSystems.shouldLogPhaseChanges()) {
                org.apache.logging.log4j.LogManager.getLogger().info(
                        "World phase changed to: {}", phase);
            }
        }
    }

    /**
     * Sets the current phase and synchronizes the change to ModSaveData.
     * Use this overload when a ServerLevel is available to keep both
     * data stores consistent.
     *
     * @param level the server level (used to sync to ModSaveData)
     * @param phase the new phase
     */
    public void setCurrentPhase(ServerLevel level, EvoPhase phase) {
        if (this.currentPhase != phase) {
            setCurrentPhase(phase);
            // Sync to ModSaveData so CelestialManager and other ModSaveData
            // consumers see the updated phase.
            ModSaveData.get(level).setCurrentPhase(level, phase);
        }
    }

    // ── SRP Difficulty ──

    /**
     * Gets the SRP difficulty setting for this world.
     *
     * @return the current SRP difficulty
     */
    public SRPDifficultySetting getSRPDifficulty() {
        return srpDifficulty;
    }

    /**
     * Sets the SRP difficulty setting for this world.
     *
     * @param difficulty the new difficulty setting
     */
    public void setSRPDifficulty(SRPDifficultySetting difficulty) {
        if (this.srpDifficulty != difficulty) {
            this.srpDifficulty = difficulty;
            this.setDirty();
            SubspaceParasite.LOGGER.info("SRP difficulty changed to: {}", difficulty);
        }
    }

    public int getPhaseProgressionTimer() { return phaseProgressionTimer; }

    public void addKillStat(String key, double amount) {
        killStats.merge(key, amount, Double::sum);
        this.setDirty();
    }

    public double getKillStat(String key) {
        return killStats.getOrDefault(key, 0.0);
    }

    public Set<String> getKillStatKeys() {
        return Collections.unmodifiableSet(killStats.keySet());
    }

    public void addColony(BlockPos center, UUID leaderUUID) {
        colonies.put(leaderUUID, center);
        colonyUnits.put(center, 0);
        this.setDirty();
    }

    public void removeColony(UUID leaderUUID) {
        BlockPos center = colonies.remove(leaderUUID);
        if (center != null) colonyUnits.remove(center);
        this.setDirty();
    }

    public void updateColonyData(BlockPos center, int unitCount, boolean isLeader) {
        if (center != null) colonyUnits.put(center, unitCount);
        this.setDirty();
    }

    public int getColonyCount() { return colonies.size(); }

    public List<BlockPos> getColonyCenters() { return new ArrayList<>(colonies.values()); }

    public boolean isInColonyTerritory(BlockPos pos) {
        for (BlockPos center : colonies.values()) {
            if (center.distManhattan(pos) < 32) return true;
        }
        return false;
    }

    public long getTickCounter() { return tickCounter; }

    public void reset() {
        currentPhase = EvoPhase.PHASE_0;
        phaseProgressionTimer = 0;
        killStats.clear();
        colonies.clear();
        colonyUnits.clear();
        tickCounter = 0;
        srpDifficulty = SRPDifficultySetting.NORMAL;
        this.setDirty();
    }
}
