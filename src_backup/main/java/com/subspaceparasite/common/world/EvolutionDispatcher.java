package com.subspaceparasite.common.world;

import com.subspaceparasite.api.entity.IParasiteEntity;
import com.subspaceparasite.api.enums.EvoPhase;
import com.subspaceparasite.common.capability.component.EvolutionComponent;
import com.subspaceparasite.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 进化调度器系统
 * 
 * 负责管理所有寄生虫实体的进化逻辑，包括：
 * - 进化点数积累调度
 * - 进化条件检查
 * - 基因突变处理
 * - 殖民地范围内的进化增益
 * 
 * 采用单例模式，全局管理所有维度的进化逻辑
 * 
 * @author SubspaceParasite Team
 * @version 1.20.1
 */
public class EvolutionDispatcher {
    
    // ==================== 单例实例 ====================
    
    private static EvolutionDispatcher instance;
    
    public static EvolutionDispatcher getInstance() {
        if (instance == null) {
            instance = new EvolutionDispatcher();
        }
        return instance;
    }
    
    // ==================== 成员变量 ====================
    
    /**
     * 已注册的寄生虫实体（按维度 ID 分组）
     */
    private final Map<Integer, Set<UUID>> registeredParasites;
    
    /**
     * 进化队列（待处理进化的实体 UUID）
     */
    private final Map<Integer, Queue<UUID>> evolutionQueue;
    
    /**
     * 殖民地中心位置缓存（用于进化增益计算）
     */
    private final Map<Integer, List<ColonyData>> colonyCenters;
    
    /**
     * Tick 计数器
     */
    private int tickCounter;
    
    /**
     * 进化检查间隔（ticks）
     */
    private static final int EVOLUTION_CHECK_INTERVAL = 20; // 1 秒
    
    /**
     * 最大同时处理进化数量
     */
    private static final int MAX_CONCURRENT_EVOLUTIONS = 5;
    
    // ==================== 内部类 ====================
    
    /**
     * 殖民地数据
     */
    public static class ColonyData {
        public final Vec3 center;
        public final double radius;
        public final EvoPhase dominantPhase;
        public final int parasiteCount;
        
        public ColonyData(Vec3 center, double radius, EvoPhase dominantPhase, int parasiteCount) {
            this.center = center;
            this.radius = radius;
            this.dominantPhase = dominantPhase;
            this.parasiteCount = parasiteCount;
        }
        
        /**
         * 检查位置是否在殖民地范围内
         */
        public boolean isInColony(Vec3 position) {
            return position.distanceTo(center) <= radius;
        }
        
        /**
         * 获取殖民地内的进化增益倍率
         */
        public double getEvolutionBonus() {
            // 基于殖民地内寄生虫数量和主导阶段计算增益
            double baseBonus = 1.0 + (parasiteCount * 0.01); // 每只寄生虫 +1%
            double phaseBonus = dominantPhase.getId() * 0.1; // 每个阶段 +10%
            return Math.min(baseBonus + phaseBonus, 3.0); // 最大 3 倍
        }
    }
    
    // ==================== 构造函数 ====================
    
    private EvolutionDispatcher() {
        this.registeredParasites = new ConcurrentHashMap<>();
        this.evolutionQueue = new ConcurrentHashMap<>();
        this.colonyCenters = new ConcurrentHashMap<>();
        this.tickCounter = 0;
    }
    
    // ==================== 注册/注销方法 ====================
    
    /**
     * 注册寄生虫实体到调度器
     */
    public void registerParasite(LivingEntity entity) {
        if (!(entity instanceof IParasiteEntity)) return;
        
        int dimensionId = entity.level().dimension().location().toString().hashCode();
        UUID entityId = entity.getUUID();
        
        registeredParasites.computeIfAbsent(dimensionId, k -> ConcurrentHashMap.newKeySet())
                          .add(entityId);
    }
    
    /**
     * 注销寄生虫实体
     */
    public void unregisterParasite(LivingEntity entity) {
        int dimensionId = entity.level().dimension().location().toString().hashCode();
        UUID entityId = entity.getUUID();
        
        Set<UUID> dimensionParasites = registeredParasites.get(dimensionId);
        if (dimensionParasites != null) {
            dimensionParasites.remove(entityId);
        }
        
        // 同时从进化队列移除
        Queue<UUID> queue = evolutionQueue.get(dimensionId);
        if (queue != null) {
            queue.remove(entityId);
        }
    }
    
    // ==================== 殖民地管理 ====================
    
    /**
     * 注册殖民地中心
     */
    public void registerColony(int dimensionId, Vec3 center, double radius, EvoPhase dominantPhase, int parasiteCount) {
        ColonyData data = new ColonyData(center, radius, dominantPhase, parasiteCount);
        
        colonyCenters.computeIfAbsent(dimensionId, k -> new ArrayList<>())
                    .add(data);
    }
    
    /**
     * 清除指定位置的殖民地
     */
    public void removeColony(int dimensionId, Vec3 position) {
        List<ColonyData> colonies = colonyCenters.get(dimensionId);
        if (colonies != null) {
            colonies.removeIf(colony -> colony.center.distanceTo(position) < 5.0);
        }
    }
    
    /**
     * 获取位置的殖民地进化增益
     */
    public double getEvolutionBonusAt(Level level, BlockPos pos) {
        int dimensionId = level.dimension().location().toString().hashCode();
        Vec3 position = new Vec3(pos.getX(), pos.getY(), pos.getZ());
        
        List<ColonyData> colonies = colonyCenters.get(dimensionId);
        if (colonies == null || colonies.isEmpty()) {
            return 1.0; // 无殖民地，无增益
        }
        
        double maxBonus = 1.0;
        for (ColonyData colony : colonies) {
            if (colony.isInColony(position)) {
                maxBonus = Math.max(maxBonus, colony.getEvolutionBonus());
            }
        }
        
        return maxBonus;
    }
    
