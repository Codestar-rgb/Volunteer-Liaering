package com.subspaceparasite.api.entity;

/**
 * 寄生虫类型枚举（用于分类）
 * 
 * @author SubspaceParasite Team
 * @version 1.20.1
 */
public enum ParasiteType {
    // ==================== 感染型 (Infected) ====================
    INF_HUMAN,
    INF_COW,
    INF_SHEEP,
    INF_PIG,
    INF_CHICKEN,
    INF_CREEPER,
    INF_SKELETON,
    INF_ZOMBIE,
    INF_SPIDER,
    INF_ENDERMAN,
    INF_VILLAGER,
    INF_WOLF,
    INF_HORSE,
    
    // ==================== 原始型 (Primitive) ====================
    BANO,
    CANRA,
    EMANA,
    GIM,
    HULL,
    IKI,
    LUM,
    NOGLA,
    RANRAC,
    SHYCO,
    WYMO,
    ZAA,
    
    // ==================== 适应型 (Adapted) ====================
    ADAPTED_ARACHNID,
    ADAPTED_BEAST,
    ADAPTED_FLYER,
    
    // ==================== 衍生型 (Derived) ====================
    CALLER,
    ASSIMILATED,
    OVERSEER,
    
    // ==================== 粗劣型 (Crude) ====================
    CRUDE_SCORCHER,
    CRUDE_MINDIM,
    CRUDE_EGAS,
    
    // ==================== 野生型 (Feral) ====================
    FERAL_CREEPER,
    FERAL_SKELETON,
    FERAL_ZOMBIE,
    
    // ==================== 劫持型 (Hijacked) ====================
    HIJACKED_CREEPER,
    HIJACKED_SKELETON,
    HIJACKED_ZOMBIE,
    
    // ==================== 天生型 (Inborn) ====================
    INBORN_ALAFIN,
    INBORN_OBUS,
    INBORN_NORMAS,
    INBORN_CANAL,
    
    // ==================== 未知类型 ====================
    UNKNOWN;

    /**
     * 判断是否为感染型
     */
    public boolean isInfected() {
        return this.name().startsWith("INF_");
    }

    /**
     * 判断是否为原始型
     */
    public boolean isPrimitive() {
        return this == BANO || this == CANRA || this == EMANA || 
               this == GIM || this == HULL || this == IKI || 
               this == LUM || this == NOGLA || this == RANRAC || 
               this == SHYCO || this == WYMO || this == ZAA;
    }

    /**
     * 判断是否为适应型
     */
    public boolean isAdapted() {
        return this.name().startsWith("ADAPTED_");
    }

    /**
     * 判断是否为衍生型
     */
    public boolean isDerived() {
        return this == CALLER || this == ASSIMILATED || this == OVERSEER;
    }

    /**
     * 判断是否为粗劣型
     */
    public boolean isCrude() {
        return this.name().startsWith("CRUDE_");
    }

    /**
     * 判断是否为野生型
     */
    public boolean isFeral() {
        return this.name().startsWith("FERAL_");
    }

    /**
     * 判断是否为劫持型
     */
    public boolean isHijacked() {
        return this.name().startsWith("HIJACKED_");
    }

    /**
     * 判断是否为天生型
     */
    public boolean isInborn() {
        return this.name().startsWith("INBORN_");
    }
}
