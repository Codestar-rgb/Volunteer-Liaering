package com.subspaceparasite.common.capability.component;

import com.subspaceparasite.api.capability.component.IEvolutionComponent;
import com.subspaceparasite.api.entity.ParasiteType;
import com.subspaceparasite.api.enums.EvoPhase;
import com.subspaceparasite.api.enums.GeneType;
import com.subspaceparasite.config.ModConfig;
import net.minecraft.nbt.CompoundTag;

/**
 * 进化组件实现
 * 
 * 管理寄生虫的进化逻辑：进化点数积累、基因突变、进化阈值检查等
 * 
 * @author SubspaceParasite Team
 * @version 1.20.1
 */
public class EvolutionComponent implements IEvolutionComponent {
    
    // ==================== 能力标识 ====================
    
    public static final String COMPONENT_CAPABILITY = "subspaceparasite:evolution_component";
    
    // ==================== 成员变量 ====================
    
    private EvoPhase currentPhase;
    private ParasiteType parasiteType;
    private GeneType geneType;
    
    private double evolutionPoints;
    private double evolutionThreshold;
    private int evolutionLevel;
    
    private int accumulationTimer;
    private int accumulationInterval;
    
    private int mutationTimer;
    private int mutationCheckInterval;
    
    private boolean canEvolve;
    private boolean hasMutated;
    
    // ==================== 常量定义 ====================
    
    private static final int DEFAULT_ACCUMULATION_INTERVAL = 600; // 10 秒 (20 TPS)
    private static final int DEFAULT_MUTATION_INTERVAL = 2400; // 2 分钟
    private static final double BASE_EVOLUTION_THRESHOLD = 100.0;
    private static final double THRESHOLD_GROWTH_RATE = 1.5;
    
    // ==================== 构造函数 ====================
    
    public EvolutionComponent() {
        this.currentPhase = EvoPhase.INFECTED;
        this.parasiteType = ParasiteType.UNKNOWN;
        this.geneType = GeneType.INF_HUMAN;
        
        this.evolutionPoints = 0.0;
        this.evolutionThreshold = BASE_EVOLUTION_THRESHOLD;
        this.evolutionLevel = 0;
        
        this.accumulationTimer = 0;
        this.accumulationInterval = DEFAULT_ACCUMULATION_INTERVAL;
        
        this.mutationTimer = 0;
        this.mutationCheckInterval = DEFAULT_MUTATION_INTERVAL;
        
        this.canEvolve = true;
        this.hasMutated = false;
    }
    
    // ==================== IEvolutionComponent 实现 ====================
    
    @Override
    public void initialize(EvoPhase phase, ParasiteType type, GeneType geneType) {
        this.currentPhase = phase;
        this.parasiteType = type;
        this.geneType = geneType;
        this.evolutionPoints = 0.0;
        this.evolutionLevel = 0;
        this.evolutionThreshold = BASE_EVOLUTION_THRESHOLD;
    }
    
    @Override
    public EvoPhase getCurrentPhase() {
        return currentPhase;
    }
    
    @Override
    public void setCurrentPhase(EvoPhase phase) {
        this.currentPhase = phase;
    }
    
    @Override
    public double getEvolutionPoints() {
        return evolutionPoints;
    }
    
    @Override
    public void addEvolutionPoints(double points) {
        if (!canEvolve) return;
        
        // 根据当前阶段应用倍率
        float phaseMultiplier = 1.0f + currentPhase.getId() * 0.2f;
        this.evolutionPoints += points * phaseMultiplier;
    }
    
    @Override
    public double getEvolutionThreshold() {
        return evolutionThreshold;
    }
    
    @Override
    public int getEvolutionLevel() {
        return evolutionLevel;
    }
    
    @Override
    public boolean canEvolve() {
        return canEvolve && evolutionPoints >= evolutionThreshold && currentPhase.canEvolve();
    }
    
    @Override
    public void setCanEvolve(boolean value) {
        this.canEvolve = value;
    }
    
    @Override
    public boolean attemptEvolution() {
        if (!canEvolve()) return false;
        if (!ModConfig.EVOLUTION_ENABLED.get()) return false;
        
        // 检查世界是否允许进化
        EvoPhase worldPhase = ModConfig.getCurrentEvoPhase();
        if (!worldPhase.isAtLeast(currentPhase.nextPhase())) {
            return false;
        }
        
        // 执行进化
        evolutionPoints -= evolutionThreshold;
        evolutionLevel++;
        evolutionThreshold *= THRESHOLD_GROWTH_RATE;
        
        // 授予基因
        grantGeneOnEvolution();
        
        // 应用进化增益
        applyEvolutionBonuses();
        
        return true;
    }
    
    @Override
    public void onKillCountChanged(double killCount) {
        if (!canEvolve) return;
        addEvolutionPoints(ModConfig.KILL_EVOLUTION_RATE.get());
    }
    
