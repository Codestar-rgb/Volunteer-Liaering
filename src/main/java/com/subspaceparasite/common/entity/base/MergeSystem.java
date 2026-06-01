package com.subspaceparasite.common.entity.base;

import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.api.parasite.EvolutionPath;
import com.subspaceparasite.api.parasite.GeneType;
import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.config.ModConfigSystems;
import com.subspaceparasite.core.ModEntities;
import com.subspaceparasite.core.ModParticleTypes;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;

/**
 * Centralized merge system that handles the entity-level merge mechanic
 * from the original Scape and Run: Parasites mod.
 * <p>
 * Two compatible parasites of the same tier and compatible type can merge
 * into a higher-tier form. This system manages:
 * <ul>
 *   <li>Merge rule definitions (which types combine into what)</li>
 *   <li>Merge eligibility checking</li>
 *   <li>Merge execution (consume sources, spawn result, transfer stats)</li>
 *   <li>Merge chance calculation based on evolution phase</li>
 * </ul>
 * <p>
 * Merge Rules (EvolutionPath-based):
 * <ul>
 *   <li>2 Infected → 1 Feral (same species)</li>
 *   <li>2 Feral → 1 Primitive (same species)</li>
 *   <li>2 Primitive → 1 Adapted</li>
 *   <li>2 Adapted → 1 Pure</li>
 *   <li>2 Buglins → 1 Alveoli Worm</li>
 *   <li>Beckon + nearby parasites → upgrades beckon tier</li>
 *   <li>Dispatcher + Rooter merge → higher-tier stationary</li>
 * </ul>
 *
 * @author SubspaceParasite Team
 * @since 1.0.0
 */
public class MergeSystem {

    // ========== Singleton ==========

    private static MergeSystem instance;

    public static MergeSystem getInstance() {
        if (instance == null) {
            instance = new MergeSystem();
        }
        return instance;
    }

    // ========== Merge Recipe Registry ==========

    /**
     * Represents a merge recipe: two source ParasiteTypes combine to produce
     * a result ParasiteType.
     */
    public static class MergeRecipe {
        private final ParasiteType sourceA;
        private final ParasiteType sourceB;
        private final ParasiteType result;
        private final boolean sameSpeciesRequired;

        public MergeRecipe(ParasiteType sourceA, ParasiteType sourceB, ParasiteType result, boolean sameSpeciesRequired) {
            this.sourceA = sourceA;
            this.sourceB = sourceB;
            this.result = result;
            this.sameSpeciesRequired = sameSpeciesRequired;
        }

        public ParasiteType getSourceA() { return sourceA; }
        public ParasiteType getSourceB() { return sourceB; }
        public ParasiteType getResult() { return result; }
        public boolean isSameSpeciesRequired() { return sameSpeciesRequired; }
    }

    /** All registered merge recipes. */
    private final List<MergeRecipe> recipes = new ArrayList<>();

    /** Cache for fast lookup: EvolutionPath → result EvolutionPath for standard tier merges. */
    private static final Map<EvolutionPath, EvolutionPath> TIER_MERGE_MAP = new EnumMap<>(EvolutionPath.class);

    static {
        // Standard tier progression via merge
        TIER_MERGE_MAP.put(EvolutionPath.INFECTED, EvolutionPath.FERAL);
        TIER_MERGE_MAP.put(EvolutionPath.SPECIAL_INFECTED, EvolutionPath.FERAL);
        TIER_MERGE_MAP.put(EvolutionPath.FERAL, EvolutionPath.PRIMITIVE);
        TIER_MERGE_MAP.put(EvolutionPath.PRIMITIVE, EvolutionPath.ADAPTED);
        TIER_MERGE_MAP.put(EvolutionPath.ADAPTED, EvolutionPath.PURE);
        TIER_MERGE_MAP.put(EvolutionPath.HIJACKED, EvolutionPath.PRIMITIVE);
        TIER_MERGE_MAP.put(EvolutionPath.INBORN, EvolutionPath.CRUDE);
        TIER_MERGE_MAP.put(EvolutionPath.CRUDE, EvolutionPath.PRIMITIVE);
        TIER_MERGE_MAP.put(EvolutionPath.DETERRENT, EvolutionPath.PURE);
        TIER_MERGE_MAP.put(EvolutionPath.PURE, EvolutionPath.PREEMINENT);
    }

