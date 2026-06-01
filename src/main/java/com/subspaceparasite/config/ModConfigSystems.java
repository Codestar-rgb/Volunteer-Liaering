package com.subspaceparasite.config;

import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.api.parasite.EvolutionPath;
import com.subspaceparasite.api.parasite.ParasiteType;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Set;

/**
 * Helper class exposing config values as static methods for easy access.
 */
public class ModConfigSystems {

    private ModConfigSystems() {}

    // ========== Evolution ==========

    public static boolean isEvolutionEnabled() {
        return ModConfig.EVOLUTION.evolutionEnabled.get();
    }
    public static double getNaturalEvolutionRate() {
        return ModConfig.EVOLUTION.naturalEvolutionRate.get();
    }
    public static double getKillEvolutionRate() {
        return ModConfig.EVOLUTION.killEvolutionRate.get();
    }
    public static double getPhaseHealthBonus() {
        return ModConfig.EVOLUTION.phaseHealthBonus.get();
    }
    public static double getPhaseDamageBonus() {
        return ModConfig.EVOLUTION.phaseDamageBonus.get();
    }
    public static double getEvolutionHealthBonus() {
        return ModConfig.EVOLUTION.evolutionHealthBonus.get();
    }
    public static double getEvolutionDamageBonus() {
        return ModConfig.EVOLUTION.evolutionDamageBonus.get();
    }

    /**
     * Get kill threshold for a specific phase.
     * Phases 1-4 control parasite ecosystem progression.
     */
    public static int getPhaseKillThreshold(EvoPhase phase) {
        return switch (phase) {
            case PHASE_0 -> 0;
            case PHASE_1 -> ModConfig.EVOLUTION.phase1KillThreshold.get();
            case PHASE_2 -> ModConfig.EVOLUTION.phase2KillThreshold.get();
            case PHASE_3 -> ModConfig.EVOLUTION.phase3KillThreshold.get();
            case PHASE_4 -> ModConfig.EVOLUTION.phase4KillThreshold.get();
            default -> Integer.MAX_VALUE;
        };
    }

    public static int getPhaseProgressionInterval() {
        return ModConfig.EVOLUTION.phaseProgressionInterval.get();
    }

    public static double getPhaseSpeedBonus() {
        return ModConfig.EVOLUTION.phaseSpeedBonus.get();
    }

    // ========== Infection ==========

    public static boolean isInfectionEnabled() {
        return ModConfig.INFECTION.infectionEnabled.get();
    }
    public static double getInfectionSpreadMultiplier() {
        return ModConfig.INFECTION.infectionSpreadMultiplier.get();
    }
    public static double getInfectionConversionMultiplier() {
        return ModConfig.INFECTION.infectionConversionMultiplier.get();
    }
    public static int getCOTHDuration() {
        return ModConfig.INFECTION.cothDuration.get();
    }
    public static boolean isDeathBurstEnabled() {
        return ModConfig.INFECTION.deathBurstEnabled.get();
    }
    public static double getDeathBurstRange() {
        return ModConfig.INFECTION.deathBurstRange.get();
    }
    public static boolean isInfectionAuraEnabled() {
        return ModConfig.INFECTION.infectionAuraEnabled.get();
    }

    // ========== Colony ==========

    public static boolean isColonySystemEnabled() {
        return ModConfig.COLONY.colonySystemEnabled.get();
    }
    public static double getColonyPointRate() {
        return ModConfig.COLONY.colonyPointRate.get();
    }
    public static double getKillColonyPoints() {
        return ModConfig.COLONY.killColonyPoints.get();
    }
    public static int getMaxColonies() {
        return ModConfig.COLONY.maxColonies.get();
    }
    public static int getMinColonySpacing() {
        return ModConfig.COLONY.minColonySpacing.get();
    }
    public static int getMaxColonyUnits() {
        return ModConfig.COLONY.maxColonyUnits.get();
    }
    public static int getColonySpawnCooldown() {
        return ModConfig.COLONY.colonySpawnCooldown.get();
    }
    public static double getColonyPointThreshold() {
        return ModConfig.COLONY.colonyPointThreshold.get();
    }
    public static boolean isColonyStructuresEnabled() {
        return ModConfig.COLONY.colonyStructuresEnabled.get();
    }

    // ========== Mob Cap ==========

    public static int getMobCap() {
        return ModConfig.MOB_CAP.globalMobCap.get();
    }

