package com.subspaceparasite.api.capability.component;

import com.subspaceparasite.api.enums.EvoPhase;
import com.subspaceparasite.api.entity.ParasiteType;
import com.subspaceparasite.api.enums.GeneType;
import net.minecraft.nbt.CompoundTag;

/**
 * 进化组件接口
 * 
 * 管理寄生虫的进化逻辑：进化点数积累、基因突变、进化阈值检查等
 * 
 * @author SubspaceParasite Team
 * @version 1.20.1
 */
public interface IEvolutionComponent {
    
    /**
     * 初始化进化组件
     * @param phase 初始进化阶段
     * @param type 寄生虫类型
     * @param geneType 基因类型
     */
    void initialize(EvoPhase phase, ParasiteType type, GeneType geneType);
    
    /**
     * 获取当前进化阶段
     * @return 进化阶段
     */
    EvoPhase getCurrentPhase();
    
    /**
     * 设置当前进化阶段
     * @param phase 新的进化阶段
     */
    void setCurrentPhase(EvoPhase phase);
    
    /**
     * 获取进化点数
     * @return 当前进化点数
     */
    double getEvolutionPoints();
    
    /**
     * 增加进化点数
     * @param points 增加的点数
     */
    void addEvolutionPoints(double points);
    
    /**
     * 获取进化阈值
     * @return 当前进化所需阈值
     */
    double getEvolutionThreshold();
    
    /**
     * 获取进化等级
     * @return 进化等级
     */
    int getEvolutionLevel();
    
    /**
     * 判断是否可以进化
     * @return true 如果可以进化
     */
    boolean canEvolve();
    
    /**
     * 设置是否可以进化
     * @param value 是否可以进化
     */
    void setCanEvolve(boolean value);
    
    /**
     * 尝试进化
     * @return true 如果进化成功
     */
    boolean attemptEvolution();
    
    /**
     * 在击杀时调用（增加进化点数）
     * @param killCount 击杀数
     */
    void onKillCountChanged(double killCount);
    
    /**
     * 检查基因突变
     */
    void checkGeneMutation();
    
    /**
     * 获取是否已突变
     * @return true 如果已突变
     */
    boolean hasMutated();
    
    /**
     * 设置积累间隔（ticks）
     * @param ticks 积累间隔
     */
    void setAccumulationInterval(int ticks);
    
    /**
     * 设置突变检查间隔（ticks）
     * @param ticks 突变检查间隔
     */
    void setMutationCheckInterval(int ticks);
    
    /**
     * 保存数据到 NBT
     * @param tag NBT 标签
     */
    void saveNBT(CompoundTag tag);
    
    /**
     * 从 NBT 加载数据
     * @param tag NBT 标签
     */
    void loadNBT(CompoundTag tag);
    
    /**
     * 组件 Tick
     */
    void tick();
}