    /** Merge constructor — registers built-in recipes. */
    private MergeSystem() {
        registerBuiltinRecipes();
    }

    /**
     * Registers the built-in merge recipes from the original SRP.
     * Specific type-to-type recipes for special merges (e.g., 2 Buglins → Alveoli).
     */
    private void registerBuiltinRecipes() {
        // 2 Buglins → 1 Alveoli Worm
        registerRecipe(ParasiteType.BUGLIN, ParasiteType.BUGLIN, ParasiteType.WORM, false);

        // 2 Moving Flesh → 1 Worker (crude upgrade)
        registerRecipe(ParasiteType.MOVING_FLESH, ParasiteType.MOVING_FLESH, ParasiteType.WORKER, false);

        // Beckon tier upgrades (beckon + same-tier beckon → next tier beckon)
        registerRecipe(ParasiteType.BECKON_SI, ParasiteType.BECKON_SI, ParasiteType.BECKON_SII, false);
        registerRecipe(ParasiteType.BECKON_SII, ParasiteType.BECKON_SII, ParasiteType.BECKON_SIII, false);
        registerRecipe(ParasiteType.BECKON_SIII, ParasiteType.BECKON_SIII, ParasiteType.BECKON_SIV, false);

        // Dispatcher tier upgrades
        registerRecipe(ParasiteType.DISPATCHER_SI, ParasiteType.DISPATCHER_SI, ParasiteType.DISPATCHER_SII, false);
        registerRecipe(ParasiteType.DISPATCHER_SII, ParasiteType.DISPATCHER_SII, ParasiteType.DISPATCHER_SIII, false);
        registerRecipe(ParasiteType.DISPATCHER_SIII, ParasiteType.DISPATCHER_SIII, ParasiteType.DISPATCHER_SIV, false);

        // Rooter tier upgrades
        registerRecipe(ParasiteType.ROOTER_SI, ParasiteType.ROOTER_SI, ParasiteType.ROOTER_SII, false);
        registerRecipe(ParasiteType.ROOTER_SII, ParasiteType.ROOTER_SII, ParasiteType.ROOTER_SIII, false);
        registerRecipe(ParasiteType.ROOTER_SIII, ParasiteType.ROOTER_SIII, ParasiteType.ROOTER_SIV, false);

        // Dispatcher + Rooter cross-merge → higher-tier stationary
        registerRecipe(ParasiteType.DISPATCHER_SI, ParasiteType.ROOTER_SI, ParasiteType.KYPHOSIS, false);
        registerRecipe(ParasiteType.DISPATCHER_SII, ParasiteType.ROOTER_SII, ParasiteType.SENTRY, false);
        registerRecipe(ParasiteType.DISPATCHER_SIII, ParasiteType.ROOTER_SIII, ParasiteType.SEIZER, false);
    }

    /**
     * Registers a new merge recipe.
     *
     * @param sourceA the first source ParasiteType
     * @param sourceB the second source ParasiteType
     * @param result  the resulting ParasiteType
     * @param sameSpeciesRequired whether both sources must be the exact same ParasiteType
     */
    public void registerRecipe(ParasiteType sourceA, ParasiteType sourceB, ParasiteType result, boolean sameSpeciesRequired) {
        recipes.add(new MergeRecipe(sourceA, sourceB, result, sameSpeciesRequired));
    }

    // ========== Eligibility Checking ==========

