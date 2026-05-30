package com.subspaceparasite.api.enums;

/**
 * 寄生虫进化阶段枚举
 * 
 * 对应 SRP 原版中的进化链：
 * INFECTED -> PRIMITIVE -> ADAPTED -> DERIVED
 * 
 * @author SubspaceParasite Team
 * @version 1.20.1
 */
public enum EvoPhase {
    /**
     * 感染阶段 - 被寄生虫感染的原版生物
     */
    INFECTED(0, "infected"),
    
    /**
     * 原始阶段 - 基础寄生虫形态
     */
    PRIMITIVE(1, "primitive"),
    
    /**
     * 适应阶段 - 经过进化的寄生虫
     */
    ADAPTED(2, "adapted"),
    
    /**
     * 衍生阶段 - 高级特化寄生虫
     */
    DERIVED(3, "derived");

    private final int id;
    private final String name;

    EvoPhase(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /**
     * 判断是否可以进化到下一阶段
     */
    public boolean canEvolve() {
        return this != DERIVED;
    }

    /**
     * 获取下一阶段
     */
    public EvoPhase nextPhase() {
        if (this == DERIVED) {
            return DERIVED;
        }
        return values()[this.ordinal() + 1];
    }

    /**
     * 从 ID 获取进化阶段
     */
    public static EvoPhase fromId(int id) {
        for (EvoPhase phase : values()) {
            if (phase.id == id) {
                return phase;
            }
        }
        return INFECTED;
    }

    /**
     * 从名称获取进化阶段
     */
    public static EvoPhase fromName(String name) {
        for (EvoPhase phase : values()) {
            if (phase.name.equalsIgnoreCase(name)) {
                return phase;
            }
        }
        return INFECTED;
    }
}
