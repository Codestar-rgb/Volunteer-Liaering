package com.subspaceparasite.common.world;

import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.api.parasite.EvoPhase;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Per-dimension SavedData tracking evolution points, current phase, and global tick count.
 * <p>
 * This is the equivalent of the original SRP's SPSaveData, which tracked
 * per-dimension evolution progress. It is separate from {@link ModWorldData}
 * which handles kill statistics and colony data.
 * <p>
 * Evolution points accumulate through parasite activity (kills, colony output,
 * COTH spreading). When points exceed a threshold, the phase advances.
 * The phase controls spawn rates, entity stats, fog density, and other
 * ecosystem parameters.
 */
public class ModSaveData extends SavedData {

    private static final String DATA_NAME = "subspaceparasite_savedata";

    /** Per-dimension evolution points. Key is dimension ResourceLocation. */
    private final Map<ResourceLocation, Integer> evolutionPoints;

    /** Per-dimension current evolution phase. Key is dimension ResourceLocation. */
    private final Map<ResourceLocation, EvoPhase> currentPhase;

    /** Global tick counter (total across all dimensions). */
    private long totalTicks;

    public ModSaveData() {
        this.evolutionPoints = new ConcurrentHashMap<>();
        this.currentPhase = new ConcurrentHashMap<>();
        this.totalTicks = 0;
    }

    // ── Static access ──

    /**
     * Gets or creates the ModSaveData for the given server level.
     *
     * @param level the server level
     * @return the save data instance for this level's dimension
     */
    public static ModSaveData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                ModSaveData::load,
                ModSaveData::new,
                DATA_NAME
        );
    }

    // ── Evolution Points ──

    /**
     * Adds evolution points to the specified dimension.
     *
     * @param dim   the dimension resource location
     * @param points the number of points to add
     */
    public void addEvolutionPoints(ResourceLocation dim, int points) {
        if (points == 0) return;
        evolutionPoints.merge(dim, points, Integer::sum);
        setDirty();
    }

    /**
     * Gets the evolution points for a dimension.
     *
     * @param dim the dimension resource location
     * @return evolution points, or 0 if none recorded
     */
    public int getEvolutionPoints(ResourceLocation dim) {
        return evolutionPoints.getOrDefault(dim, 0);
    }

    /**
     * Sets the evolution points for a dimension.
     *
     * @param dim   the dimension resource location
     * @param points the new point value
     */
    public void setEvolutionPoints(ResourceLocation dim, int points) {
        if (evolutionPoints.getOrDefault(dim, 0) != points) {
            evolutionPoints.put(dim, points);
            setDirty();
        }
    }

    /**
     * Returns an unmodifiable view of all evolution points.
     *
     * @return map of dimension → evolution points
     */
    public Map<ResourceLocation, Integer> getAllEvolutionPoints() {
        return Collections.unmodifiableMap(evolutionPoints);
    }

    // ── Phase ──

    /**
     * Gets the current evolution phase for the given server level's dimension.
     *
     * @param level the server level
     * @return the current EvoPhase for this dimension
     */
    public EvoPhase getCurrentPhase(ServerLevel level) {
        ResourceLocation dim = level.dimension().location();
        return currentPhase.getOrDefault(dim, EvoPhase.ZERO);
    }

    /**
     * Sets the current evolution phase for the given server level's dimension.
     *
     * @param level the server level
     * @param phase the new phase
     */
    public void setCurrentPhase(ServerLevel level, EvoPhase phase) {
        ResourceLocation dim = level.dimension().location();
        EvoPhase current = currentPhase.get(dim);
        if (current != phase) {
            currentPhase.put(dim, phase);
            setDirty();
            if (SubspaceParasite.LOGGER.isDebugEnabled()) {
                SubspaceParasite.LOGGER.debug("Phase changed for dimension {}: {} -> {}",
                        dim, current, phase);
            }
        }
    }

    /**
     * Returns an unmodifiable view of all phase data.
     *
     * @return map of dimension → current phase
     */
    public Map<ResourceLocation, EvoPhase> getAllPhases() {
        return Collections.unmodifiableMap(currentPhase);
    }

    // ── Tick ──

    /**
     * Called every server tick to update global state.
     *
     * @param level the server level (used for dimension context)
     */
    public void tick(ServerLevel level) {
        totalTicks++;
        // Only mark dirty periodically to avoid excessive saves
        if (totalTicks % 6000 == 0) { // every 5 minutes
            setDirty();
        }
    }

    /**
     * Returns the total number of ticks recorded.
     *
     * @return total ticks
     */
    public long getTotalTicks() {
        return totalTicks;
    }

    // ── NBT Serialization ──

    public static ModSaveData load(CompoundTag tag) {
        ModSaveData data = new ModSaveData();
        data.totalTicks = tag.getLong("TotalTicks");

        // Load evolution points
        ListTag pointsList = tag.getList("EvolutionPoints", Tag.TAG_COMPOUND);
        for (int i = 0; i < pointsList.size(); i++) {
            CompoundTag entry = pointsList.getCompound(i);
            ResourceLocation dim = new ResourceLocation(entry.getString("Dim"));
            int points = entry.getInt("Points");
            data.evolutionPoints.put(dim, points);
        }

        // Load phases
        ListTag phaseList = tag.getList("CurrentPhase", Tag.TAG_COMPOUND);
        for (int i = 0; i < phaseList.size(); i++) {
            CompoundTag entry = phaseList.getCompound(i);
            ResourceLocation dim = new ResourceLocation(entry.getString("Dim"));
            int phaseNum = entry.getInt("Phase");
            data.currentPhase.put(dim, EvoPhase.getByPhaseNumber(phaseNum));
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putLong("TotalTicks", totalTicks);

        // Save evolution points
        ListTag pointsList = new ListTag();
        for (Map.Entry<ResourceLocation, Integer> entry : evolutionPoints.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putString("Dim", entry.getKey().toString());
            entryTag.putInt("Points", entry.getValue());
            pointsList.add(entryTag);
        }
        tag.put("EvolutionPoints", pointsList);

        // Save phases
        ListTag phaseList = new ListTag();
        for (Map.Entry<ResourceLocation, EvoPhase> entry : currentPhase.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putString("Dim", entry.getKey().toString());
            entryTag.putInt("Phase", entry.getValue().getPhaseNumber());
            phaseList.add(entryTag);
        }
        tag.put("CurrentPhase", phaseList);

        return tag;
    }

    // ── Reset ──

    /**
     * Resets all data. Used for debug/commands.
     */
    public void reset() {
        evolutionPoints.clear();
        currentPhase.clear();
        totalTicks = 0;
        setDirty();
    }
}