    // ==================== Tick 调度 ====================
    
    /**
     * 执行 Tick（由服务器主循环调用）
     */
    public void tick(ServerLevel level) {
        if (!ModConfig.isEvolutionEnabled()) return;
        
        tickCounter++;
        int dimensionId = level.dimension().location().toString().hashCode();
        
        // 每隔一定间隔检查进化条件
        if (tickCounter % EVOLUTION_CHECK_INTERVAL == 0) {
            processEvolutionQueue(level, dimensionId);
        }
        
        // 更新殖民地数据（较低频率）
        if (tickCounter % 100 == 0) {
            updateColonyData(level, dimensionId);
        }
    }
    
    /**
     * 处理进化队列
     */
    private void processEvolutionQueue(ServerLevel level, int dimensionId) {
        Queue<UUID> queue = evolutionQueue.computeIfAbsent(dimensionId, k -> new LinkedList<>());
        
        int processed = 0;
        while (!queue.isEmpty() && processed < MAX_CONCURRENT_EVOLUTIONS) {
            UUID entityId = queue.poll();
            LivingEntity entity = (LivingEntity) level.getEntity(entityId);
            
            if (entity != null && entity instanceof IParasiteEntity) {
                attemptEvolution(entity);
            }
            
            processed++;
        }
    }
    
    /**
     * 尝试让实体进化
     */
    public void attemptEvolution(LivingEntity entity) {
        if (!(entity instanceof IParasiteEntity parasite)) return;
        if (entity.level().isClientSide) return;
        
        // 获取进化组件
        // TODO: 使用 Forge Capability 系统获取组件
        // 目前直接检查接口方法
        
        if (parasite.canEvolve()) {
            // 应用殖民地增益
            double bonus = getEvolutionBonusAt(
                entity.level(), 
                entity.blockPosition()
            );
            
            // 执行进化
            parasite.evolve();
            
            // 记录日志
            com.subspaceparasite.SubspaceParasite.LOGGER.info(
                "Entity {} evolved with bonus x{}", 
                entity.getName().getString(), 
                bonus
            );
        }
    }
    
    /**
     * 将实体加入进化队列
     */
    public void addToEvolutionQueue(LivingEntity entity) {
        int dimensionId = entity.level().dimension().location().toString().hashCode();
        evolutionQueue.computeIfAbsent(dimensionId, k -> new LinkedList<>())
                     .offer(entity.getUUID());
    }
    
    // ==================== 殖民地数据更新 ====================
    
    /**
     * 更新殖民地数据
     */
    private void updateColonyData(ServerLevel level, int dimensionId) {
        // 扫描所有寄生虫实体，重新计算殖民地中心
        List<IParasiteEntity> parasites = new ArrayList<>();
        
        for (UUID entityId : registeredParasites.getOrDefault(dimensionId, Collections.emptySet())) {
            LivingEntity entity = (LivingEntity) level.getEntity(entityId);
            if (entity instanceof IParasiteEntity) {
                parasites.add((IParasiteEntity) entity);
            }
        }
        
        if (parasites.isEmpty()) {
            colonyCenters.remove(dimensionId);
            return;
        }
        
        // 简单的聚类算法：找到寄生虫密集区域作为殖民地中心
        Map<BlockPos, Integer> densityMap = new HashMap<>();
        for (IParasiteEntity parasite : parasites) {
            if (parasite instanceof LivingEntity living) {
                BlockPos pos = living.blockPosition();
                densityMap.merge(pos, 1, Integer::sum);
            }
        }
        
        // 找到密度最高的位置作为殖民地中心
        BlockPos centerPos = null;
        int maxDensity = 0;
        for (Map.Entry<BlockPos, Integer> entry : densityMap.entrySet()) {
            if (entry.getValue() > maxDensity) {
                maxDensity = entry.getValue();
                centerPos = entry.getKey();
            }
        }
        
        if (centerPos != null && maxDensity >= 5) {
            // 确定主导阶段
            EvoPhase dominantPhase = getDominantPhase(parasites);
            
            // 计算殖民地半径（基于寄生虫数量）
            double radius = 20.0 + (maxDensity * 2.0);
            
            // 注册殖民地
            Vec3 center = new Vec3(centerPos.getX() + 0.5, centerPos.getY(), centerPos.getZ() + 0.5);
            registerColony(dimensionId, center, radius, dominantPhase, maxDensity);
        }
    }
    
    /**
     * 获取寄生虫群体中的主导进化阶段
     */
    private EvoPhase getDominantPhase(List<IParasiteEntity> parasites) {
        Map<EvoPhase, Integer> phaseCounts = new HashMap<>();
        
        for (IParasiteEntity parasite : parasites) {
            EvoPhase phase = parasite.getPhase();
            phaseCounts.merge(phase, 1, Integer::sum);
        }
        
        EvoPhase dominant = EvoPhase.INFECTED;
        int maxCount = 0;
        
        for (Map.Entry<EvoPhase, Integer> entry : phaseCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                dominant = entry.getKey();
            }
        }
        
        return dominant;
    }
    
    // ==================== 清理方法 ====================
    
    /**
     * 清理指定维度的所有数据
     */
    public void cleanupDimension(int dimensionId) {
        registeredParasites.remove(dimensionId);
        evolutionQueue.remove(dimensionId);
        colonyCenters.remove(dimensionId);
    }
    
    /**
     * 重置调度器（用于世界重载）
     */
    public void reset() {
        registeredParasites.clear();
        evolutionQueue.clear();
        colonyCenters.clear();
        tickCounter = 0;
    }
}