    /**
     * Checks whether two parasite entities are eligible to merge.
     * <p>
     * Requirements:
     * <ul>
     *   <li>Both entities must be alive</li>
     *   <li>Neither entity can be on merge cooldown</li>
     *   <li>Both must have a valid ParasiteType</li>
     *   <li>They must be of compatible types (same tier or specific recipe match)</li>
     *   <li>Merge system must be enabled in config</li>
     * </ul>
     *
     * @param a the first parasite
     * @param b the second parasite
     * @return true if both entities can merge
     */
    public boolean canMergeWith(EntityParasiteBase a, EntityParasiteBase b) {
        if (!ModConfigSystems.isMergeEnabled()) return false;
        if (a == b) return false;
        if (!a.isAlive() || !b.isAlive()) return false;
        if (a.getMergeCooldown() > 0 || b.getMergeCooldown() > 0) return false;
        if (a.getParasiteType() == null || b.getParasiteType() == null) return false;

        // Must not be currently fighting
        if (a.getTarget() != null || b.getTarget() != null) return false;

        // Check for specific recipe match first
        if (findSpecificRecipe(a.getParasiteType(), b.getParasiteType()) != null) {
            return true;
        }

        // Check for standard tier merge compatibility
        return areSameTierCompatible(a, b);
    }

    /**
     * Checks whether two parasites are compatible for a standard tier merge.
     * They must be on the same EvolutionPath tier and that tier must have a merge target.
     *
     * @param a the first parasite
     * @param b the second parasite
     * @return true if compatible for tier merge
     */
    protected boolean areSameTierCompatible(EntityParasiteBase a, EntityParasiteBase b) {
        EvolutionPath pathA = a.getParasiteType().getEvolutionTier();
        EvolutionPath pathB = b.getParasiteType().getEvolutionTier();

        // Must be on the same evolution tier
        if (pathA != pathB) return false;

        // Must have a valid merge target tier
        if (!TIER_MERGE_MAP.containsKey(pathA)) return false;

        // Same species requirement: within the same tier, same species name prefix
        // e.g., two SIM_HUMAN → FER_HUMAN; but SIM_HUMAN + SIM_COW cannot tier-merge
        // We check by matching the species suffix after the path prefix
        String nameA = a.getParasiteType().getSerializedName();
        String nameB = b.getParasiteType().getSerializedName();
        String speciesA = extractSpeciesSuffix(nameA);
        String speciesB = extractSpeciesSuffix(nameB);

        return speciesA.equals(speciesB);
    }

    /**
     * Extracts the species suffix from a serialized ParasiteType name.
     * E.g., "sim_human" → "human", "fer_bear" → "bear", "pri_hull" → "hull"
     */
    private String extractSpeciesSuffix(String serializedName) {
        int underscore = serializedName.indexOf('_');
        if (underscore >= 0 && underscore < serializedName.length() - 1) {
            return serializedName.substring(underscore + 1);
        }
        return serializedName;
    }

    // ========== Merge Result Lookup ==========

    /**
     * Returns the ParasiteType that would result from merging two parasites.
     * Returns null if the merge is not valid.
     *
     * @param a the first parasite
     * @param b the second parasite
     * @return the resulting ParasiteType, or null if no valid merge
     */
    public ParasiteType getMergeResult(EntityParasiteBase a, EntityParasiteBase b) {
        if (a.getParasiteType() == null || b.getParasiteType() == null) return null;

        // 1. Check for specific recipe match
        MergeRecipe recipe = findSpecificRecipe(a.getParasiteType(), b.getParasiteType());
        if (recipe != null) {
            return recipe.getResult();
        }

        // 2. Standard tier merge
        EvolutionPath pathA = a.getParasiteType().getEvolutionTier();
        EvolutionPath targetPath = TIER_MERGE_MAP.get(pathA);
        if (targetPath == null) return null;

        // Find the appropriate result type in the target path matching the species
        String species = extractSpeciesSuffix(a.getParasiteType().getSerializedName());
        return findTypeForTierAndSpecies(targetPath, species);
    }

    /**
     * Finds a specific merge recipe matching the two given source types.
     * Recipes are symmetric — (A, B) matches (B, A).
     */
    protected MergeRecipe findSpecificRecipe(ParasiteType typeA, ParasiteType typeB) {
        for (MergeRecipe recipe : recipes) {
            if ((recipe.getSourceA() == typeA && recipe.getSourceB() == typeB) ||
                (recipe.getSourceA() == typeB && recipe.getSourceB() == typeA)) {
                return recipe;
            }
        }
        return null;
    }

