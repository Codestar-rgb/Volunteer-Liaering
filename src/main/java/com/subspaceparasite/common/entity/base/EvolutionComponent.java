package com.subspaceparasite.common.entity.base;

import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.api.parasite.EvolutionPath;
import com.subspaceparasite.api.parasite.GeneType;
import com.subspaceparasite.common.world.ModWorldData;
import com.subspaceparasite.common.world.SRPDifficultySetting;
import com.subspaceparasite.config.ModConfigSystems;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Component handling evolution logic for parasite entities.
 * Manages natural evolution point accumulation, gene mutation,
 * evolution threshold checking, gene gain on evolution, and
 * evolution tier transformation.
 */
public class EvolutionComponent {

    private final EntityParasiteBase parasite;

    double evolutionPointsInternal;
    private double evolutionThreshold;
    private int accumulationTimer;
    private int accumulationInterval;
    private int mutationTimer;
    private int mutationCheckInterval;
    int evolutionLevel;
    private boolean canEvolve;
    private boolean hasMutated;

    private static final int DEFAULT_ACCUMULATION_INTERVAL = 600;
    private static final int DEFAULT_MUTATION_INTERVAL = 2400;
    private static final double BASE_EVOLUTION_THRESHOLD = 100.0;
    private static final double THRESHOLD_GROWTH_RATE = 1.5;

    public EvolutionComponent(EntityParasiteBase parasite) {
        this.parasite = parasite;
        this.evolutionPointsInternal = 0.0;
        this.evolutionLevel = 0;
        this.evolutionThreshold = BASE_EVOLUTION_THRESHOLD;
        this.accumulationTimer = 0;
        this.accumulationInterval = DEFAULT_ACCUMULATION_INTERVAL;
        this.mutationTimer = 0;
        this.mutationCheckInterval = DEFAULT_MUTATION_INTERVAL;
        this.canEvolve = true;
        this.hasMutated = false;
    }

    public void tick() {
        if (!canEvolve) return;
        if (parasite.level().isClientSide) return;

        accumulationTimer++;
        if (accumulationTimer >= accumulationInterval) {
            accumulationTimer = 0;
            addEvolutionPoints(ModConfigSystems.getNaturalEvolutionRate());
        }

        mutationTimer++;
        if (mutationTimer >= mutationCheckInterval) {
            mutationTimer = 0;
            checkGeneMutation();
        }

        if (evolutionPointsInternal >= evolutionThreshold) {
            attemptEvolution();
        }
    }

    public void addEvolutionPoints(double points) {
        if (!canEvolve) return;
        float phaseMult = 1.0F + parasite.getPhaseCreated().getPhaseNumber() * 0.2F;
        // Apply difficulty multiplier from SRPDifficultySetting
        float difficultyMult = 1.0F;
        if (parasite.level() instanceof ServerLevel serverLevel) {
            SRPDifficultySetting difficulty = ModWorldData.get(serverLevel).getSRPDifficulty();
            difficultyMult = difficulty.getEvolutionRateMultiplier();
        }
        this.evolutionPointsInternal += points * phaseMult * difficultyMult;
    }

    /**
     * Called when the parasite kills an entity.
     * Awards evolution points based on the victim's max health ("consuming flesh").
     *
     * @param victimMaxHealth the max health of the killed entity
     */
    public void onKillEntity(float victimMaxHealth) {
        if (!canEvolve) return;
        // Award evolution points proportional to victim's max health
        // This implements "consuming flesh" as an evolution point source
        double killPoints = ModConfigSystems.getKillEvolutionRate() * (victimMaxHealth / 20.0);
        addEvolutionPoints(killPoints);
    }

    public void onKillCountChanged(double killCount) {
        if (!canEvolve) return;
        addEvolutionPoints(ModConfigSystems.getKillEvolutionRate());
    }

    protected void attemptEvolution() {
        if (!canEvolve) return;
        if (!ModConfigSystems.isEvolutionEnabled()) return;

        EvoPhase currentPhase = EntityParasiteBase.getCurrentPhase(parasite.level());
        if (!currentPhase.isAtLeast(EvoPhase.PHASE_1)) return;

        // Deduct evolution points and increment level
        evolutionPointsInternal -= evolutionThreshold;
        evolutionLevel++;
        evolutionThreshold *= THRESHOLD_GROWTH_RATE;

        grantGeneOnEvolution();
        applyEvolutionBonuses();

        if (parasite.getCombatComponent() != null) {
            parasite.getCombatComponent().resetAdaptation();
        }

        // Attempt tier transformation: if this entity has a next evolution path,
        // spawn the next-tier entity and remove the current one
        if (!tryEvolutionTransformation()) {
            // If no transformation occurred (e.g., already at max tier or no mapping),
            // just apply stat bonuses to the current entity
            parasite.applyBonuses();
        }
    }

