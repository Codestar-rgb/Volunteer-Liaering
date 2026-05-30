package com.subspaceparasite.common.entity.base;

import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.api.parasite.GeneType;
import com.subspaceparasite.config.ModConfigSystems;

import net.minecraft.nbt.CompoundTag;

/**
 * Component handling evolution logic for parasite entities.
 * Manages natural evolution point accumulation, gene mutation,
 * evolution threshold checking, and gene gain on evolution.
 */
public class EvolutionComponent {

    private final EntityParasiteBase parasite;

    private double evolutionPointsInternal;
    private double evolutionThreshold;
    private int accumulationTimer;
    private int accumulationInterval;
    private int mutationTimer;
    private int mutationCheckInterval;
    private int evolutionLevel;
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
        this.evolutionPointsInternal += points * phaseMult;
        // Sync the host's evolutionPoints field with internal tracking
        parasite.evolutionPoints = (int) this.evolutionPointsInternal;
    }

    public void onKillCountChanged(double killCount) {
        if (!canEvolve) return;
        addEvolutionPoints(ModConfigSystems.getKillEvolutionRate());
    }

    protected void attemptEvolution() {
        if (!canEvolve) return;
        if (!ModConfigSystems.isEvolutionEnabled()) return;

        EvoPhase currentPhase = EntityParasiteBase.getCurrentPhase(parasite.level());
        if (!currentPhase.isAtLeast(EvoPhase.ONE)) return;

        evolutionPointsInternal -= evolutionThreshold;
        evolutionLevel++;
        evolutionThreshold *= THRESHOLD_GROWTH_RATE;

        grantGeneOnEvolution();
        applyEvolutionBonuses();

        if (parasite.getCombatComponent() != null) {
            parasite.getCombatComponent().resetAdaptation();
        }

        parasite.applyBonuses();
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
                GeneType.LEAP_POWER, GeneType.PROJECTILE_SPEED, GeneType.INFECTIOUSNESS };
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