    /**
     * Finds a ParasiteType in the given EvolutionPath that matches the species suffix.
     * E.g., (FERAL, "human") → FER_HUMAN
     */
    protected ParasiteType findTypeForTierAndSpecies(EvolutionPath targetPath, String species) {
        for (ParasiteType type : ParasiteType.values()) {
            if (type.getEvolutionTier() == targetPath) {
                String typeSpecies = extractSpeciesSuffix(type.getSerializedName());
                if (typeSpecies.equals(species)) {
                    return type;
                }
            }
        }
        // Fallback: return the first type in the target path
        for (ParasiteType type : ParasiteType.values()) {
            if (type.getEvolutionTier() == targetPath) {
                return type;
            }
        }
        return null;
    }

    // ========== Merge Chance ==========

    /**
     * Calculates the merge chance for the given parasite based on the current
     * evolution phase. Higher phases increase merge probability.
     *
     * @param parasite the parasite attempting to merge
     * @return the probability (0.0–1.0) of merge occurring this check
     */
    public float calculateMergeChance(EntityParasiteBase parasite) {
        float baseChance = (float) ModConfigSystems.getMergeChanceBase();
        float phaseBonus = (float) ModConfigSystems.getMergeChancePerPhase() *
                parasite.getPhaseCreated().getPhaseNumber();

        // Higher evolution levels also increase merge chance
        int evoLevel = parasite.getEvolutionComponent() != null
                ? parasite.getEvolutionComponent().getEvolutionLevel() : 0;
        float evoBonus = evoLevel * 0.002F;

        return Math.min(1.0F, baseChance + phaseBonus + evoBonus);
    }

    // ========== Merge Execution ==========

    /**
     * Performs the merge between two compatible parasites.
     * <p>
     * This method:
     * <ol>
     *   <li>Calculates the merge result type</li>
     *   <li>Plays merge animation/particles</li>
     *   <li>Spawns the result entity at the midpoint</li>
     *   <li>Transfers combined evolution points</li>
     *   <li>Transfers union of active genes</li>
     *   <li>Sets merge cooldown on the result</li>
     *   <li>Consumes (removes) both source entities</li>
     * </ol>
     *
     * @param a the first parasite (initiator)
     * @param b the second parasite (partner)
     * @return the resulting entity, or null if the merge failed
     */
    public EntityParasiteBase performMerge(EntityParasiteBase a, EntityParasiteBase b) {
        if (!canMergeWith(a, b)) return null;

        ParasiteType resultType = getMergeResult(a, b);
        if (resultType == null) return null;

        if (!(a.level() instanceof ServerLevel serverLevel)) return null;

        // Calculate midpoint position
        Vec3 midpoint = a.position().add(b.position()).scale(0.5);

        // Play merge particles at both source locations and midpoint
        spawnMergeParticles(serverLevel, a.position());
        spawnMergeParticles(serverLevel, b.position());
        spawnMergeParticles(serverLevel, midpoint);

        // Create the result entity
        EntityType<? extends EntityParasiteBase> resultEntityType =
                resolveEntityType(resultType, serverLevel);
        if (resultEntityType == null) {
            SubspaceParasite.LOGGER.warn("MergeSystem: Could not resolve entity type for {}",
                    resultType.getSerializedName());
            return null;
        }

        EntityParasiteBase result = resultEntityType.create(serverLevel);
        if (result == null) {
            SubspaceParasite.LOGGER.warn("MergeSystem: Could not create entity for type {}",
                    resultType.getSerializedName());
            return null;
        }

        // Position the result at midpoint
        result.moveTo(midpoint.x, midpoint.y, midpoint.z,
                (a.getYRot() + b.getYRot()) / 2.0F, 0.0F);
        result.setParasiteType(resultType);

        // Transfer evolution points (sum of both sources)
        if (ModConfigSystems.shouldTransferEvolutionOnMerge()) {
            int combinedEvoPoints = a.getEvolutionPoints() + b.getEvolutionPoints();
            if (result.getEvolutionComponent() != null) {
                result.getEvolutionComponent().addEvolutionPoints(combinedEvoPoints);
            } else {
                result.addEvolutionPoints(combinedEvoPoints);
            }
        }

        // Transfer active genes (union of both sources)
        if (ModConfigSystems.shouldTransferGenesOnMerge()) {
            transferGenes(a, b, result);
        }

        // Transfer kill count (sum of both)
        result.setKillCount(a.getKillCount() + b.getKillCount());

        // Transfer colony membership if applicable
        if (a.isColonySpawned() || b.isColonySpawned()) {
            result.setColonySpawned(true);
        }

        // Inherit the higher phase created
        if (b.getPhaseCreated().isAtLeast(a.getPhaseCreated())) {
            result.setPhaseCreated(b.getPhaseCreated());
        }

        // Set merge cooldown on result
        result.setMergeCooldown(ModConfigSystems.getMergeCooldown());

        // Set health to the max of result type (spawning fresh)
        result.setHealth(result.getMaxHealth());

        // Apply bonuses
        result.applyBonuses();

        // Add result entity to world
        serverLevel.addFreshEntity(result);

        // Remove both source entities
        a.discard();
        b.discard();

        if (ModConfigSystems.isLoggingEnabled()) {
            SubspaceParasite.LOGGER.debug("MergeSystem: {} + {} → {} at ({}, {}, {})",
                    a.getParasiteType().getSerializedName(),
                    b.getParasiteType().getSerializedName(),
                    resultType.getSerializedName(),
                    midpoint.x, midpoint.y, midpoint.z);
        }

        return result;
    }

