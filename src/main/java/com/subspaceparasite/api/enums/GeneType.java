package com.subspaceparasite.api.enums;

/**
 * 寄生虫基因类型枚举
 * 
 * 用于区分不同寄生虫种类的基因特征
 * 
 * @author SubspaceParasite Team
 * @version 1.20.1
 */
public enum GeneType {
    // ==================== 感染型基因 ====================
    INF_HUMAN("infected_human", "Infected Human"),
    INF_COW("infected_cow", "Infected Cow"),
    INF_SHEEP("infected_sheep", "Infected Sheep"),
    INF_PIG("infected_pig", "Infected Pig"),
    INF_CHICKEN("infected_chicken", "Infected Chicken"),
    
    // ==================== 原始型基因 ====================
    BANO("bano", "Bano"),
    CANRA("canra", "Canra"),
    EMANA("emana", "Emana"),
    GIM("gim", "Gim"),
    HULL("hull", "Hull"),
    IKI("iki", "Iki"),
    LUM("lum", "Lum"),
    NOGLA("nogla", "Nogla"),
    RANRAC("ranrac", "Ranrac"),
    SHYCO("shyco", "Shyco"),
    WYMO("wymo", "Wymo"),
    ZAA("zaa", "Zaa"),
    
    // ==================== 适应型基因 ====================
    ADAPTED_ARACHNID("adapted_arachnid", "Adapted Arachnid"),
    ADAPTED_BEAST("adapted_beast", "Adapted Beast"),
    ADAPTED_FLYER("adapted_flyer", "Adapted Flyer"),
    
    // ==================== 衍生型基因 ====================
    CALLER("caller", "Caller"),
    ASSIMILATED("assimilated", "Assimilated"),
    OVERSEER("overseer", "Overseer"),
    
    // ==================== 粗劣型基因 ====================
    CRUDE_SCORCHER("crude_scorcher", "Crude Scorcher"),
    CRUDE_MINDIM("crude_mindim", "Crude Mindim"),
    CRUDE_EGAS("crude_egas", "Crude Egas"),
    
    // ==================== 野生型基因 ====================
    FERAL_CREEPER("feral_creeper", "Feral Creeper"),
    FERAL_SKELETON("feral_skeleton", "Feral Skeleton"),
    FERAL_ZOMBIE("feral_zombie", "Feral Zombie"),
    
    // ==================== 劫持型基因 ====================
    HIJACKED_CREEPER("hijacked_creeper", "Hijacked Creeper"),
    HIJACKED_SKELETON("hijacked_skeleton", "Hijacked Skeleton"),
    HIJACKED_ZOMBIE("hijacked_zombie", "Hijacked Zombie"),
    
    // ==================== 天生型基因 ====================
    INBORN_ALAFIN("inborn_alafin", "Inborn Alafin"),
    INBORN_OBUS("inborn_obus", "Inborn Obus"),
    INBORN_NORMAS("inborn_normas", "Inborn Normas"),
    INBORN_CANAL("inborn_canal", "Inborn Canal");

    private final String id;
    private final String displayName;

    GeneType(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 从 ID 获取基因类型
     */
    public static GeneType fromId(String id) {
        for (GeneType type : values()) {
            if (type.id.equalsIgnoreCase(id)) {
                return type;
            }
        }
        return INF_HUMAN;
    }
}