    /**
     * Attempts to transform the current parasite into the next evolution tier.
     * <p>
     * When a parasite accumulates enough evolution points, it transforms into
     * the next tier entity along its {@link EvolutionPath}. The new entity
     * inherits the old entity's position, rotation, health proportion, name,
     * equipment, gene data, and other persistent state.
     *
     * @return true if the transformation was performed (old entity removed, new spawned),
     *         false if no transformation was possible
     */
    protected boolean tryEvolutionTransformation() {
        EvolutionPath currentPath = parasite.getEvolutionPath();
        if (currentPath == null) return false;

        EvolutionPath nextPath = currentPath.getNextPath();
        if (nextPath == null) return false; // Already at max tier

        if (!(parasite.level() instanceof ServerLevel serverLevel)) return false;

        // Determine the entity type to evolve into
        EntityType<?> nextType = getNextEntityType(nextPath);
        if (nextType == null) {
            if (ModConfigSystems.isLoggingEnabled()) {
                SubspaceParasite.LOGGER.debug("Evolution: No entity type mapping for path {} -> {}",
                        currentPath, nextPath);
            }
            return false;
        }

        // Create the new entity
        net.minecraft.world.entity.Entity newEntity = nextType.create(serverLevel);
        if (!(newEntity instanceof EntityParasiteBase newParasite)) {
            if (newEntity != null) {
                newEntity.discard();
            }
            if (ModConfigSystems.isLoggingEnabled()) {
                SubspaceParasite.LOGGER.warn("Evolution: Created entity {} is not EntityParasiteBase",
                        nextType);
            }
            return false;
        }

        // ── Transfer position and rotation ──
        newParasite.moveTo(parasite.getX(), parasite.getY(), parasite.getZ(),
                parasite.getYRot(), parasite.getXRot());
        newParasite.setYHeadRot(parasite.getYHeadRot());

        // ── Transfer health proportion ──
        float healthRatio = parasite.getHealth() / Math.max(1.0F, parasite.getMaxHealth());
        float newHealth = newParasite.getMaxHealth() * healthRatio;
        newParasite.setHealth(newHealth);

        // ── Transfer name ──
        if (parasite.hasCustomName()) {
            newParasite.setCustomName(parasite.getCustomName());
            newParasite.setCustomNameVisible(parasite.isCustomNameVisible());
        }

        // ── Transfer equipment ──
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = parasite.getItemBySlot(slot);
            if (!stack.isEmpty()) {
                newParasite.setItemSlot(slot, stack.copy());
            }
        }

        // ── Transfer evolution path ──
        newParasite.setEvolutionPath(nextPath);

        // ── Transfer phase created ──
        newParasite.setPhaseCreated(parasite.getPhaseCreated());

        // ── Transfer evolution data (remaining points carry over) ──
        if (newParasite.evolutionComponent != null) {
            newParasite.evolutionComponent.evolutionPointsInternal = this.evolutionPointsInternal;
            newParasite.evolutionComponent.evolutionLevel = this.evolutionLevel;
            newParasite.evolutionComponent.evolutionThreshold = this.evolutionThreshold;
            newParasite.evolutionComponent.canEvolve = this.canEvolve;
        }

        // ── Transfer gene data ──
        for (int i = 0; i < Math.min(parasite.geneBooleans.length, newParasite.geneBooleans.length); i++) {
            newParasite.geneBooleans[i] = parasite.geneBooleans[i];
        }
        for (int i = 0; i < Math.min(parasite.geneFloats.length, newParasite.geneFloats.length); i++) {
            newParasite.geneFloats[i] = parasite.geneFloats[i];
        }

        // ── Transfer ownership and colony state ──
        if (parasite.owner != null) {
            newParasite.setOwner(parasite.owner);
        } else if (parasite.ownerUUID != null) {
            newParasite.ownerUUID = parasite.ownerUUID;
        }
        newParasite.setColonySpawned(parasite.colonySpawned);
        newParasite.setCanDespawn(parasite.canDespawn);

        // ── Transfer leader state ──
        newParasite.isLeaderFlag = parasite.isLeaderFlag;
        if (parasite.leader != null) {
            newParasite.leader = parasite.leader;
        }

        // ── Transfer kill count ──
        newParasite.setKillCount(parasite.getKillCount());

        // ── Apply bonuses to the new entity ──
        newParasite.applyBonuses();
        newParasite.applyGeneModifications();