    /**
     * Transfers the union of active genes from both source parasites
     * to the result entity. If either source has a gene active, the
     * result gets it.
     *
     * @param sourceA the first source
     * @param sourceB the second source
     * @param result  the result entity receiving genes
     */
    protected void transferGenes(EntityParasiteBase sourceA, EntityParasiteBase sourceB,
                                 EntityParasiteBase result) {
        // Boolean genes: union (if either has it, result gets it)
        for (int i = 0; i < GeneType.booleanGeneCount(); i++) {
            if (sourceA.getGeneBoolean(i) || sourceB.getGeneBoolean(i)) {
                result.setGeneBoolean(i, true);
            }
        }

        // Float genes: take the maximum value from either source
        for (int i = 0; i < GeneType.floatGeneCount(); i++) {
            float valA = sourceA.getGeneFloat(i);
            float valB = sourceB.getGeneFloat(i);
            float maxVal = Math.max(valA, valB);
            if (maxVal > result.getGeneFloat(i)) {
                result.setGeneFloat(i, maxVal);
            }
        }

        result.applyGeneModifications();
    }

    // ========== Particle Effects ==========

    /**
     * Spawns merge particles at the given position.
     * Uses a mix of ender and spore particles for a dramatic merge effect.
     */
    protected void spawnMergeParticles(ServerLevel level, Vec3 pos) {
        // Inner core: portal particles
        level.sendParticles(ParticleTypes.PORTAL,
                pos.x, pos.y + 0.5, pos.z,
                30, 0.3, 0.5, 0.3, 0.5);

        // Outer ring: spore particles
        try {
            ParticleOptions sporeParticle = ModParticleTypes.SPORE.get();
            level.sendParticles(sporeParticle,
                    pos.x, pos.y + 0.5, pos.z,
                    20, 0.5, 0.5, 0.5, 0.05);
        } catch (Exception e) {
            // Fallback to vanilla if mod particles not available
            level.sendParticles(ParticleTypes.REVERSE_PORTAL,
                    pos.x, pos.y + 0.5, pos.z,
                    15, 0.5, 0.5, 0.5, 0.1);
        }
    }

    // ========== Entity Type Resolution ==========

