package com.subspaceparasite.api.entity;

import com.subspaceparasite.api.enums.EvoPhase;
import com.subspaceparasite.api.enums.GeneType;

/**
 * 寄生虫实体接口
 * 
 * 所有寄生虫实体必须实现此接口以提供统一的寄生虫特性访问
 * 
 * @author SubspaceParasite Team
 * @version 1.20.1
 */
public interface IParasiteEntity {

    /**
     * 判断是否为寄生虫实体
     * @return true 如果是寄生虫
     */
    boolean isParasite();

    /**
     * 获取当前进化阶段
     * @return 进化阶段枚举
     */
    EvoPhase getPhase();

    /**
     * 设置进化阶段
     * @param phase 新的进化阶段
     */
    void setPhase(EvoPhase phase);

    /**
     * 获取寄生虫类型（基因类型）
     * @return 基因类型枚举
     */
    GeneType getParasiteType();

    /**
     * 设置寄生虫类型
     * @param type 新的寄生虫类型
     */
    void setParasiteType(GeneType type);

    /**
     * 判断是否可以被感染
     * @return true 如果可以被感染
     */
    boolean canBeInfected();

    /**
     * 判断是否应该掉落战利品
     * @return true 如果应该掉落
     */
    boolean shouldDropLoot();

    /**
     * 获取击杀数（用于进化判定）
     * @return 击杀计数
     */
    int getKillCount();

    /**
     * 增加击杀数
     * @param amount 增加的击杀数
     */
    void addKillCount(int amount);

    /**
     * 重置击杀数
     */
    void resetKillCount();

    /**
     * 检查是否可以进化到下一阶段
     * @return true 如果可以进化
     */
    boolean canEvolve();

    /**
     * 执行进化到下一阶段
     */
    void evolve();

    /**
     * 获取感染传播半径
     * @return 感染半径（方块单位）
     */
    float getInfectionRadius();

    /**
     * 获取死亡时最大感染生物数
     * @return 最大感染数量
     */
    int getMaxInfectOnDeath();
}
