package com.subspaceparasite.common.world;

import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.common.world.ModWorldData;
import com.subspaceparasite.network.ModNetwork;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.PacketDistributor;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages timed celestial events that affect the parasite ecosystem.
 * <p>
 * The original SRP has celestial events (e.g. "Celestial Night") that
 * periodically occur and dramatically alter game conditions:
 * <ul>
 *   <li>Parasite spawning rates increase significantly</li>
 *   <li>Evolution point gain is boosted</li>
 *   <li>Fog density increases (red tint)</li>
 *   <li>COTH spreading is amplified</li>
 * </ul>
 * <p>
 * Celestial events are synchronized to clients via network packets so
 * that client-side rendering (fog, overlays) can respond accordingly.
 */
public class CelestialManager {

    // ── Event State ──

    /** Whether a celestial night event is currently active. */
    private boolean celestialNightActive;

    /** Remaining duration of the current celestial night (in ticks). */
    private int celestialNightDuration;

    /** Cooldown before the next celestial night can begin (in ticks). */
    private int celestialNightCooldown;

    // ── Configuration Constants ──

    /** Default duration of a celestial night event (in ticks). ~6 minutes. */
    private static final int DEFAULT_CELESTIAL_DURATION = 7200;

    /** Default cooldown between celestial nights (in ticks). ~30 minutes. */
    private static final int DEFAULT_CELESTIAL_COOLDOWN = 36000;

    /** Minimum phase required for celestial events to occur (Phase 3: Evolved). */
    private static final EvoPhase MINIMUM_PHASE = EvoPhase.PHASE_3;

    /** Base chance per tick for a celestial night to start (when off cooldown).
     *  This is the base value before difficulty multipliers are applied. */
    private static final float CELESTIAL_START_CHANCE_BASE = 0.00005F;

    /** Spawn rate multiplier during celestial night. */
    private static final float CELESTIAL_SPAWN_MULTIPLIER = 2.5F;

    /** Evolution point gain multiplier during celestial night. */
    private static final float CELESTIAL_EVOLUTION_MULTIPLIER = 3.0F;

    /** Fog density bonus during celestial night. */
    private static final float CELESTIAL_FOG_BONUS = 0.1F;

    // ── Singleton per-level ──

    /** Per-level celestial manager instances, keyed by dimension. */
    private static final Map<ResourceLocation, CelestialManager> instances = new ConcurrentHashMap<>();

    private CelestialManager() {
        this.celestialNightActive = false;
        this.celestialNightDuration = 0;
        this.celestialNightCooldown = DEFAULT_CELESTIAL_COOLDOWN;
    }

    /**
     * Gets the CelestialManager instance for the given server level.
     *
     * @param level the server level
     * @return the celestial manager for this dimension
     */
    public static CelestialManager get(ServerLevel level) {
        ResourceLocation dim = level.dimension().location();
        return instances.computeIfAbsent(dim, k -> new CelestialManager());
    }

    /**
     * Gets or creates the CelestialManager instance for the given dimension key.
     * Used by {@link ModSaveData} during NBT load to ensure an instance
     * exists before restoring state into it.
     *
     * @param dim the dimension resource location
     * @return the celestial manager for this dimension
     */
    public static CelestialManager getOrCreate(ResourceLocation dim) {
        return instances.computeIfAbsent(dim, k -> new CelestialManager());
    }

    /**
     * Returns an unmodifiable view of all celestial manager instances.
     * Used by {@link ModSaveData} during NBT save to iterate and persist
     * state for every dimension.
     *
     * @return unmodifiable map of dimension → celestial manager
     */
    public static Map<ResourceLocation, CelestialManager> getAllInstances() {
        return Collections.unmodifiableMap(instances);
    }

    // ── Tick Logic ──