    /**
     * Resolves the EntityType for a given ParasiteType.
     * This uses the ModEntities registry to find the matching entity type.
     *
     * @param type  the ParasiteType to resolve
     * @param level the server level (for entity creation context)
     * @return the matching EntityType, or null if not found
     */
    @SuppressWarnings("unchecked")
    protected EntityType<? extends EntityParasiteBase> resolveEntityType(ParasiteType type, ServerLevel level) {
        // Map specific ParasiteTypes to their ModEntities registry entries
        // This handles the most common merge results
        return switch (type) {
            // Buglin → Alveoli Worm
            case WORM -> ModEntities.ALVEOLI_WORM.isPresent()
                    ? (EntityType<EntityParasiteBase>) (EntityType<?>) ModEntities.ALVEOLI_WORM.get()
                    : null;
            // Moving Flesh → Worker
            case WORKER -> ModEntities.INBORN_KOL.isPresent()
                    ? (EntityType<EntityParasiteBase>) (EntityType<?>) ModEntities.INBORN_KOL.get()
                    : null;
            // Beckon tiers
            case BECKON_SII -> ModEntities.BECKON_UNCOMMON.isPresent()
                    ? (EntityType<EntityParasiteBase>) (EntityType<?>) ModEntities.BECKON_UNCOMMON.get()
                    : null;
            case BECKON_SIII -> ModEntities.BECKON_RARE.isPresent()
                    ? (EntityType<EntityParasiteBase>) (EntityType<?>) ModEntities.BECKON_RARE.get()
                    : null;
            case BECKON_SIV -> ModEntities.BECKON_EPIC.isPresent()
                    ? (EntityType<EntityParasiteBase>) (EntityType<?>) ModEntities.BECKON_EPIC.get()
                    : null;
            // Dispatcher tiers
            case DISPATCHER_SII -> ModEntities.DISPATCHER_UNCOMMON.isPresent()
                    ? (EntityType<EntityParasiteBase>) (EntityType<?>) ModEntities.DISPATCHER_UNCOMMON.get()
                    : null;
            case DISPATCHER_SIII -> ModEntities.DISPATCHER_RARE.isPresent()
                    ? (EntityType<EntityParasiteBase>) (EntityType<?>) ModEntities.DISPATCHER_RARE.get()
                    : null;
            case DISPATCHER_SIV -> ModEntities.DISPATCHER_EPIC.isPresent()
                    ? (EntityType<EntityParasiteBase>) (EntityType<?>) ModEntities.DISPATCHER_EPIC.get()
                    : null;
            // Rooter tiers
            case ROOTER_SII -> ModEntities.ROOTER_UNCOMMON.isPresent()
                    ? (EntityType<EntityParasiteBase>) (EntityType<?>) ModEntities.ROOTER_UNCOMMON.get()
                    : null;
            case ROOTER_SIII -> ModEntities.ROOTER_RARE.isPresent()
                    ? (EntityType<EntityParasiteBase>) (EntityType<?>) ModEntities.ROOTER_RARE.get()
                    : null;
            case ROOTER_SIV -> ModEntities.ROOTER_EPIC.isPresent()
                    ? (EntityType<EntityParasiteBase>) (EntityType<?>) ModEntities.ROOTER_EPIC.get()
                    : null;
            // Nexus cross-merge results
            case KYPHOSIS -> ModEntities.NEXUS_CONSTRUCT.isPresent()
                    ? (EntityType<EntityParasiteBase>) (EntityType<?>) ModEntities.NEXUS_CONSTRUCT.get()
                    : null;
            case SENTRY -> ModEntities.NEXUS_OVERSEER.isPresent()
                    ? (EntityType<EntityParasiteBase>) (EntityType<?>) ModEntities.NEXUS_OVERSEER.get()
                    : null;
            case SEIZER -> ModEntities.NEXUS_GUARD.isPresent()
                    ? (EntityType<EntityParasiteBase>) (EntityType<?>) ModEntities.NEXUS_GUARD.get()
                    : null;
            // Feral results (from Infected merge)
            case FER_BEAR -> ModEntities.FERAL_IRON_GOLEM.isPresent()
                    ? (EntityType<EntityParasiteBase>) (EntityType<?>) ModEntities.FERAL_IRON_GOLEM.get()
                    : null;
            case FER_COW -> ModEntities.FERAL_COW.isPresent()
                    ? (EntityType<EntityParasiteBase>) (EntityType<?>) ModEntities.FERAL_COW.get()
                    : null;
            case FER_HUMAN -> ModEntities.FERAL_HUMAN.isPresent()
                    ? (EntityType<EntityParasiteBase>) (EntityType<?>) ModEntities.FERAL_HUMAN.get()
                    : null;
            case FER_CREEPER -> ModEntities.FERAL_CREEPER.isPresent()
                    ? (EntityType<EntityParasiteBase>) (EntityType<?>) ModEntities.FERAL_CREEPER.get()
                    : null;
            case FER_SKELETON -> ModEntities.FERAL_SKELETON.isPresent()
                    ? (EntityType<EntityParasiteBase>) (EntityType<?>) ModEntities.FERAL_SKELETON.get()
                    : null;
            case FER_ZOMBIE -> ModEntities.FERAL_ZOMBIE.isPresent()
                    ? (EntityType<EntityParasiteBase>) (EntityType<?>) ModEntities.FERAL_ZOMBIE.get()
                    : null;
            case FER_SPIDER -> ModEntities.FERAL_SPIDER.isPresent()
                    ? (EntityType<EntityParasiteBase>) (EntityType<?>) ModEntities.FERAL_SPIDER.get()
                    : null;
            case FER_ENDERMAN -> ModEntities.FERAL_ENDERMAN.isPresent()
                    ? (EntityType<EntityParasiteBase>) (EntityType<?>) ModEntities.FERAL_ENDERMAN.get()
                    : null;
            case FER_WOLF -> ModEntities.FERAL_WOLF.isPresent()
                    ? (EntityType<EntityParasiteBase>) (EntityType<?>) ModEntities.FERAL_WOLF.get()
                    : null;
            case FER_IRONGOLEM -> ModEntities.FERAL_IRON_GOLEM.isPresent()
                    ? (EntityType<EntityParasiteBase>) (EntityType<?>) ModEntities.FERAL_IRON_GOLEM.get()
                    : null;
            // Primitive results (from Feral merge)
            case PRI_HULL -> ModEntities.PRIMITIVE_HULL.isPresent()
                    ? (EntityType<EntityParasiteBase>) (EntityType<?>) ModEntities.PRIMITIVE_HULL.get()
                    : null;
            case PRI_EMANA -> ModEntities.PRIMITIVE_EMANA.isPresent()
                    ? (EntityType<EntityParasiteBase>) (EntityType<?>) ModEntities.PRIMITIVE_EMANA.get()
                    : null;
            // Generic fallback — try to find a matching entity by ParasiteType
            default -> resolveEntityTypeByPath(type);
        };
    }