    @Override
    public void checkGeneMutation() {
        if (!ModConfig.GENE_MUTATION_ENABLED.get()) return;
        
        float mutationChance = (float) ModConfig.GENE_MUTATION_CHANCE.get();
        java.util.Random random = new java.util.Random();
        
        if (random.nextFloat() < mutationChance) {
            // 简单实现：随机激活一个布尔基因或提升浮点基因
            hasMutated = true;
            // 具体基因操作需要宿主实体支持，此处预留接口
        }
    }
    
    @Override
    public boolean hasMutated() {
        return hasMutated;
    }
    
    @Override
    public void setAccumulationInterval(int ticks) {
        this.accumulationInterval = ticks;
    }
    
    @Override
    public void setMutationCheckInterval(int ticks) {
        this.mutationCheckInterval = ticks;
    }
    
    @Override
    public void tick() {
        if (!canEvolve) return;
        
        // 自然进化点数积累
        accumulationTimer++;
        if (accumulationTimer >= accumulationInterval) {
            accumulationTimer = 0;
            addEvolutionPoints(ModConfig.NATURAL_EVOLUTION_RATE.get());
        }
        
        // 基因突变检查
        mutationTimer++;
        if (mutationTimer >= mutationCheckInterval) {
            mutationTimer = 0;
            checkGeneMutation();
        }
        
        // 检查是否可以进化
        if (evolutionPoints >= evolutionThreshold) {
            attemptEvolution();
        }
    }
    
    // ==================== 内部方法 ====================
    
    /**
     * 进化时授予基因
     */
    protected void grantGeneOnEvolution() {
        if (!ModConfig.GENE_GAIN_ENABLED.get()) return;
        
        float chance = (float) ModConfig.GENE_GAIN_CHANCE.get();
        java.util.Random random = new java.util.Random();
        
        // 尝试激活一个未激活的布尔基因
        for (GeneType gene : GeneType.values()) {
            // 这里需要宿主实体的基因系统支持
            // 预留接口供未来实现
        }
    }
    
    /**
     * 应用进化增益
     */
    protected void applyEvolutionBonuses() {
        // 预留接口供未来实现
        // 可以在此处应用属性增益、效果增益等
    }
    
    // ==================== NBT 保存/加载 ====================
    
    @Override
    public void saveNBT(CompoundTag tag) {
        tag.putString("CurrentPhase", currentPhase.getName());
        tag.putString("ParasiteType", parasiteType.name());
        tag.putString("GeneType", geneType.getId());
        tag.putDouble("EvolutionPoints", evolutionPoints);
        tag.putDouble("EvolutionThreshold", evolutionThreshold);
        tag.putInt("EvolutionLevel", evolutionLevel);
        tag.putInt("AccumulationTimer", accumulationTimer);
        tag.putInt("AccumulationInterval", accumulationInterval);
        tag.putInt("MutationTimer", mutationTimer);
        tag.putInt("MutationCheckInterval", mutationCheckInterval);
        tag.putBoolean("CanEvolve", canEvolve);
        tag.putBoolean("HasMutated", hasMutated);
    }
    
    @Override
    public void loadNBT(CompoundTag tag) {
        if (tag.contains("CurrentPhase")) {
            this.currentPhase = EvoPhase.fromName(tag.getString("CurrentPhase"));
        }
        if (tag.contains("ParasiteType")) {
            this.parasiteType = ParasiteType.valueOf(tag.getString("ParasiteType"));
        }
        if (tag.contains("GeneType")) {
            this.geneType = GeneType.fromId(tag.getString("GeneType"));
        }
        if (tag.contains("EvolutionPoints")) {
            this.evolutionPoints = tag.getDouble("EvolutionPoints");
        }
        if (tag.contains("EvolutionThreshold")) {
            this.evolutionThreshold = tag.getDouble("EvolutionThreshold");
        }
        if (tag.contains("EvolutionLevel")) {
            this.evolutionLevel = tag.getInt("EvolutionLevel");
        }
        if (tag.contains("AccumulationTimer")) {
            this.accumulationTimer = tag.getInt("AccumulationTimer");
        }
        if (tag.contains("AccumulationInterval")) {
            this.accumulationInterval = tag.getInt("AccumulationInterval");
        }
        if (tag.contains("MutationTimer")) {
            this.mutationTimer = tag.getInt("MutationTimer");
        }
        if (tag.contains("MutationCheckInterval")) {
            this.mutationCheckInterval = tag.getInt("MutationCheckInterval");
        }
        if (tag.contains("CanEvolve")) {
            this.canEvolve = tag.getBoolean("CanEvolve");
        }
        if (tag.contains("HasMutated")) {
            this.hasMutated = tag.getBoolean("HasMutated");
        }
    }
}
