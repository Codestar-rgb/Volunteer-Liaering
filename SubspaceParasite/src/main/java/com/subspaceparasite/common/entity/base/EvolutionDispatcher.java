package com.subspaceparasite.common.entity.base;

import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.api.parasite.GeneType;
import com.subspaceparasite.config.ModConfigSystems;
import com.subspaceparasite.network.ModNetwork;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * EvolutionDispatcher - Central evolution management system
 * 
 * Handles:
 * - Kill count tracking and evolution triggers
 * - Phase transition condition checking
 * - Gene mutation scheduling
 * - Evolution event broadcasting
 * - Global evolution statistics
 * 
 * This is a singleton manager that coordinates evolution across all parasite entities.
 * 
 * @author SubspaceParasite Team
 * @version 1.20.1
 */
public class EvolutionDispatcher {
    
    private static EvolutionDispatcher instance;
    
    /**
     * Maps entity UUID to kill count for evolution tracking
     */
    private final Map<UUID, Integer> killCountMap;
    
    /**
     * Maps entity UUID to evolution stage progress (0-100%)
     */
    private final Map<UUID, Float> evolutionProgressMap;
    
    /**
     * Entities pending evolution check this tick
     */
    private final Set<UUID> pendingEvolutionChecks;
    
    /**
     * Global evolution statistics
     */
    private int totalEvolutionsThisSession;
    private int totalMutationsThisSession;
    private int currentActiveParasites;
    
    /**
     * Evolution tick counter for scheduled events
     */
    private long globalTickCounter;
    
    private static final int EVOLUTION_CHECK_INTERVAL = 10; // Check every 10 ticks
    private static final int STAT_SYNC_INTERVAL = 600; // Sync stats every 30 seconds
    
    private EvolutionDispatcher() {
        this.killCountMap = new ConcurrentHashMap<>();
        this.evolutionProgressMap = new ConcurrentHashMap<>();
        this.pendingEvolutionChecks = ConcurrentHashMap.newKeySet();
        this.totalEvolutionsThisSession = 0;
        this.totalMutationsThisSession = 0;
        this.currentActiveParasites = 0;
        this.globalTickCounter = 0;
    }
    
    /**
     * Get the singleton instance
     */
    public static EvolutionDispatcher getInstance() {
        if (instance == null) {
            instance = new EvolutionDispatcher();
        }
        return instance;
    }
    
    /**
     * Reset instance (for testing/server restart)
     */
    public static void resetInstance() {
        instance = null;
    }
    
    /**
     * Tick the dispatcher - called from server tick event
     */
    public void tick(ServerLevel level) {
        globalTickCounter++;
        
        // Process pending evolution checks
        if (!pendingEvolutionChecks.isEmpty()) {
            processPendingEvolutionChecks(level);
        }
        
        // Periodic global stats sync
        if (globalTickCounter % STAT_SYNC_INTERVAL == 0) {
            syncGlobalStats();
        }
    }
    
    /**
     * Register a kill for an entity
     */
    public void registerKill(LivingEntity killer, LivingEntity victim) {
        if (!(killer instanceof EntityParasiteBase parasite)) return;
        
        UUID entityId = parasite.getUUID();
        killCountMap.merge(entityId, 1, Integer::sum);
        
        // Notify evolution component
        if (parasite.getEvolutionComponent() != null) {
            parasite.getEvolutionComponent().onKillCountChanged(killCountMap.get(entityId));
        }
        
        // Check for immediate evolution trigger
        scheduleEvolutionCheck(entityId);
        
        // Update colony if applicable
        if (parasite.getColonyComponent() != null) {
            parasite.getColonyComponent().onMemberKill(victim);
        }
    }
    
    /**
     * Schedule an evolution check for next tick batch
     */
    public void scheduleEvolutionCheck(UUID entityId) {
        pendingEvolutionChecks.add(entityId);
    }
    
    /**
     * Process all pending evolution checks
     */
    private void processPendingEvolutionChecks(Level level) {
        List<UUID> toProcess = new ArrayList<>(pendingEvolutionChecks);
        pendingEvolutionChecks.clear();
        
        for (UUID entityId : toProcess) {
            if (level.getEntity(entityId) instanceof EntityParasiteBase parasite) {
                checkEvolutionConditions(parasite);
            } else {
                // Entity no longer exists, clean up data
                killCountMap.remove(entityId);
                evolutionProgressMap.remove(entityId);
            }
        }
    }
    
