package com.subspaceparasite.config;

import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Main configuration class for the SubspaceParasite mod.
 * Uses Forge's ForgeConfigSpec system with categories matching
 * the original SRP configuration structure.
 *
 * Categories:
 * - Evolution: Phase speeds, stat multipliers, thresholds
 * - Infection: Spread chance, conversion chance, COTH settings
 * - Colony: Point costs, spawn limits, structure settings
 * - Mob Cap: Per-type caps, emergency cull settings
 * - World: Biome weight, dimension blacklist
 * - Combat: Damage caps, minimum damage, adaptation
 * - Gene: Mutation chance, gain chance
 * - Debug: Logging enabled
 */
public class ModConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    // ========== Evolution Settings ==========
    public static final Evolution EVOLUTION;

    // ========== Infection Settings ==========
    public static final Infection INFECTION;

    // ========== Colony Settings ==========
    public static final Colony COLONY;

    // ========== Mob Cap Settings ==========
    public static final MobCap MOB_CAP;

    // ========== World Settings ==========
    public static final World WORLD;

    // ========== Combat Settings ==========
    public static final Combat COMBAT;

    // ========== Gene Settings ==========
    public static final Gene GENE;

    // ========== Debug Settings ==========
    public static final Debug DEBUG;

    // ========== Evolution Category ==========
    public static class Evolution {
        // Phase progression
        public final ForgeConfigSpec.IntValue phase1KillThreshold;
        public final ForgeConfigSpec.IntValue phase2KillThreshold;
        public final ForgeConfigSpec.IntValue phase3KillThreshold;
        public final ForgeConfigSpec.IntValue phase4KillThreshold;
        public final ForgeConfigSpec.IntValue phase5KillThreshold;

        // Phase speeds (ticks per phase progression check)
        public final ForgeConfigSpec.IntValue phaseProgressionInterval;

        // Stat multipliers per phase
        public final ForgeConfigSpec.DoubleValue phaseHealthBonus;
        public final ForgeConfigSpec.DoubleValue phaseDamageBonus;
        public final ForgeConfigSpec.DoubleValue phaseSpeedBonus;

        // Evolution settings
        public final ForgeConfigSpec.BooleanValue evolutionEnabled;
        public final ForgeConfigSpec.DoubleValue naturalEvolutionRate;
        public final ForgeConfigSpec.DoubleValue killEvolutionRate;
        public final ForgeConfigSpec.DoubleValue evolutionHealthBonus;
        public final ForgeConfigSpec.DoubleValue evolutionDamageBonus;
        public final ForgeConfigSpec.DoubleValue evolutionThreshold;

        Evolution(ForgeConfigSpec.Builder builder) {
            builder.comment("Evolution system settings").push("evolution");

            phase1KillThreshold = builder
                    .comment("Kill count threshold to enter Phase 1 (Emergence)")
                    .defineInRange("phase1KillThreshold", 50, 0, Integer.MAX_VALUE);

            phase2KillThreshold = builder
                    .comment("Kill count threshold to enter Phase 2 (Spread)")
                    .defineInRange("phase2KillThreshold", 200, 0, Integer.MAX_VALUE);

            phase3KillThreshold = builder
                    .comment("Kill count threshold to enter Phase 3 (Assimilation)")
                    .defineInRange("phase3KillThreshold", 500, 0, Integer.MAX_VALUE);

            phase4KillThreshold = builder
                    .comment("Kill count threshold to enter Phase 4 (Convergence)")
                    .defineInRange("phase4KillThreshold", 1500, 0, Integer.MAX_VALUE);

            phase5KillThreshold = builder
                    .comment("Kill count threshold to enter Phase 5 (Apex)")
                    .defineInRange("phase5KillThreshold", 5000, 0, Integer.MAX_VALUE);

            phaseProgressionInterval = builder
                    .comment("Ticks between phase progression checks (20 ticks = 1 second)")
                    .defineInRange("phaseProgressionInterval", 200, 20, 6000);

            phaseHealthBonus = builder
                    .comment("Health multiplier bonus per phase level")
                    .defineInRange("phaseHealthBonus", 0.15, 0.0, 5.0);

            phaseDamageBonus = builder
                    .comment("Damage multiplier bonus per phase level")
                    .defineInRange("phaseDamageBonus", 0.1, 0.0, 5.0);

            phaseSpeedBonus = builder
                    .comment("Speed multiplier bonus per phase level")
                    .defineInRange("phaseSpeedBonus", 0.05, 0.0, 2.0);

            evolutionEnabled = builder
                    .comment("Whether the evolution system is enabled")
                    .define("evolutionEnabled", true);

            naturalEvolutionRate = builder
                    .comment("Evolution points gained per accumulation tick")
                    .defineInRange("naturalEvolutionRate", 2.0, 0.0, 100.0);

            killEvolutionRate = builder
                    .comment("Evolution points gained per kill")
                    .defineInRange("killEvolutionRate", 5.0, 0.0, 100.0);

            evolutionHealthBonus = builder
                    .comment("Health bonus per evolution level")
                    .defineInRange("evolutionHealthBonus", 0.1, 0.0, 5.0);

            evolutionDamageBonus = builder
                    .comment("Damage bonus per evolution level")
                    .defineInRange("evolutionDamageBonus", 0.08, 0.0, 5.0);

            evolutionThreshold = builder
                    .comment("Base evolution point threshold before first evolution")
                    .defineInRange("evolutionThreshold", 100.0, 10.0, 10000.0);

            builder.pop();
        }
    }

    // ========== Infection Category ==========
    public static class Infection {
        public final ForgeConfigSpec.BooleanValue infectionEnabled;
        public final ForgeConfigSpec.DoubleValue infectionSpreadChance;
        public final ForgeConfigSpec.DoubleValue infectionConversionChance;
        public final ForgeConfigSpec.DoubleValue infectionSpreadMultiplier;
        public final ForgeConfigSpec.DoubleValue infectionConversionMultiplier;
        public final ForgeConfigSpec.IntValue cothDuration;
        public final ForgeConfigSpec.BooleanValue deathBurstEnabled;
        public final ForgeConfigSpec.DoubleValue deathBurstRange;
        public final ForgeConfigSpec.BooleanValue infectionAuraEnabled;

        Infection(ForgeConfigSpec.Builder builder) {
            builder.comment("Infection (COTH) system settings").push("infection");

            infectionEnabled = builder
                    .comment("Whether the COTH infection system is enabled")
                    .define("infectionEnabled", true);

            infectionSpreadChance = builder
                    .comment("Base chance for COTH to spread on attack (0.0-1.0)")
                    .defineInRange("infectionSpreadChance", 0.15, 0.0, 1.0);

            infectionConversionChance = builder
                    .comment("Base chance for an infected entity to convert into a parasite")
                    .defineInRange("infectionConversionChance", 0.05, 0.0, 1.0);

            infectionSpreadMultiplier = builder
                    .comment("Global multiplier for infection spread chance")
                    .defineInRange("infectionSpreadMultiplier", 1.0, 0.0, 10.0);

            infectionConversionMultiplier = builder
                    .comment("Global multiplier for infection conversion chance")
                    .defineInRange("infectionConversionMultiplier", 1.0, 0.0, 10.0);

            cothDuration = builder
                    .comment("Duration of COTH effect in ticks")
                    .defineInRange("cothDuration", 200, 20, 6000);

            deathBurstEnabled = builder
                    .comment("Whether parasites spread COTH in a burst on death")
                    .define("deathBurstEnabled", true);

            deathBurstRange = builder
                    .comment("Range of COTH burst on death")
                    .defineInRange("deathBurstRange", 3.0, 1.0, 16.0);

            infectionAuraEnabled = builder
                    .comment("Whether evolved parasites have passive COTH auras")
                    .define("infectionAuraEnabled", true);

            builder.pop();
        }
    }

    // ========== Colony Category ==========
    public static class Colony {
        public final ForgeConfigSpec.BooleanValue colonySystemEnabled;
        public final ForgeConfigSpec.DoubleValue colonyPointRate;
        public final ForgeConfigSpec.DoubleValue killColonyPoints;
        public final ForgeConfigSpec.IntValue maxColonies;
        public final ForgeConfigSpec.IntValue minColonySpacing;
        public final ForgeConfigSpec.IntValue maxColonyUnits;
        public final ForgeConfigSpec.IntValue colonySpawnCooldown;
        public final ForgeConfigSpec.DoubleValue colonyPointThreshold;
        public final ForgeConfigSpec.BooleanValue colonyStructuresEnabled;

        Colony(ForgeConfigSpec.Builder builder) {
            builder.comment("Colony system settings").push("colony");

            colonySystemEnabled = builder
                    .comment("Whether the colony system is enabled")
                    .define("colonySystemEnabled", true);

            colonyPointRate = builder
                    .comment("Colony points gained per accumulation tick")
                    .defineInRange("colonyPointRate", 1.0, 0.0, 100.0);

            killColonyPoints = builder
                    .comment("Colony points gained per kill")
                    .defineInRange("killColonyPoints", 5.0, 0.0, 100.0);

            maxColonies = builder
                    .comment("Maximum number of colonies per dimension")
                    .defineInRange("maxColonies", 5, 1, 50);

            minColonySpacing = builder
                    .comment("Minimum distance in blocks between colony centers")
                    .defineInRange("minColonySpacing", 128, 32, 1024);

            maxColonyUnits = builder
                    .comment("Maximum number of units per colony")
                    .defineInRange("maxColonyUnits", 8, 1, 64);

            colonySpawnCooldown = builder
                    .comment("Cooldown in ticks between colony unit spawns")
                    .defineInRange("colonySpawnCooldown", 2400, 200, 12000);

            colonyPointThreshold = builder
                    .comment("Colony points needed to form a new colony")
                    .defineInRange("colonyPointThreshold", 500.0, 100.0, 10000.0);

            colonyStructuresEnabled = builder
                    .comment("Whether colony structures are generated")
                    .define("colonyStructuresEnabled", true);

            builder.pop();
        }
    }

    // ========== Mob Cap Category ==========
    public static class MobCap {
        public final ForgeConfigSpec.IntValue globalMobCap;
        public final ForgeConfigSpec.IntValue primitiveCap;
        public final ForgeConfigSpec.IntValue feralCap;
        public final ForgeConfigSpec.IntValue infectedCap;
        public final ForgeConfigSpec.IntValue hijackedCap;
        public final ForgeConfigSpec.IntValue crudeCap;
        public final ForgeConfigSpec.IntValue derivedCap;
        public final ForgeConfigSpec.IntValue adaptedCap;
        public final ForgeConfigSpec.IntValue inbornCap;
        public final ForgeConfigSpec.IntValue abominationCap;
        public final ForgeConfigSpec.IntValue ancientCap;
        public final ForgeConfigSpec.IntValue deterrentCap;
        public final ForgeConfigSpec.IntValue pureCap;
        public final ForgeConfigSpec.BooleanValue emergencyCullEnabled;
        public final ForgeConfigSpec.DoubleValue emergencyCullThreshold;
        public final ForgeConfigSpec.DoubleValue emergencyCullPercent;

        MobCap(ForgeConfigSpec.Builder builder) {
            builder.comment("Mob cap settings").push("mob_cap");

            globalMobCap = builder
                    .comment("Global parasite entity cap per dimension (0 = unlimited)")
                    .defineInRange("globalMobCap", 100, 0, 500);

            primitiveCap = builder
                    .comment("Cap for primitive type parasites")
                    .defineInRange("primitiveCap", 30, 0, 200);

            feralCap = builder
                    .comment("Cap for feral type parasites")
                    .defineInRange("feralCap", 20, 0, 200);

            infectedCap = builder
                    .comment("Cap for infected type parasites")
                    .defineInRange("infectedCap", 25, 0, 200);

            hijackedCap = builder
                    .comment("Cap for hijacked type parasites")
                    .defineInRange("hijackedCap", 15, 0, 200);

            crudeCap = builder
                    .comment("Cap for crude type parasites")
                    .defineInRange("crudeCap", 15, 0, 200);

            derivedCap = builder
                    .comment("Cap for derived type parasites")
                    .defineInRange("derivedCap", 10, 0, 200);

            adaptedCap = builder
                    .comment("Cap for adapted type parasites")
                    .defineInRange("adaptedCap", 8, 0, 200);

            inbornCap = builder
                    .comment("Cap for inborn type parasites")
                    .defineInRange("inbornCap", 5, 0, 200);

            abominationCap = builder
                    .comment("Cap for abomination type parasites")
                    .defineInRange("abominationCap", 3, 0, 50);

            ancientCap = builder
                    .comment("Cap for ancient type parasites")
                    .defineInRange("ancientCap", 2, 0, 20);

            deterrentCap = builder
                    .comment("Cap for deterrent type parasites")
                    .defineInRange("deterrentCap", 10, 0, 100);

            pureCap = builder
                    .comment("Cap for pure type parasites")
                    .defineInRange("pureCap", 1, 0, 10);

            emergencyCullEnabled = builder
                    .comment("Whether emergency culling is enabled when mob cap is exceeded")
                    .define("emergencyCullEnabled", true);

            emergencyCullThreshold = builder
                    .comment("Threshold multiplier above mob cap before emergency cull triggers")
                    .defineInRange("emergencyCullThreshold", 1.5, 1.0, 5.0);

            emergencyCullPercent = builder
                    .comment("Percentage of excess entities to cull (0.0-1.0)")
                    .defineInRange("emergencyCullPercent", 0.5, 0.0, 1.0);

            builder.pop();
        }
    }

    // ========== World Category ==========
    public static class World {
        public final ForgeConfigSpec.DoubleValue biomeWeight;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> dimensionBlacklist;
        public final ForgeConfigSpec.IntValue maxSpawnLightLevel;
        public final ForgeConfigSpec.BooleanValue parasiteBiomeEnabled;
        public final ForgeConfigSpec.DoubleValue parasiteBiomeWeight;
        public final ForgeConfigSpec.BooleanValue evolveInPeaceful;
        public final ForgeConfigSpec.BooleanValue spawnInPeaceful;

        World(ForgeConfigSpec.Builder builder) {
            builder.comment("World and dimension settings").push("world");

            biomeWeight = builder
                    .comment("Weight for parasite biome generation (0.0 = disabled)")
                    .defineInRange("biomeWeight", 2.0, 0.0, 100.0);

            dimensionBlacklist = builder
                    .comment("List of dimension IDs where parasites cannot spawn",
                             "Example: [\"minecraft:the_end\", \"othermod:custom_dim\"]")
                    .defineList("dimensionBlacklist", java.util.Collections.emptyList(),
                            obj -> obj instanceof String);

            maxSpawnLightLevel = builder
                    .comment("Maximum light level for parasite natural spawning")
                    .defineInRange("maxSpawnLightLevel", 7, 0, 15);

            parasiteBiomeEnabled = builder
                    .comment("Whether the parasite biome can generate in the world")
                    .define("parasiteBiomeEnabled", true);

            parasiteBiomeWeight = builder
                    .comment("Generation weight for parasite biome")
                    .defineInRange("parasiteBiomeWeight", 1.0, 0.0, 50.0);

            evolveInPeaceful = builder
                    .comment("Whether parasites can evolve on peaceful difficulty")
                    .define("evolveInPeaceful", false);

            spawnInPeaceful = builder
                    .comment("Whether parasites can spawn on peaceful difficulty")
                    .define("spawnInPeaceful", false);

            builder.pop();
        }
    }

    // ========== Combat Category ==========
    public static class Combat {
        public final ForgeConfigSpec.BooleanValue damageCapEnabled;
        public final ForgeConfigSpec.IntValue defaultDamageCap;
        public final ForgeConfigSpec.DoubleValue minimumDamage;
        public final ForgeConfigSpec.DoubleValue adaptationMax;
        public final ForgeConfigSpec.DoubleValue adaptationGainPerHit;
        public final ForgeConfigSpec.IntValue adaptationDecayDelay;
        public final ForgeConfigSpec.BooleanValue parasitesBreakBlocks;
        public final ForgeConfigSpec.DoubleValue maxBlockHardness;
        public final ForgeConfigSpec.IntValue blockBreakCooldown;
        public final ForgeConfigSpec.IntValue leapCooldown;
        public final ForgeConfigSpec.DoubleValue beckonChance;

        Combat(ForgeConfigSpec.Builder builder) {
            builder.comment("Combat system settings").push("combat");

            damageCapEnabled = builder
                    .comment("Whether the damage cap system is enabled for parasites")
                    .define("damageCapEnabled", true);

            defaultDamageCap = builder
                    .comment("Default damage cap divisor (damage cap = maxHealth / this value)")
                    .defineInRange("defaultDamageCap", 10, 1, 100);

            minimumDamage = builder
                    .comment("Minimum damage that parasites deal per hit")
                    .defineInRange("minimumDamage", 1.0, 0.0, 20.0);

            adaptationMax = builder
                    .comment("Maximum adaptation level (0.0-1.0, reduces incoming damage)")
                    .defineInRange("adaptationMax", 0.75, 0.0, 1.0);

            adaptationGainPerHit = builder
                    .comment("Adaptation gained per same-type hit")
                    .defineInRange("adaptationGainPerHit", 0.03, 0.0, 1.0);

            adaptationDecayDelay = builder
                    .comment("Ticks before adaptation starts decaying after last hit")
                    .defineInRange("adaptationDecayDelay", 200, 0, 6000);

            parasitesBreakBlocks = builder
                    .comment("Whether parasites can break blocks")
                    .define("parasitesBreakBlocks", true);

            maxBlockHardness = builder
                    .comment("Maximum block hardness parasites can break")
                    .defineInRange("maxBlockHardness", 3.0, 0.0, 50.0);

            blockBreakCooldown = builder
                    .comment("Cooldown in ticks between block break attempts")
                    .defineInRange("blockBreakCooldown", 60, 10, 600);

            leapCooldown = builder
                    .comment("Cooldown in ticks between leap attacks")
                    .defineInRange("leapCooldown", 80, 10, 600);

            beckonChance = builder
                    .comment("Chance for a dying parasite to alert nearby parasites (0.0-1.0)")
                    .defineInRange("beckonChance", 0.3, 0.0, 1.0);

            builder.pop();
        }
    }

    // ========== Gene Category ==========
    public static class Gene {
        public final ForgeConfigSpec.BooleanValue geneMutationEnabled;
        public final ForgeConfigSpec.DoubleValue geneMutationChance;
        public final ForgeConfigSpec.BooleanValue geneGainEnabled;
        public final ForgeConfigSpec.DoubleValue geneGainChance;
        public final ForgeConfigSpec.IntValue geneMutationInterval;
        public final ForgeConfigSpec.DoubleValue geneMaxFloatValue;

        Gene(ForgeConfigSpec.Builder builder) {
            builder.comment("Gene system settings").push("gene");

            geneMutationEnabled = builder
                    .comment("Whether random gene mutations can occur")
                    .define("geneMutationEnabled", true);

            geneMutationChance = builder
                    .comment("Chance per mutation check for a gene to mutate (0.0-1.0)")
                    .defineInRange("geneMutationChance", 0.05, 0.0, 1.0);

            geneGainEnabled = builder
                    .comment("Whether genes are gained on evolution")
                    .define("geneGainEnabled", true);

            geneGainChance = builder
                    .comment("Chance per evolution for a new gene to activate (0.0-1.0)")
                    .defineInRange("geneGainChance", 0.3, 0.0, 1.0);

            geneMutationInterval = builder
                    .comment("Ticks between gene mutation checks")
                    .defineInRange("geneMutationInterval", 2400, 200, 12000);

            geneMaxFloatValue = builder
                    .comment("Maximum value for gene float modifiers")
                    .defineInRange("geneMaxFloatValue", 1.0, 0.1, 10.0);

            builder.pop();
        }
    }

    // ========== Debug Category ==========
    public static class Debug {
        public final ForgeConfigSpec.BooleanValue loggingEnabled;
        public final ForgeConfigSpec.BooleanValue logEvolution;
        public final ForgeConfigSpec.BooleanValue logInfection;
        public final ForgeConfigSpec.BooleanValue logColony;
        public final ForgeConfigSpec.BooleanValue logPhaseChanges;
        public final ForgeConfigSpec.BooleanValue logCombat;

        Debug(ForgeConfigSpec.Builder builder) {
            builder.comment("Debug and logging settings").push("debug");

            loggingEnabled = builder
                    .comment("Master switch for debug logging")
                    .define("loggingEnabled", false);

            logEvolution = builder
                    .comment("Log evolution events")
                    .define("logEvolution", false);

            logInfection = builder
                    .comment("Log infection/COTH events")
                    .define("logInfection", false);

            logColony = builder
                    .comment("Log colony events")
                    .define("logColony", false);

            logPhaseChanges = builder
                    .comment("Log phase progression events")
                    .define("logPhaseChanges", true);

            logCombat = builder
                    .comment("Log combat/adaptation events")
                    .define("logCombat", false);

            builder.pop();
        }
    }

    // Static initialization
    static {
        EVOLUTION = new Evolution(BUILDER);
        INFECTION = new Infection(BUILDER);
        COLONY = new Colony(BUILDER);
        MOB_CAP = new MobCap(BUILDER);
        WORLD = new World(BUILDER);
        COMBAT = new Combat(BUILDER);
        GENE = new Gene(BUILDER);
        DEBUG = new Debug(BUILDER);

        SPEC = BUILDER.build();
    }
}
