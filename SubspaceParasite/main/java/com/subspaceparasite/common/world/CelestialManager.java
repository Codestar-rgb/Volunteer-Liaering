package com.subspaceparasite.common.world;

import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.network.ModNetwork;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.PacketDistributor;

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

    /** Minimum phase required for celestial events to occur. */
    private static final EvoPhase MINIMUM_PHASE = EvoPhase.THREE;

    /** Base chance per tick for a celestial night to start (when off cooldown). */
    private static final float CELESTIAL_START_CHANCE = 0.00005F;

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

        // Random chance to start a celestial night
        if (level.random.nextFloat() < CELESTIAL_START_CHANCE) {
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
        celestialNightDuration = DEFAULT_CELESTIAL_DURATION;

        SubspaceParasite.LOGGER.info("Celestial Night has begun in dimension {}!",
                level.dimension().location());

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
        celestialNightCooldown = DEFAULT_CELESTIAL_COOLDOWN;
        celestialNightDuration = 0;

        SubspaceParasite.LOGGER.info("Celestial Night has ended in dimension {}.",
                level.dimension().location());

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

    // TODO: CelestialManager state is currently held in a transient ConcurrentHashMap
    // and is NOT persisted across server restarts. To survive saves/loads, celestial
    // event state should be stored via SavedData (net.minecraft.world.level.saveddata.SavedData)
    // so that active celestial nights and cooldowns persist across world reloads.

    // ── Reset ──

    /**
     * Resets all celestial manager instances. Used for debug/commands.
     */
    public static void resetAll() {
        instances.clear();
    }
}