    /**
     * Called every server tick to update celestial event state.
     *
     * @param level the server level
     */
    public void tick(ServerLevel level) {
        if (level.isClientSide) return;

        // Check if the phase is high enough for celestial events
        ModSaveData saveData = ModSaveData.get(level);
        EvoPhase currentPhase = saveData.getCurrentPhase(level);

        if (celestialNightActive) {
            tickActiveCelestialNight(level);
        } else {
            tickInactiveCelestialNight(level, currentPhase);
        }
    }

    /**
     * Ticks an active celestial night event.
     */
    private void tickActiveCelestialNight(ServerLevel level) {
        celestialNightDuration--;

        if (celestialNightDuration <= 0) {
            endCelestialNight(level);
        }
    }

    /**
     * Ticks when no celestial night is active, checking for start conditions.
     */
    private void tickInactiveCelestialNight(ServerLevel level, EvoPhase currentPhase) {
        if (celestialNightCooldown > 0) {
            celestialNightCooldown--;
            return;
        }

        // Only start if phase is high enough
        if (!currentPhase.isAtLeast(MINIMUM_PHASE)) return;

        // Random chance to start a celestial night, modified by SRP difficulty
        SRPDifficultySetting difficulty = ModWorldData.get(level).getSRPDifficulty();
        float chance = CELESTIAL_START_CHANCE_BASE * difficulty.getCelestialFrequencyMultiplier();
        if (level.random.nextFloat() < chance) {
            startCelestialNight(level);
        }
    }

    // ── Event Control ──

    /**
     * Starts a celestial night event.
     *
     * @param level the server level
     */
    public void startCelestialNight(ServerLevel level) {
        if (celestialNightActive) return;

        celestialNightActive = true;

        // Duration scales with SRP difficulty (harder = longer events)
        SRPDifficultySetting difficulty = ModWorldData.get(level).getSRPDifficulty();
        float durationMultiplier = 1.0f + (difficulty.getId() * 0.1f); // EASY=1.1x, NORMAL=1.2x, HARD=1.3x, IMPOSSIBLE=1.4x
        celestialNightDuration = (int) (DEFAULT_CELESTIAL_DURATION * durationMultiplier);

        SubspaceParasite.LOGGER.info("Celestial Night has begun in dimension {}! Duration={}ticks difficulty={}",
                level.dimension().location(), celestialNightDuration, difficulty);

        // Add a difficulty event to amplify parasite threats during celestial night
        DifficultySystem.get(level).addEvent(
                DifficultySystem.DifficultyEventType.CELESTIAL_NIGHT, celestialNightDuration);

        // Sync to all players in this dimension
        syncToClients(level);
    }

    /**
     * Ends the current celestial night event.
     *
     * @param level the server level
     */
    public void endCelestialNight(ServerLevel level) {
        if (!celestialNightActive) return;

        celestialNightActive = false;

        // Cooldown scales inversely with difficulty (harder = shorter cooldown)
        SRPDifficultySetting difficulty = ModWorldData.get(level).getSRPDifficulty();
        float cooldownMultiplier = 1.0f / difficulty.getCelestialFrequencyMultiplier();
        celestialNightCooldown = (int) (DEFAULT_CELESTIAL_COOLDOWN * cooldownMultiplier);
        celestialNightDuration = 0;

        // Remove the celestial night difficulty event
        DifficultySystem.get(level).removeEvent(DifficultySystem.DifficultyEventType.CELESTIAL_NIGHT);

        SubspaceParasite.LOGGER.info("Celestial Night has ended in dimension {}. Cooldown={}ticks",
                level.dimension().location(), celestialNightCooldown);

        // Sync to all players in this dimension
        syncToClients(level);
    }

    // ── Queries ──

    /**
     * Returns whether a celestial night is currently active.
     *
     * @return true if celestial night is active
     */
    public boolean isCelestialNightActive() {
        return celestialNightActive;
    }

    /**
     * Returns the remaining duration of the current celestial night.
     *
     * @return remaining ticks, or 0 if not active
     */
    public int getCelestialNightDuration() {
        return celestialNightDuration;
    }

