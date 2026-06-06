package com.subspaceparasite.client;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client-side data storage for synced server data.
 * <p>
 * Stores biome infection levels, colony heart data, and player infection
 * data received from server-to-client packets. All fields are static and
 * accessed from the client main thread via enqueueWork().
 * <p>
 * Used by:
 * <ul>
 *   <li>{@link com.subspaceparasite.client.overlay.InfectionOverlayHandler} - player infection HUD</li>
 *   <li>{@link com.subspaceparasite.client.fog.FogHandler} - biome infection fog</li>
 * </ul>
 */
public class ClientData {

    // ========== Biome Infection Data ==========

    /**
     * Maps chunk position long keys to their infection level (0.0 - 1.0).
     * The long key is computed via {@link net.minecraft.world.level.ChunkPos#toLong(long, long)}
     * or equivalently {@code ((long)chunkX << 32) | (chunkZ & 0xFFFFFFFFL)}.
     * <p>
     * Used by the fog renderer and infection overlay to determine visual effects
     * based on the local biome infection state.
     */
    public static final Map<Long, Float> CLIENT_INFECTION_DATA = new ConcurrentHashMap<>();

    // ========== Colony Heart Data ==========

    /**
     * Maps colony heart IDs to their colony data for GUI display.
     * Key is the blockEntityId sent from the server.
     */
    public static final Map<Integer, ColonyData> CLIENT_COLONY_DATA = new ConcurrentHashMap<>();

    // ========== Player Infection Data ==========

    /**
     * The current player's infection level (0.0 - 1.0).
     * Synced from the server via PlayerInfectionSyncPacket.
     * Used by InfectionOverlayHandler for the HUD overlay.
     */
    public static float CLIENT_PLAYER_INFECTION = 0.0f;

    /**
     * The current player's virulence level (0.0 - 1.0).
     * Synced from the server via PlayerInfectionSyncPacket.
     * Used by InfectionOverlayHandler for the HUD overlay.
     */
    public static float CLIENT_PLAYER_VIRULENCE = 0.0f;

    // ========== Utility Methods ==========

    /**
     * Gets the infection level at the given chunk coordinates.
     *
     * @param chunkX the chunk X coordinate
     * @param chunkZ the chunk Z coordinate
     * @return the infection level (0.0 - 1.0), or 0.0 if no data
     */
    public static float getChunkInfection(int chunkX, int chunkZ) {
        return CLIENT_INFECTION_DATA.getOrDefault(chunkKey(chunkX, chunkZ), 0.0f);
    }

    /**
     * Sets the infection level for a chunk.
     *
     * @param chunkX the chunk X coordinate
     * @param chunkZ the chunk Z coordinate
     * @param level  the infection level (0.0 - 1.0)
     */
    public static void setChunkInfection(int chunkX, int chunkZ, float level) {
        if (level <= 0.001f) {
            CLIENT_INFECTION_DATA.remove(chunkKey(chunkX, chunkZ));
        } else {
            CLIENT_INFECTION_DATA.put(chunkKey(chunkX, chunkZ), level);
        }
    }

    /**
     * Computes a long key from chunk coordinates, matching ChunkPos.toLong().
     *
     * @param chunkX the chunk X coordinate
     * @param chunkZ the chunk Z coordinate
     * @return the long key
     */
    public static long chunkKey(int chunkX, int chunkZ) {
        return ((long) chunkX << 32) | (chunkZ & 0xFFFFFFFFL);
    }

    /**
     * Resets all client data. Called when disconnecting from a server.
     */
    public static void reset() {
        CLIENT_INFECTION_DATA.clear();
        CLIENT_COLONY_DATA.clear();
        CLIENT_PLAYER_INFECTION = 0.0f;
        CLIENT_PLAYER_VIRULENCE = 0.0f;
    }

    /**
     * Returns an unmodifiable view of the colony data map.
     *
     * @return unmodifiable map of colony data
     */
    public static Map<Integer, ColonyData> getColonyDataView() {
        return Collections.unmodifiableMap(CLIENT_COLONY_DATA);
    }

    // ========== Colony Data Record ==========

    /**
     * Simple data class for colony heart status.
     * Synced from the server via ColonyHeartSyncPacket.
     */
    public static class ColonyData {

        /** The colony's current evolution level. */
        public final int colonyLevel;

        /** The number of parasites belonging to this colony. */
        public final int parasiteCount;

        public ColonyData(int colonyLevel, int parasiteCount) {
            this.colonyLevel = colonyLevel;
            this.parasiteCount = parasiteCount;
        }

        @Override
        public String toString() {
            return "ColonyData{level=" + colonyLevel + ", parasites=" + parasiteCount + "}";
        }
    }

    // Prevent instantiation
    private ClientData() {
    }
}
