package com.subspaceparasite.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

/**
 * 模组配置系统
 * 
 * 管理所有寄生虫相关的配置选项
 * 
 * @author SubspaceParasite Team
 * @version 1.20.1
 */
@Mod.EventBusSubscriber(modid = "subspaceparasite", bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModConfig {
    
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    
    // ==================== 进化相关配置 ====================
    
    public static final ForgeConfigSpec.BooleanValue EVOLUTION_ENABLED;
    public static final ForgeConfigSpec.DoubleValue NATURAL_EVOLUTION_RATE;
    public static final ForgeConfigSpec.DoubleValue KILL_EVOLUTION_RATE;
    public static final ForgeConfigSpec.BooleanValue GENE_MUTATION_ENABLED;
    public static final ForgeConfigSpec.DoubleValue GENE_MUTATION_CHANCE;
    public static final ForgeConfigSpec.BooleanValue GENE_GAIN_ENABLED;
    public static final ForgeConfigSpec.DoubleValue GENE_GAIN_CHANCE;
    
    // ==================== 感染相关配置 ====================
    
    public static final ForgeConfigSpec.DoubleValue BASE_INFECTION_CHANCE;
    public static final ForgeConfigSpec.DoubleValue INFECTION_RADIUS_MULTIPLIER;
    public static final ForgeConfigSpec.IntValue MAX_INFECTED_PER_DEATH;
    
    // ==================== 世界生成配置 ====================
    
    public static final ForgeConfigSpec.BooleanValue WORLD_GENERATION_ENABLED;
    public static final ForgeConfigSpec.DoubleValue PARASITE_SPAWN_RATE;
    public static final ForgeConfigSpec.IntValue INITIAL_EVO_PHASE;
    
    // ==================== 性能配置 ====================
    
    public static final ForgeConfigSpec.IntValue MAX_PARASITES_PER_CHUNK;
    public static final ForgeConfigSpec.BooleanValue ENABLE_OPTIMIZATIONS;
    
    // ==================== 客户端配置 ====================
    
    public static final ForgeConfigSpec.BooleanValue ENABLE_PARTICLES;
    public static final ForgeConfigSpec.BooleanValue ENABLE_SOUNDS;
    public static final ForgeConfigSpec.DoubleValue PARTICLE_DENSITY;
    
    static {
        BUILDER.push("evolution");
        
        EVOLUTION_ENABLED = BUILDER
            .comment("是否启用寄生虫进化系统")
            .define("evolutionEnabled", true);
        
        NATURAL_EVOLUTION_RATE = BUILDER
            .comment("自然进化点数获取速率 (每 tick)")
            .defineInRange("naturalEvolutionRate", 0.01, 0.0, 1.0);
        
        KILL_EVOLUTION_RATE = BUILDER
            .comment("击杀获得进化点数")
            .defineInRange("killEvolutionRate", 5.0, 0.0, 100.0);
        
        GENE_MUTATION_ENABLED = BUILDER
            .comment("是否启用基因突变系统")
            .define("geneMutationEnabled", true);
        
        GENE_MUTATION_CHANCE = BUILDER
            .comment("基因突变概率 (每次检查)")
            .defineInRange("geneMutationChance", 0.05, 0.0, 1.0);
        
        GENE_GAIN_ENABLED = BUILDER
            .comment("是否启用进化时基因获取")
            .define("geneGainEnabled", true);
        
        GENE_GAIN_CHANCE = BUILDER
            .comment("进化时获得新基因的概率")
            .defineInRange("geneGainChance", 0.3, 0.0, 1.0);
        
        BUILDER.pop();
        
        BUILDER.push("infection");
        
        BASE_INFECTION_CHANCE = BUILDER
            .comment("基础感染概率")
            .defineInRange("baseInfectionChance", 0.5, 0.0, 1.0);
        
        INFECTION_RADIUS_MULTIPLIER = BUILDER
            .comment("感染半径乘数")
            .defineInRange("infectionRadiusMultiplier", 1.0, 0.5, 5.0);
        
        MAX_INFECTED_PER_DEATH = BUILDER
            .comment("死亡时最大感染生物数量")
            .defineInRange("maxInfectedPerDeath", 3, 0, 20);
        
        BUILDER.pop();
        
        BUILDER.push("world_generation");
        
        WORLD_GENERATION_ENABLED = BUILDER
            .comment("是否启用世界生成（殖民地、巢穴等）")
            .define("worldGenerationEnabled", true);
        
        PARASITE_SPAWN_RATE = BUILDER
            .comment("寄生虫自然生成速率乘数")
            .defineInRange("parasiteSpawnRate", 1.0, 0.0, 10.0);
        
        INITIAL_EVO_PHASE = BUILDER
            .comment("初始进化阶段 (0=Infected, 1=Primitive, 2=Adapted, 3=Derived)")
            .defineInRange("initialEvoPhase", 0, 0, 3);
        
        BUILDER.pop();
        
        BUILDER.push("performance");
        
        MAX_PARASITES_PER_CHUNK = BUILDER
            .comment("每个区块最大寄生虫数量")
            .defineInRange("maxParasitesPerChunk", 50, 10, 500);
        
        ENABLE_OPTIMIZATIONS = BUILDER
            .comment("是否启用性能优化")
            .define("enableOptimizations", true);
        
        BUILDER.pop();
        
        BUILDER.push("client");
        
        ENABLE_PARTICLES = BUILDER
            .comment("是否启用粒子效果")
            .define("enableParticles", true);
        
        ENABLE_SOUNDS = BUILDER
            .comment("是否启用自定义音效")
            .define("enableSounds", true);
        
        PARTICLE_DENSITY = BUILDER
            .comment("粒子密度乘数")
            .defineInRange("particleDensity", 1.0, 0.0, 5.0);
        
        BUILDER.pop();
    }
    
    public static final ForgeConfigSpec SPEC = BUILDER.build();
    
    // ==================== 配置值缓存 ====================
    
    private static boolean evolutionEnabledCache = true;
    private static double naturalEvolutionRateCache = 0.01;
    private static double killEvolutionRateCache = 5.0;
    
    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading event) {
        updateCache();
    }
    
    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading event) {
        updateCache();
    }
    
    private static void updateCache() {
        evolutionEnabledCache = EVOLUTION_ENABLED.get();
        naturalEvolutionRateCache = NATURAL_EVOLUTION_RATE.get();
        killEvolutionRateCache = KILL_EVOLUTION_RATE.get();
    }
    
    // ==================== 公共访问方法 ====================
    
    public static boolean isEvolutionEnabled() {
        return evolutionEnabledCache;
    }
    
    public static double getNaturalEvolutionRate() {
        return naturalEvolutionRateCache;
    }
    
    public static double getKillEvolutionRate() {
        return killEvolutionRateCache;
    }
    
    public static boolean isGeneMutationEnabled() {
        return GENE_MUTATION_ENABLED.get();
    }
    
    public static double getGeneMutationChance() {
        return GENE_MUTATION_CHANCE.get();
    }
    
    public static boolean isGeneGainEnabled() {
        return GENE_GAIN_ENABLED.get();
    }
    
    public static double getGeneGainChance() {
        return GENE_GAIN_CHANCE.get();
    }
    
    public static double getBaseInfectionChance() {
        return BASE_INFECTION_CHANCE.get();
    }
    
    public static double getInfectionRadiusMultiplier() {
        return INFECTION_RADIUS_MULTIPLIER.get();
    }
    
    public static int getMaxInfectedPerDeath() {
        return MAX_INFECTED_PER_DEATH.get();
    }
    
    public static boolean isWorldGenerationEnabled() {
        return WORLD_GENERATION_ENABLED.get();
    }
    
    public static double getParasiteSpawnRate() {
        return PARASITE_SPAWN_RATE.get();
    }
    
    public static int getInitialEvoPhase() {
        return INITIAL_EVO_PHASE.get();
    }
    
    public static int getMaxParasitesPerChunk() {
        return MAX_PARASITES_PER_CHUNK.get();
    }
    
    public static boolean areOptimizationsEnabled() {
        return ENABLE_OPTIMIZATIONS.get();
    }
    
    public static boolean areParticlesEnabled() {
        return ENABLE_PARTICLES.get();
    }
    
    public static boolean areSoundsEnabled() {
        return ENABLE_SOUNDS.get();
    }
    
    public static double getParticleDensity() {
        return PARTICLE_DENSITY.get();
    }
    
    /**
     * 获取当前世界的进化阶段
     * @return 进化阶段枚举
     */
    public static com.subspaceparasite.api.enums.EvoPhase getCurrentEvoPhase() {
        int phaseId = getInitialEvoPhase();
        return com.subspaceparasite.api.enums.EvoPhase.fromId(phaseId);
    }
}