    /**
     * Returns the remaining cooldown before the next celestial night.
     *
     * @return remaining cooldown ticks
     */
    public int getCelestialNightCooldown() {
        return celestialNightCooldown;
    }

    /**
     * Returns the spawn rate multiplier, boosted during celestial night.
     *
     * @return spawn rate multiplier
     */
    public float getSpawnRateMultiplier() {
        return celestialNightActive ? CELESTIAL_SPAWN_MULTIPLIER : 1.0F;
    }

    /**
     * Returns the evolution point gain multiplier, boosted during celestial night.
     * Also factors in the SRP difficulty evolution rate multiplier.
     *
     * @return evolution multiplier
     */
    public float getEvolutionMultiplier() {
        return celestialNightActive ? CELESTIAL_EVOLUTION_MULTIPLIER : 1.0F;
    }

    /**
     * Returns the additional fog density during celestial night.
     *
     * @return fog density bonus
     */
    public float getFogDensityBonus() {
        return celestialNightActive ? CELESTIAL_FOG_BONUS : 0.0F;
    }

    // ── Client Sync ──

    /**
     * Sends celestial event state to all players in the given dimension.
     *
     * @param level the server level
     */
    private void syncToClients(ServerLevel level) {
        // Send a CelestialSyncPacket to all players in this dimension
        // The packet encodes: active state, remaining duration
        ModNetwork.CHANNEL.send(PacketDistributor.DIMENSION.with(level::dimension),
                new CelestialSyncPacket(celestialNightActive, celestialNightDuration));
    }

    /**
     * Packet for synchronizing celestial event state to clients.
     * Nested in CelestialManager to keep the celestial system self-contained.
     */
    public static class CelestialSyncPacket {
        public final boolean active;
        public final int duration;

        public CelestialSyncPacket(boolean active, int duration) {
            this.active = active;
            this.duration = duration;
        }

        public static void encode(CelestialSyncPacket msg, FriendlyByteBuf buf) {
            buf.writeBoolean(msg.active);
            buf.writeInt(msg.duration);
        }

        public static CelestialSyncPacket decode(FriendlyByteBuf buf) {
            return new CelestialSyncPacket(buf.readBoolean(), buf.readInt());
        }

        public static void handle(CelestialSyncPacket msg, net.minecraftforge.network.NetworkEvent.Context ctx) {
            ctx.enqueueWork(() -> {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                    // Update client-side celestial state cache
                    ClientCelestialCache.setActive(msg.active);
                    ClientCelestialCache.setDuration(msg.duration);
                });
            });
            ctx.setPacketHandled(true);
        }
    }

    /**
     * Client-side cache for celestial event state, updated by sync packets.
     * Used by rendering code (fog handler, overlay handler) to display
     * celestial effects without querying the server.
     */
    public static class ClientCelestialCache {
        private static boolean active = false;
        private static int duration = 0;

        public static boolean isActive() { return active; }
        public static void setActive(boolean a) { active = a; }

        public static int getDuration() { return duration; }
        public static void setDuration(int d) { duration = d; }

        public static void reset() {
            active = false;
            duration = 0;
        }
    }

    // ── Persistence ──

    /**
     * Saves celestial event state to the given ModSaveData compound tag.
     * Called from ModSaveData.save() to persist state across server restarts.
     */
    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean("CelestialNightActive", celestialNightActive);
        tag.putInt("CelestialNightDuration", celestialNightDuration);
        tag.putInt("CelestialNightCooldown", celestialNightCooldown);
        return tag;
    }

    /**
     * Loads celestial event state from the given compound tag.
     * Called from ModSaveData.load() to restore state after server restart.
     */
    public void load(CompoundTag tag) {
        celestialNightActive = tag.getBoolean("CelestialNightActive");
        celestialNightDuration = tag.getInt("CelestialNightDuration");
        celestialNightCooldown = tag.getInt("CelestialNightCooldown");
    }

    // ── Reset ──

    /**
     * Resets all celestial manager instances. Used for debug/commands.
     */
    public static void resetAll() {
        instances.clear();
    }
}