    /**
     * Check if an entity meets evolution conditions
     */
    private void checkEvolutionConditions(EntityParasiteBase parasite) {
        if (!ModConfigSystems.isEvolutionEnabled()) return;
        if (parasite.level().isClientSide()) return;
        
        EvolutionComponent evoComp = parasite.getEvolutionComponent();
        if (evoComp == null || !evoComp.canEvolve()) return;
        
        EvoPhase currentPhase = parasite.getPhaseCreated();
        EvoPhase worldPhase = EntityParasiteBase.getCurrentPhase(parasite.level());
        
        // Check if world phase allows evolution
        if (!worldPhase.isAtLeast(EvoPhase.ONE)) return;
        
        // Check evolution threshold
        if (evoComp.getEvolutionPointsInternal() >= evoComp.getEvolutionThreshold()) {
            // Attempt evolution
            attemptEntityEvolution(parasite);
        }
        
        // Update progress map for UI/sync
        float progress = (float) (evoComp.getEvolutionPointsInternal() / evoComp.getEvolutionThreshold());
        evolutionProgressMap.put(parasite.getUUID(), Math.min(1.0f, progress));
    }
    
    /**
     * Attempt to evolve an entity
     */
    public void attemptEntityEvolution(EntityParasiteBase parasite) {
        if (!ModConfigSystems.isEvolutionEnabled()) return;
        
        EvolutionComponent evoComp = parasite.getEvolutionComponent();
        if (evoComp == null) return;
        
        // Perform evolution via component
        evoComp.attemptEvolution();
        
        // Track statistics
        totalEvolutionsThisSession++;
        
        // Broadcast evolution event
        broadcastEvolutionEvent(parasite);
        
        // Check for phase advancement
        checkPhaseAdvancement(parasite);
    }
    
    /**
     * Check if entity should advance to next phase
     */
    private void checkPhaseAdvancement(EntityParasiteBase parasite) {
        EvoPhase currentPhase = parasite.getPhaseCreated();
        int evoLevel = parasite.getEvolutionComponent().getEvolutionLevel();
        
        // Phase advancement thresholds (configurable)
        Map<EvoPhase, Integer> phaseThresholds = getPhaseAdvancementThresholds();
        
        for (Map.Entry<EvoPhase, Integer> entry : phaseThresholds.entrySet()) {
            if (currentPhase.isBefore(entry.getKey()) && evoLevel >= entry.getValue()) {
                advanceEntityPhase(parasite, entry.getKey());
                break;
            }
        }
    }
    
    /**
     * Advance entity to new phase
     */
    private void advanceEntityPhase(EntityParasiteBase parasite, EvoPhase newPhase) {
        EvoPhase oldPhase = parasite.getPhaseCreated();
        
        // Apply phase change
        parasite.setPhaseCreated(newPhase);
        
        // Grant phase-specific genes
        grantPhaseGenes(parasite, newPhase);
        
        // Apply major bonuses
        parasite.applyBonuses();
        
        // Broadcast phase change
        broadcastPhaseChange(parasite, oldPhase, newPhase);
    }
    
    /**
     * Grant genes based on new phase
     */
    private void grantPhaseGenes(EntityParasiteBase parasite, EvoPhase phase) {
        List<GeneType> phaseGenes = getPhaseSpecificGenes(phase);
        
        for (GeneType gene : phaseGenes) {
            if (!parasite.hasGene(gene)) {
                parasite.activateGene(gene);
            }
        }
    }
    
    /**
     * Trigger gene mutation for an entity
     */
    public void triggerMutation(EntityParasiteBase parasite) {
        if (!ModConfigSystems.isGeneMutationEnabled()) return;
        
        EvolutionComponent evoComp = parasite.getEvolutionComponent();
        if (evoComp == null) return;
        
        evoComp.checkGeneMutation();
        
        if (evoComp.hasMutated()) {
            totalMutationsThisSession++;
            broadcastMutationEvent(parasite);
        }
    }
    
    /**
     * Broadcast evolution event to clients
     */
    private void broadcastEvolutionEvent(EntityParasiteBase parasite) {
        if (parasite.level().isClientSide()) return;
        
        int entityId = parasite.getId();
        int evoLevel = parasite.getEvolutionComponent().getEvolutionLevel();
        int evoPoints = (int) parasite.getEvolutionComponent().getEvolutionPointsInternal();
        
        // Send sync packet
        ModNetwork.CHANNEL.sendToAllPlayers(
            new ModNetwork.EvolutionSyncPacket(entityId, evoLevel, evoPoints),
            (ServerLevel) parasite.level()
        );
    }
    