        // ── Spawn the new entity and remove the old one ──
        if (serverLevel.addFreshEntity(newParasite)) {
            if (ModConfigSystems.shouldLogEvolution()) {
                SubspaceParasite.LOGGER.info("Evolution: {} (path={}) evolved to {} (path={}) at [{}, {}, {}]",
                        parasite.getName().getString(), currentPath,
                        newParasite.getName().getString(), nextPath,
                        String.format("%.1f", parasite.getX()),
                        String.format("%.1f", parasite.getY()),
                        String.format("%.1f", parasite.getZ()));
            }
            parasite.discard();
            return true;
        } else {
            // Spawning failed — clean up and fall back
            newParasite.discard();
            if (ModConfigSystems.isLoggingEnabled()) {
                SubspaceParasite.LOGGER.warn("Evolution: Failed to spawn evolved entity {} at [{}, {}, {}]",
                        nextType,
                        String.format("%.1f", parasite.getX()),
                        String.format("%.1f", parasite.getY()),
                        String.format("%.1f", parasite.getZ()));
            }
            return false;
        }
    }

    /**
     * Gets the entity type that this parasite should evolve into for the given next path.
     * Uses the current parasite's {@code getNextEvolutionEntityType()} override first,
     * then falls back to the COTHMapping evolution path defaults.
     *
     * @param nextPath the target evolution path
     * @return the EntityType to evolve into, or null if no mapping exists
     */
    @Nullable
    protected EntityType<?> getNextEntityType(EvolutionPath nextPath) {
        // Allow entity subclass to provide species-specific evolution type
        EntityType<?> override = parasite.getNextEvolutionEntityType(nextPath);
        if (override != null) return override;

        // Fall back to the default path → entity type mapping
        return COTHMapping.getEntityTypeForEvolutionPath(nextPath);
    }

    protected void grantGeneOnEvolution() {
        if (!ModConfigSystems.isGeneGainEnabled()) return;

        float chance = (float) ModConfigSystems.getGeneGainChance();

        for (GeneType gene : GeneType.values()) {
            if (gene.isBoolean() && !parasite.getGeneBoolean(gene.getIndex())) {
                if (parasite.getRandom().nextFloat() < chance) {
                    parasite.activateGene(gene);
                    return;
                }
            }
        }

        // If all booleans active, boost a random float gene
        GeneType[] floatGenes = { GeneType.POISON_HEALING, GeneType.MOB_HEALING,
                GeneType.ATTACK_SPEED, GeneType.REGEN_RATE, GeneType.ANTI_KNOCKBACK,
                GeneType.LEAP_POWER, GeneType.PROJECTILE_SPEED, GeneType.INFECTIOUSNESS,
                GeneType.ADAPTATION_SPEED, GeneType.SPREAD_RANGE };
        GeneType chosen = floatGenes[parasite.getRandom().nextInt(floatGenes.length)];
        parasite.setGeneFloat(chosen.getIndex(),
                parasite.getGeneFloat(chosen.getIndex()) + parasite.getRandom().nextFloat() * 0.3F);
    }

    protected void applyEvolutionBonuses() {
        // Tracked via evolutionLevel for external query
    }

    protected void checkGeneMutation() {
        if (!ModConfigSystems.isGeneMutationEnabled()) return;

        float mutationChance = (float) ModConfigSystems.getGeneMutationChance();
        if (parasite.getRandom().nextFloat() < mutationChance) {
            int boolCount = GeneType.booleanGeneCount();
            int geneIdx = parasite.getRandom().nextInt(boolCount);

            if (!parasite.getGeneBoolean(geneIdx)) {
                parasite.setGeneBoolean(geneIdx, true);
                if (geneIdx < GeneType.floatGeneCount()) {
                    parasite.setGeneFloat(geneIdx, parasite.getRandom().nextFloat() * 0.3F);
                }
                hasMutated = true;
            } else {
                if (geneIdx < GeneType.floatGeneCount()) {
                    float current = parasite.getGeneFloat(geneIdx);
                    parasite.setGeneFloat(geneIdx,
                            Math.min((float) ModConfigSystems.getGeneMaxFloatValue(),
                                    current + parasite.getRandom().nextFloat() * 0.15F));
                }
            }
            parasite.applyGeneModifications();
        }
    }

    public double getEvolutionPointsInternal() { return evolutionPointsInternal; }
    public double getEvolutionThreshold() { return evolutionThreshold; }
    public int getEvolutionLevel() { return evolutionLevel; }
    public void setEvolutionLevel(int level) { this.evolutionLevel = level; }
    public boolean canEvolve() { return canEvolve; }
    public void setCanEvolve(boolean value) { this.canEvolve = value; }
    public boolean hasMutated() { return hasMutated; }
    public void setAccumulationInterval(int ticks) { this.accumulationInterval = ticks; }
    public void setMutationCheckInterval(int ticks) { this.mutationCheckInterval = ticks; }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("EvolutionPoints", evolutionPointsInternal);
        tag.putDouble("EvolutionThreshold", evolutionThreshold);
        tag.putInt("AccumulationTimer", accumulationTimer);
        tag.putInt("AccumulationInterval", accumulationInterval);
        tag.putInt("MutationTimer", mutationTimer);
        tag.putInt("MutationCheckInterval", mutationCheckInterval);
        tag.putInt("EvolutionLevel", evolutionLevel);
        tag.putBoolean("CanEvolve", canEvolve);
        tag.putBoolean("HasMutated", hasMutated);
        return tag;
    }

    public void load(CompoundTag tag) {
        evolutionPointsInternal = tag.getDouble("EvolutionPoints");
        evolutionThreshold = tag.getDouble("EvolutionThreshold");
        accumulationTimer = tag.getInt("AccumulationTimer");
        accumulationInterval = tag.getInt("AccumulationInterval");
        mutationTimer = tag.getInt("MutationTimer");
        mutationCheckInterval = tag.getInt("MutationCheckInterval");
        evolutionLevel = tag.getInt("EvolutionLevel");
        canEvolve = tag.getBoolean("CanEvolve");
        hasMutated = tag.getBoolean("HasMutated");
    }
}