    /**
     * Fallback resolution: attempts to find an entity type matching the
     * ParasiteType's evolution tier. Used when specific mappings aren't available.
     */
    @SuppressWarnings("unchecked")
    protected EntityType<? extends EntityParasiteBase> resolveEntityTypeByPath(ParasiteType type) {
        // Try to find a generic entity type for the target evolution path
        EvolutionPath path = type.getEvolutionTier();
        return switch (path) {
            case FERAL -> ModEntities.FERAL_HUMAN.isPresent()
                    ? (EntityType<EntityParasiteBase>) (EntityType<?>) ModEntities.FERAL_HUMAN.get()
                    : null;
            case PRIMITIVE -> ModEntities.PRIMITIVE_EMANA.isPresent()
                    ? (EntityType<EntityParasiteBase>) (EntityType<?>) ModEntities.PRIMITIVE_EMANA.get()
                    : null;
            case ADAPTED -> ModEntities.ADAPTED_EMANA.isPresent()
                    ? (EntityType<EntityParasiteBase>) (EntityType<?>) ModEntities.ADAPTED_EMANA.get()
                    : null;
            case PURE -> ModEntities.PURE_FLAM.isPresent()
                    ? (EntityType<EntityParasiteBase>) (EntityType<?>) ModEntities.PURE_FLAM.get()
                    : null;
            case CRUDE -> ModEntities.CRUDE_MOVING_FLESH.isPresent()
                    ? (EntityType<EntityParasiteBase>) (EntityType<?>) ModEntities.CRUDE_MOVING_FLESH.get()
                    : null;
            case NEXUS -> ModEntities.BECKON_COMMON.isPresent()
                    ? (EntityType<EntityParasiteBase>) (EntityType<?>) ModEntities.BECKON_COMMON.get()
                    : null;
            default -> null;
        };
    }

    // ========== Colony Integration ==========