    /**
     * Broadcast phase change to clients
     */
    private void broadcastPhaseChange(EntityParasiteBase parasite, EvoPhase oldPhase, EvoPhase newPhase) {
        if (parasite.level().isClientSide()) return;
        
        // Send particle effects
        spawnEvolutionParticles(parasite, newPhase);
        
        // Play sound
        playEvolutionSound(parasite, newPhase);
    }
    
    /**
     * Broadcast mutation event to clients
     */
    private void broadcastMutationEvent(EntityParasiteBase parasite) {
        if (parasite.level().isClientSide()) return;
        
        // Send particle effects
        spawnMutationParticles(parasite);
    }
    
    /**
     * Spawn evolution particles
     */
    private void spawnEvolutionParticles(EntityParasiteBase parasite, EvoPhase phase) {
        // TODO: Implement particle spawning based on phase
        // Different phases have different particle types
    }
    
    /**
     * Spawn mutation particles
     */
    private void spawnMutationParticles(EntityParasiteBase parasite) {
        // TODO: Implement mutation particle effects
    }
    
    /**
     * Play evolution sound
     */
    private void playEvolutionSound(EntityParasiteBase parasite, EvoPhase phase) {
        // TODO: Implement evolution sounds per phase
    }
    
    /**
     * Sync global statistics to clients
     */
    private void syncGlobalStats() {
        // TODO: Send global stats packet for UI display
        // Includes total evolutions, active parasites, etc.
    }
    
    /**
     * Get phase advancement thresholds
     */
    private Map<EvoPhase, Integer> getPhaseAdvancementThresholds() {
        Map<EvoPhase, Integer> thresholds = new EnumMap<>(EvoPhase.class);
        thresholds.put(EvoPhase.ZERO, 0);
        thresholds.put(EvoPhase.PRIMITIVE, 2);
        thresholds.put(EvoPhase.CARRIER, 5);
        thresholds.put(EvoPhase.ADAPTED, 10);
        thresholds.put(EvoPhase.INFECTOR, 15);
        thresholds.put(EvoPhase.BEAST, 25);
        thresholds.put(EvoPhase.ANCIENT, 40);
        thresholds.put(EvoPhase.SENTIENT, 60);
        return thresholds;
    }
    
    /**
     * Get phase-specific genes
     */
    private List<GeneType> getPhaseSpecificGenes(EvoPhase phase) {
        List<GeneType> genes = new ArrayList<>();
        
        switch (phase) {
            case PRIMITIVE:
                genes.add(GeneType.BASIC_RESISTANCE);
                break;
            case CARRIER:
                genes.add(GeneType.INFECTION_BOOST);
                break;
            case ADAPTED:
                genes.add(GeneType.ADAPTATION_SPEED);
                break;
            case INFECTOR:
                genes.add(GeneType.SPREAD_RANGE);
                break;
            case BEAST:
                genes.add(GeneType.COMBAT_MASTERY);
                break;
            case ANCIENT:
                genes.add(GeneType.ANCIENT_POWER);
                break;
            case SENTIENT:
                genes.add(GeneType.HIVE_MIND);
                break;
            default:
                break;
        }
        
        return genes;
    }
    
    /**
     * Get kill count for an entity
     */
    public int getKillCount(UUID entityId) {
        return killCountMap.getOrDefault(entityId, 0);
    }
    
    /**
     * Get evolution progress for an entity (0.0 - 1.0)
     */
    public float getEvolutionProgress(UUID entityId) {
        return evolutionProgressMap.getOrDefault(entityId, 0.0f);
    }
    
    /**
     * Get total evolutions this session
     */
    public int getTotalEvolutions() {
        return totalEvolutionsThisSession;
    }
    
    /**
     * Get total mutations this session
     */
    public int getTotalMutations() {
        return totalMutationsThisSession;
    }
    
    /**
     * Clear all data (server shutdown/restart)
     */
    public void clear() {
        killCountMap.clear();
        evolutionProgressMap.clear();
        pendingEvolutionChecks.clear();
        totalEvolutionsThisSession = 0;
        totalMutationsThisSession = 0;
        currentActiveParasites = 0;
        globalTickCounter = 0;
    }
}
