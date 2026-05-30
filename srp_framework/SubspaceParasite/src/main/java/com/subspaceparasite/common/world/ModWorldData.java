package com.subspaceparasite.common.world;

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
 * and phase progression timers.
 */
public class ModWorldData extends SavedData {

    private static final String DATA_NAME = "subspaceparasite_worlddata";

    private EvoPhase currentPhase;
    private int phaseProgressionTimer;
    private final Map<String, Double> killStats;
    private final Map<UUID, BlockPos> colonies;
    private final Map<BlockPos, Integer> colonyUnits;
    private long tickCounter;

    public ModWorldData() {
        this.currentPhase = EvoPhase.ZERO;
        this.phaseProgressionTimer = 0;
        this.killStats = new ConcurrentHashMap<>();
        this.colonies = new ConcurrentHashMap<>();
        this.colonyUnits = new ConcurrentHashMap<>();
        this.tickCounter = 0;
    }

    public static ModWorldData load(CompoundTag tag) {
        ModWorldData data = new ModWorldData();
        data.currentPhase = EvoPhase.getByPhaseNumber(tag.getInt("CurrentPhase"));
        data.phaseProgressionTimer = tag.getInt("PhaseProgressionTimer");
        data.tickCounter = tag.getLong("TickCounter");

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
        // Only mark dirty at intervals to reduce I/O overhead
        // (setDirty() triggers a save to disk, so avoid calling every tick)
        if (tickCounter % 600 == 0) {
            this.setDirty();
        }
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
        currentPhase = EvoPhase.ZERO;
        phaseProgressionTimer = 0;
        killStats.clear();
        colonies.clear();
        colonyUnits.clear();
        tickCounter = 0;
        this.setDirty();
    }
}