    /**
     * Called by ColonyComponent to trigger merges among colony members.
     * Finds compatible pairs among colony units and attempts to merge them.
     *
     * @param colonyLeader the colony leader entity
     * @param colonyMembers the list of colony member entities
     */
    public void triggerColonyMerges(EntityParasiteBase colonyLeader,
                                     List<EntityParasiteBase> colonyMembers) {
        if (!ModConfigSystems.canColonyTriggerMerge()) return;
        if (!ModConfigSystems.isMergeEnabled()) return;
        if (colonyMembers.size() < 2) return;

        // Find compatible pairs and attempt merges
        List<EntityParasiteBase> merged = new ArrayList<>();

        for (int i = 0; i < colonyMembers.size(); i++) {
            EntityParasiteBase a = colonyMembers.get(i);
            if (merged.contains(a) || !a.isAlive()) continue;

            for (int j = i + 1; j < colonyMembers.size(); j++) {
                EntityParasiteBase b = colonyMembers.get(j);
                if (merged.contains(b) || !b.isAlive()) continue;

                if (canMergeWith(a, b)) {
                    // Colony-triggered merges have a higher chance
                    float chance = calculateMergeChance(a) * 1.5F;
                    if (a.getRandom().nextFloat() < Math.min(1.0F, chance)) {
                        EntityParasiteBase result = performMerge(a, b);
                        if (result != null) {
                            merged.add(a);
                            merged.add(b);
                            // Make the result a colony member too
                            result.setColonySpawned(true);
                            if (colonyLeader.getColonyComponent() != null) {
                                result.setOwner(colonyLeader);
                            }
                            break; // Only one merge per initiator per check
                        }
                    }
                }
            }
        }
    }

    // ========== Beckon Upgrade ==========

    /**
     * Attempts to upgrade a Beckon by consuming nearby lower-tier parasites.
     * This is a special merge variant where the Beckon absorbs nearby parasites
     * to advance to its next tier.
     *
     * @param beckon the beckon entity to upgrade
     * @return the upgraded beckon, or null if upgrade failed
     */
    public EntityParasiteBase attemptBeckonUpgrade(EntityParasiteBase beckon) {
        if (!ModConfigSystems.isMergeEnabled()) return null;
        if (beckon.getMergeCooldown() > 0) return null;
        if (!(beckon.level() instanceof ServerLevel serverLevel)) return null;

        ParasiteType beckonType = beckon.getParasiteType();
        if (beckonType == null) return null;

        // Only beckons can use this upgrade path
        EvolutionPath path = beckonType.getEvolutionTier();
        if (path != EvolutionPath.NEXUS) return null;

        // Find the upgrade recipe for this beckon
        MergeRecipe upgradeRecipe = null;
        for (MergeRecipe recipe : recipes) {
            if (recipe.getSourceA() == beckonType && recipe.getSourceB() == beckonType) {
                upgradeRecipe = recipe;
                break;
            }
        }
        if (upgradeRecipe == null) return null;

        // Search for another beckon of the same type nearby
        double searchRange = ModConfigSystems.getMergeSearchRange();
        AABB searchArea = beckon.getBoundingBox().inflate(searchRange);
        List<EntityParasiteBase> nearby = serverLevel.getEntitiesOfClass(
                EntityParasiteBase.class, searchArea,
                p -> p != beckon && p.isAlive()
                        && p.getParasiteType() == beckonType
                        && p.getMergeCooldown() <= 0
                        && p.getTarget() == null);

        if (nearby.isEmpty()) return null;

        // Pick the closest one
        EntityParasiteBase partner = nearby.get(0);
        double minDist = Double.MAX_VALUE;
        for (EntityParasiteBase candidate : nearby) {
            double dist = beckon.distanceToSqr(candidate);
            if (dist < minDist) {
                minDist = dist;
                partner = candidate;
            }
        }

        // Must be within proximity range
        double proximity = ModConfigSystems.getMergeProximityRange();
        if (beckon.distanceToSqr(partner) > proximity * proximity) return null;

        return performMerge(beckon, partner);
    }

    // ========== Accessors ==========

    /** Returns all registered merge recipes. */
    public List<MergeRecipe> getRecipes() {
        return Collections.unmodifiableList(recipes);
    }

    /** Returns the tier merge target map. */
    public Map<EvolutionPath, EvolutionPath> getTierMergeMap() {
        return Collections.unmodifiableMap(TIER_MERGE_MAP);
    }
}