    /**
     * Get mob cap for a specific ParasiteType based on its evolution tier.
     */
    public static int getTypeMobCap(ParasiteType type) {
        if (type == null) return 0;
        EvolutionPath tier = type.getEvolutionTier();
        return switch (tier) {
            case INFECTED, SPECIAL_INFECTED -> ModConfig.MOB_CAP.infectedCap.get();
            case FERAL -> ModConfig.MOB_CAP.feralCap.get();
            case HIJACKED -> ModConfig.MOB_CAP.hijackedCap.get();
            case INBORN -> ModConfig.MOB_CAP.inbornCap.get();
            case CRUDE -> ModConfig.MOB_CAP.crudeCap.get();
            case PRIMITIVE -> ModConfig.MOB_CAP.primitiveCap.get();
            case ADAPTED -> ModConfig.MOB_CAP.adaptedCap.get();
            case NEXUS -> ModConfig.MOB_CAP.deterrentCap.get();
            case DETERRENT -> ModConfig.MOB_CAP.deterrentCap.get();
            case PURE, PREEMINENT -> ModConfig.MOB_CAP.pureCap.get();
            case ANCIENT, DERIVED -> ModConfig.MOB_CAP.ancientCap.get();
            case ABOMINATION -> ModConfig.MOB_CAP.abominationCap.get();
            default -> 0;
        };
    }

    public static boolean isEmergencyCullEnabled() {
        return ModConfig.MOB_CAP.emergencyCullEnabled.get();
    }
    public static double getEmergencyCullThreshold() {
        return ModConfig.MOB_CAP.emergencyCullThreshold.get();
    }
    public static double getEmergencyCullPercent() {
        return ModConfig.MOB_CAP.emergencyCullPercent.get();
    }

    // ========== World ==========

