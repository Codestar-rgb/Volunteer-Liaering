package com.subspaceparasite.common.world;

import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.common.entity.projectile.EntityMeteor;
import com.subspaceparasite.config.ModConfigSystems;
import com.subspaceparasite.core.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DifficultySystem - Dynamic Difficulty Management for Parasite Mod
 * <p>
 * Implements an adaptive difficulty system that scales parasite threats
 * based on player progress, world state, and gameplay metrics.
 * <p>
 * Integrates with {@link SRPDifficultySetting} for per-world difficulty
 * selection and with {@link CelestialManager} for celestial event modifiers.
 * <p>
 * Features:
 * - Dynamic difficulty scaling based on player stats
 * - Regional difficulty modifiers
 * - Time-based escalation (harder over time)
 * - Event-triggered difficulty spikes
 * - Configurable difficulty curves
 * - SRP difficulty setting integration (spawn/evolution/infection/meteor multipliers)
 * - Meteor spawning system (frequency scales with difficulty and phase)
 */
@Mod.EventBusSubscriber(modid = SubspaceParasite.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DifficultySystem {

    // ========== Singleton per-level ==========

    private static final Map<String, DifficultySystem> instances = new ConcurrentHashMap<>();

    public static DifficultySystem get(ServerLevel level) {
        String dimensionKey = level.dimension().location().toString();
        return instances.computeIfAbsent(dimensionKey, k -> new DifficultySystem());
    }

    // ========== Difficulty State ==========

    /** Base difficulty level (0.0 - 10.0 scale) */
    private float baseDifficulty = 0.0f;

    /** Current effective difficulty after all modifiers */
    private float currentDifficulty = 0.0f;

    /** World tick counter for time-based scaling */
    private long worldTickCounter = 0;

    /** Player threat level based on kills/deaths ratio */
    private float playerThreatLevel = 0.0f;

    /** Regional difficulty modifier */
    private float regionalModifier = 1.0f;

    /** Active difficulty events */
    private final List<DifficultyEvent> activeEvents = new ArrayList<>();

    /** Ticks since last meteor attempt. */
    private long ticksSinceLastMeteorCheck = 0;

    // ========== Configuration Constants ==========

    /** Maximum base difficulty cap */
    private static final float MAX_DIFFICULTY = 10.0f;

    /** Minimum difficulty floor */
    private static final float MIN_DIFFICULTY = 0.5f;

    /** Ticks per difficulty increment (base rate) */
    private static final int DIFFICULTY_INCREMENT_INTERVAL = 12000; // ~10 minutes

    /** Difficulty increment amount per interval */
    private static final float DIFFICULTY_INCREMENT = 0.1f;

    /** Player threat influence factor */
    private static final float THREAT_INFLUENCE = 0.3f;

    /** Regional influence factor */
    private static final float REGIONAL_INFLUENCE = 0.2f;

    // ========== Constructors ==========

    private DifficultySystem() {
        this.baseDifficulty = MIN_DIFFICULTY;
        this.currentDifficulty = MIN_DIFFICULTY;
        this.playerThreatLevel = 0.0f;
        this.regionalModifier = 1.0f;
    }

    // ========== Tick System ==========

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) event.level;
            DifficultySystem system = get(serverLevel);
            system.tick(serverLevel);
        }
    }

    /**
     * Main tick method - updates difficulty calculations and attempts meteor spawns.
     */
    public void tick(ServerLevel level) {
        worldTickCounter++;

        // Update base difficulty over time
        if (worldTickCounter % DIFFICULTY_INCREMENT_INTERVAL == 0) {
            increaseBaseDifficulty();
        }

        // Process active events
        processActiveEvents(level);

        // Calculate current effective difficulty
        calculateCurrentDifficulty();

        // Attempt meteor spawning
        ticksSinceLastMeteorCheck++;
        if (ticksSinceLastMeteorCheck >= 100) { // Check every 5 seconds
            ticksSinceLastMeteorCheck = 0;
            attemptMeteorSpawn(level);
        }
    }

    // ========== Difficulty Calculation ==========

    private void increaseBaseDifficulty() {
        if (baseDifficulty < MAX_DIFFICULTY) {
            baseDifficulty = Math.min(MAX_DIFFICULTY, baseDifficulty + DIFFICULTY_INCREMENT);
        }
    }

    private void calculateCurrentDifficulty() {
        float calculated = baseDifficulty;

        // Apply player threat modifier
        calculated += playerThreatLevel * THREAT_INFLUENCE;

        // Apply regional modifier
        calculated *= regionalModifier;

        // Apply event modifiers
        for (DifficultyEvent event : activeEvents) {
            calculated *= event.getDifficultyMultiplier();
        }

        // Clamp to valid range
        currentDifficulty = Math.max(MIN_DIFFICULTY, Math.min(MAX_DIFFICULTY, calculated));
    }

    // ========== Event System ==========

    /**
     * Adds a temporary difficulty event
     */
    public void addEvent(DifficultyEventType type, int durationTicks) {
        DifficultyEvent event = new DifficultyEvent(type, durationTicks);
        activeEvents.add(event);
    }

    /**
     * Removes a difficulty event
     */
    public void removeEvent(DifficultyEventType type) {
        activeEvents.removeIf(e -> e.getType() == type);
        calculateCurrentDifficulty();
    }

    private void processActiveEvents(ServerLevel level) {
        Iterator<DifficultyEvent> iterator = activeEvents.iterator();
        while (iterator.hasNext()) {
            DifficultyEvent event = iterator.next();
            event.tick();

            if (event.isExpired()) {
                iterator.remove();
            }
        }
    }

    // ========== Meteor Spawning ==========

    /**
     * Attempts to spawn a meteor based on current difficulty, phase,
     * celestial event state, and SRP difficulty setting.
     * <p>
     * Meteor frequency is determined by:
     * <ol>
     *   <li>SRP difficulty setting's base meteor chance</li>
     *   <li>EvoPhase (higher phase = more frequent)</li>
     *   <li>CelestialManager (active celestial night = 3x chance)</li>
     *   <li>SRP difficulty meteor frequency multiplier</li>
     * </ol>
     */
    private void attemptMeteorSpawn(ServerLevel level) {
        // Check if meteors are disabled in config
        if (!ModConfigSystems.areMeteorsEnabled()) return;

        // Get the current world state
        ModWorldData worldData = ModWorldData.get(level);
        SRPDifficultySetting difficulty = worldData.getSRPDifficulty();
        EvoPhase currentPhase = worldData.getCurrentPhase();

        // Check if the phase is high enough for meteors
        if (currentPhase.getPhaseNumber() < difficulty.getMinMeteorPhase()) return;

        // Calculate meteor spawn chance
        float baseChance = difficulty.getBaseMeteorChance();
        float phaseFactor = 1.0f + (currentPhase.getPhaseNumber() * 0.15f);
        float difficultyMultiplier = difficulty.getMeteorFrequencyMultiplier();

        // Celestial night increases meteor chance by 3x
        CelestialManager celestialMgr = CelestialManager.get(level);
        float celestialMultiplier = celestialMgr.isCelestialNightActive() ? 3.0f : 1.0f;

        float totalChance = baseChance * phaseFactor * difficultyMultiplier * celestialMultiplier;

        // Cap at a reasonable maximum to prevent meteor spam
        totalChance = Math.min(totalChance, 0.05f); // Max 5% per check (every 5 seconds)

        if (level.getRandom().nextFloat() < totalChance) {
            spawnMeteor(level, difficulty, currentPhase);
        }
    }

    /**
     * Spawns a meteor in the given level.
     */
    private void spawnMeteor(ServerLevel level, SRPDifficultySetting difficulty, EvoPhase phase) {
        // Find random position near players
        var players = level.players();
        if (players.isEmpty()) return;

        Player targetPlayer = players.get(level.random.nextInt(players.size()));

        // Spawn meteor above a random position near the target player
        double spawnX = targetPlayer.getX() + (level.random.nextDouble() - 0.5) * 80;
        double spawnY = 200 + level.random.nextDouble() * 56; // Y 200-256
        double spawnZ = targetPlayer.getZ() + (level.random.nextDouble() - 0.5) * 80;

        // Determine meteor size based on phase and difficulty
        int meteorSize = EntityMeteor.determineMeteorSize(phase, difficulty);

        // Check if this meteor should create permanent infestation
        boolean permanent = difficulty.largeMeteorsCreatePermanentInfestation() && meteorSize >= 3;

        EntityMeteor meteor = new EntityMeteor(level, spawnX, spawnY, spawnZ, meteorSize, permanent);
        level.addFreshEntity(meteor);

        SubspaceParasite.LOGGER.debug("Meteor spawned at [{}, {}, {}] size={} permanent={} phase={} difficulty={}",
                (int) spawnX, (int) spawnY, (int) spawnZ, meteorSize, permanent, phase, difficulty);
    }

    // ========== Player Threat System ==========

    /**
     * Updates player threat level based on kills/deaths
     */
    public void updatePlayerThreat(int kills, int deaths) {
        float kdRatio = deaths > 0 ? (float) kills / deaths : kills;
        playerThreatLevel = Math.min(5.0f, kdRatio);
        calculateCurrentDifficulty();
    }

    /**
     * Adjusts threat level incrementally
     */
    public void adjustPlayerThreat(float delta) {
        playerThreatLevel = Math.max(0.0f, Math.min(5.0f, playerThreatLevel + delta));
        calculateCurrentDifficulty();
    }

    // ========== Regional Modifiers ==========

    /**
     * Sets regional difficulty modifier
     */
    public void setRegionalModifier(float modifier) {
        this.regionalModifier = Math.max(0.5f, Math.min(2.0f, modifier));
        calculateCurrentDifficulty();
    }

    /**
     * Updates regional modifier based on biome/position
     */
    public void updateRegionalModifier(ServerLevel level, BlockPos pos) {
        float biomeModifier = 1.0f;
        setRegionalModifier(biomeModifier);
    }

    // ========== Getters ==========

    public float getBaseDifficulty() {
        return baseDifficulty;
    }

    public float getCurrentDifficulty() {
        return currentDifficulty;
    }

    public long getWorldTickCounter() {
        return worldTickCounter;
    }

    public float getPlayerThreatLevel() {
        return playerThreatLevel;
    }

    public float getRegionalModifier() {
        return regionalModifier;
    }

    public List<DifficultyEvent> getActiveEvents() {
        return Collections.unmodifiableList(activeEvents);
    }

    // ========== Difficulty Scaling Helpers ==========

    /**
     * Gets spawn rate multiplier based on difficulty, incorporating SRP difficulty setting.
     */
    public float getSpawnRateMultiplier(ServerLevel level) {
        SRPDifficultySetting setting = ModWorldData.get(level).getSRPDifficulty();
        float dynamicPart = 1.0f + (currentDifficulty / MAX_DIFFICULTY) * 2.0f;
        return dynamicPart * setting.getSpawnRateMultiplier();
    }

    /**
     * Gets damage multiplier for parasites based on difficulty
     */
    public float getParasiteDamageMultiplier() {
        return 1.0f + (currentDifficulty / MAX_DIFFICULTY) * 1.5f;
    }

    /**
     * Gets health multiplier for parasites based on difficulty
     */
    public float getParasiteHealthMultiplier() {
        return 1.0f + (currentDifficulty / MAX_DIFFICULTY) * 1.0f;
    }

    /**
     * Gets evolution rate multiplier based on difficulty, incorporating SRP difficulty setting.
     */
    public float getEvolutionRateMultiplier(ServerLevel level) {
        SRPDifficultySetting setting = ModWorldData.get(level).getSRPDifficulty();
        float dynamicPart = 1.0f + (currentDifficulty / MAX_DIFFICULTY) * 0.5f;
        return dynamicPart * setting.getEvolutionRateMultiplier();
    }

    /**
     * Gets infection rate multiplier based on difficulty, incorporating SRP difficulty setting.
     */
    public float getInfectionRateMultiplier(ServerLevel level) {
        SRPDifficultySetting setting = ModWorldData.get(level).getSRPDifficulty();
        float dynamicPart = 1.0f + (currentDifficulty / MAX_DIFFICULTY) * 0.3f;
        return dynamicPart * setting.getInfectionRateMultiplier();
    }

    /**
     * Gets merge rate multiplier based on difficulty, incorporating SRP difficulty setting.
     */
    public float getMergeRateMultiplier(ServerLevel level) {
        SRPDifficultySetting setting = ModWorldData.get(level).getSRPDifficulty();
        return setting.getMergeRateMultiplier();
    }

    /**
     * Gets colony growth rate multiplier based on difficulty, incorporating SRP difficulty setting.
     */
    public float getColonyGrowthRateMultiplier(ServerLevel level) {
        SRPDifficultySetting setting = ModWorldData.get(level).getSRPDifficulty();
        return setting.getColonyGrowthRateMultiplier();
    }

    // ========== Difficulty Event Class ==========

    public static class DifficultyEvent {
        private final DifficultyEventType type;
        private int remainingTicks;
        private final int duration;

        public DifficultyEvent(DifficultyEventType type, int durationTicks) {
            this.type = type;
            this.remainingTicks = durationTicks;
            this.duration = durationTicks;
        }

        public void tick() {
            remainingTicks--;
        }

        public boolean isExpired() {
            return remainingTicks <= 0;
        }

        public float getDifficultyMultiplier() {
            return type.getMultiplier();
        }

        public DifficultyEventType getType() {
            return type;
        }

        public int getRemainingTicks() {
            return remainingTicks;
        }

        public float getProgress() {
            return 1.0f - ((float) remainingTicks / duration);
        }
    }

    // ========== Difficulty Event Types ==========

    public enum DifficultyEventType {
        CELESTIAL_NIGHT(1.5f),      // 50% increase
        BLOOD_MOON(1.8f),           // 80% increase
        PARASITE_STORM(2.0f),       // 100% increase
        APOCALYPSE(3.0f),           // 200% increase
        COLONY_SURGE(1.3f);         // 30% increase

        private final float multiplier;

        DifficultyEventType(float multiplier) {
            this.multiplier = multiplier;
        }

        public float getMultiplier() {
            return multiplier;
        }
    }
}