    public static double getBiomeWeight() {
        return ModConfig.WORLD.biomeWeight.get();
    }
    public static boolean isDimensionBlacklisted(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            ResourceLocation dimId = serverLevel.dimension().location();
            return isDimensionBlacklisted(dimId);
        }
        return false;
    }
    /**
     * Checks if a dimension is blacklisted by its ResourceLocation.
     * Used during dimension checks where a ServerLevel may not yet be available.
     *
     * @param dimId the dimension resource location
     * @return true if the dimension is in the blacklist
     */
    public static boolean isDimensionBlacklisted(ResourceLocation dimId) {
        List<? extends String> blacklist = ModConfig.WORLD.dimensionBlacklist.get();
        return blacklist.contains(dimId.toString());
    }
    /**
     * Returns the raw dimension blacklist from config.
     *
     * @return the list of blacklisted dimension ID strings
     */
    public static List<? extends String> getDimensionBlacklist() {
        return ModConfig.WORLD.dimensionBlacklist.get();
    }
    public static int getMaxSpawnLightLevel() {
        return ModConfig.WORLD.maxSpawnLightLevel.get();
    }
    public static boolean isParasiteBiomeEnabled() {
        return ModConfig.WORLD.parasiteBiomeEnabled.get();
    }
    public static boolean canEvolveInPeaceful() {
        return ModConfig.WORLD.evolveInPeaceful.get();
    }
    public static boolean canSpawnInPeaceful() {
        return ModConfig.WORLD.spawnInPeaceful.get();
    }

    // ========== Combat ==========

    public static boolean isDamageCapEnabled() {
        return ModConfig.COMBAT.damageCapEnabled.get();
    }
    public static int getDefaultDamageCap() {
        return ModConfig.COMBAT.defaultDamageCap.get();
    }
    public static double getMinimumDamage() {
        return ModConfig.COMBAT.minimumDamage.get();
    }
    public static double getAdaptationMax() {
        return ModConfig.COMBAT.adaptationMax.get();
    }
    public static double getAdaptationGainPerHit() {
        return ModConfig.COMBAT.adaptationGainPerHit.get();
    }
    public static int getAdaptationDecayDelay() {
        return ModConfig.COMBAT.adaptationDecayDelay.get();
    }
    public static boolean canParasitesBreakBlocks() {
        return ModConfig.COMBAT.parasitesBreakBlocks.get();
    }
    public static double getMaxBlockHardness() {
        return ModConfig.COMBAT.maxBlockHardness.get();
    }
    public static int getBlockBreakCooldown() {
        return ModConfig.COMBAT.blockBreakCooldown.get();
    }
    public static int getLeapCooldown() {
        return ModConfig.COMBAT.leapCooldown.get();
    }
    public static double getBeckonChance() {
        return ModConfig.COMBAT.beckonChance.get();
    }

    // ========== Gene ==========

    public static boolean isGeneMutationEnabled() {
        return ModConfig.GENE.geneMutationEnabled.get();
    }
    public static double getGeneMutationChance() {
        return ModConfig.GENE.geneMutationChance.get();
    }
    public static boolean isGeneGainEnabled() {
        return ModConfig.GENE.geneGainEnabled.get();
    }
    public static double getGeneGainChance() {
        return ModConfig.GENE.geneGainChance.get();
    }
    public static int getGeneMutationInterval() {
        return ModConfig.GENE.geneMutationInterval.get();
    }
    public static double getGeneMaxFloatValue() {
        return ModConfig.GENE.geneMaxFloatValue.get();
    }

    // ========== Merge ==========

    public static boolean isMergeEnabled() {
        return ModConfig.MERGE.mergeEnabled.get();
    }
    public static int getMergeCooldown() {
        return ModConfig.MERGE.mergeCooldown.get();
    }
    public static double getMergeChanceBase() {
        return ModConfig.MERGE.mergeChanceBase.get();
    }
    public static double getMergeChancePerPhase() {
        return ModConfig.MERGE.mergeChancePerPhase.get();
    }
    public static double getMergeProximityRange() {
        return ModConfig.MERGE.mergeProximityRange.get();
    }
    public static double getMergeSearchRange() {
        return ModConfig.MERGE.mergeSearchRange.get();
    }
    public static int getMergeCheckInterval() {
        return ModConfig.MERGE.mergeCheckInterval.get();
    }
    public static boolean canColonyTriggerMerge() {
        return ModConfig.MERGE.colonyTriggerMerge.get();
    }
    public static boolean shouldTransferGenesOnMerge() {
        return ModConfig.MERGE.transferGenesOnMerge.get();
    }
    public static boolean shouldTransferEvolutionOnMerge() {
        return ModConfig.MERGE.transferEvolutionOnMerge.get();
    }

    // ========== Meteor ==========

    public static boolean areMeteorsEnabled() {
        return ModConfig.METEOR.meteorsEnabled.get();
    }
    public static double getMeteorBaseChance() {
        return ModConfig.METEOR.meteorBaseChance.get();
    }
    public static int getMeteorCheckInterval() {
        return ModConfig.METEOR.meteorCheckInterval.get();
    }
    public static int getMinMeteorPhase() {
        return ModConfig.METEOR.minMeteorPhase.get();
    }
    public static double getMeteorCraterRadiusMultiplier() {
        return ModConfig.METEOR.meteorCraterRadiusMultiplier.get();
    }
    public static int getMeteorFireCount() {
        return ModConfig.METEOR.meteorFireCount.get();
    }
    public static int getMeteorCOTHCount() {
        return ModConfig.METEOR.meteorCOTHCount.get();
    }
    public static int getMeteorParasiteCount() {
        return ModConfig.METEOR.meteorParasiteCount.get();
    }
    public static boolean shouldMeteorsCreateColonies() {
        return ModConfig.METEOR.meteorCreateColonies.get();
    }
    public static boolean shouldMeteorsCreatePermanentInfestation() {
        return ModConfig.METEOR.meteorPermanentInfestation.get();
    }
    public static double getMeteorDamageMultiplier() {
        return ModConfig.METEOR.meteorDamageMultiplier.get();
    }

    // ========== Debug ==========

    public static boolean isLoggingEnabled() {
        return ModConfig.DEBUG.loggingEnabled.get();
    }
    public static boolean shouldLogEvolution() {
        return isLoggingEnabled() && ModConfig.DEBUG.logEvolution.get();
    }
    public static boolean shouldLogInfection() {
        return isLoggingEnabled() && ModConfig.DEBUG.logInfection.get();
    }
    public static boolean shouldLogColony() {
        return isLoggingEnabled() && ModConfig.DEBUG.logColony.get();
    }
    public static boolean shouldLogPhaseChanges() {
        return isLoggingEnabled() && ModConfig.DEBUG.logPhaseChanges.get();
    }
    public static boolean shouldLogCombat() {
        return isLoggingEnabled() && ModConfig.DEBUG.logCombat.get();
    }

    // ========== Utility ==========

    /** Cached set of entity types immune to COTH (Call of the Hive) infection. */
    private static final Set<EntityType<?>> COTH_IMMUNE_SET = Set.of(
            EntityType.ENDER_DRAGON,
            EntityType.WITHER,
            EntityType.PLAYER
    );

    public static boolean isEntityCOTHImmune(EntityType<?> type) {
        return COTH_IMMUNE_SET.contains(type);
    }
}
