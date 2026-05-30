package com.subspaceparasite.common.capability.component;

import com.subspaceparasite.api.capability.component.IInfectionComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 感染组件实现
 * 
 * 管理寄生虫的感染传播逻辑：感染概率、感染范围、感染免疫等
 * 
 * @author SubspaceParasite Team
 * @version 1.20.1
 */
public class InfectionComponent implements IInfectionComponent {
    
    // ==================== 能力标识 ====================
    
    public static final String COMPONENT_CAPABILITY = "subspaceparasite:infection_component";
    
    // ==================== 成员变量 ====================
    
    private boolean canSpreadInfection;
    private float infectionChance;
    private float infectionRadius;
    private int maxInfectOnDeath;
    
    private final Set<UUID> immuneEntities;
    
    // ==================== 常量定义 ====================
    
    private static final float DEFAULT_INFECTION_RADIUS = 5.0f;
    private static final int DEFAULT_MAX_INFECT_ON_DEATH = 3;
    private static final float BASE_INFECTION_CHANCE = 0.5f;
    
    // ==================== 构造函数 ====================
    
    public InfectionComponent() {
        this.canSpreadInfection = true;
        this.infectionChance = BASE_INFECTION_CHANCE;
        this.infectionRadius = DEFAULT_INFECTION_RADIUS;
        this.maxInfectOnDeath = DEFAULT_MAX_INFECT_ON_DEATH;
        this.immuneEntities = new HashSet<>();
    }
    
    // ==================== IInfectionComponent 实现 ====================
    
    @Override
    public void initialize(boolean canSpread, float infectionChance) {
        this.canSpreadInfection = canSpread;
        this.infectionChance = infectionChance;
    }
    
    @Override
    public boolean canSpreadInfection() {
        return canSpreadInfection;
    }
    
    @Override
    public void setCanSpreadInfection(boolean canSpread) {
        this.canSpreadInfection = canSpread;
    }
    
    @Override
    public float getInfectionChance() {
        return infectionChance;
    }
    
    @Override
    public void setInfectionChance(float chance) {
        this.infectionChance = Math.clamp(chance, 0.0f, 1.0f);
    }
    
    @Override
    public float getInfectionRadius() {
        return infectionRadius;
    }
    
    @Override
    public void setInfectionRadius(float radius) {
        this.infectionRadius = Math.max(0.0f, radius);
    }
    
    @Override
    public InfectedResult canInfect(LivingEntity target) {
        if (target == null || !target.isAlive()) {
            return InfectedResult.INVALID_TARGET;
        }
        
        // 检查是否已经是寄生虫
        if (target instanceof com.subspaceparasite.api.entity.IParasiteEntity) {
            return InfectedResult.ALREADY_PARASITE;
        }
        
        // 检查是否有免疫
        if (hasImmunity(target)) {
            return InfectedResult.IMMUNE;
        }
        
        // 检查是否是 Boss 或其他不可感染生物
        if (isBossOrImmune(target)) {
            return InfectedResult.IMMUNE;
        }
        
        return InfectedResult.CAN_INFECT;
    }
    
    @Override
    public boolean infect(LivingEntity target) {
        InfectedResult result = canInfect(target);
        if (result != InfectedResult.CAN_INFECT) {
            return false;
        }
        
        // 进行感染概率检定
        if (target.level().random.nextFloat() > infectionChance) {
            return false;
        }
        
        // 执行感染逻辑
        // TODO: 这里需要调用感染转换系统，将目标转换为对应的感染生物
        // 目前仅添加免疫标记，防止重复感染
        addImmunity(target);
        
        return true;
    }
    
    @Override
    public void spreadInfectionOnDeath(Level level, Vec3 position) {
        if (!canSpreadInfection) return;
        if (level.isClientSide) return;
        
        // 获取范围内的所有生物
        var nearbyEntities = level.getEntitiesOfClass(
            LivingEntity.class,
            position.inflate(infectionRadius),
            entity -> entity != null && entity.isAlive()
        );
        
        int infectedCount = 0;
        for (LivingEntity nearby : nearbyEntities) {
            if (infectedCount >= maxInfectOnDeath) break;
            
            InfectedResult result = canInfect(nearby);
            if (result == InfectedResult.CAN_INFECT) {
                if (infect(nearby)) {
                    infectedCount++;
                }
            }
        }
        
        if (infectedCount > 0) {
            com.subspaceparasite.SubspaceParasite.LOGGER.debug(
                "InfectionComponent spread infection to {} entities at {}", 
                infectedCount, position
            );
        }
    }
    
    @Override
    public int getMaxInfectOnDeath() {
        return maxInfectOnDeath;
    }
    
    @Override
    public void setMaxInfectOnDeath(int count) {
        this.maxInfectOnDeath = Math.max(0, count);
    }
    
    @Override
    public void addImmunity(LivingEntity entity) {
        if (entity != null && entity.getUUID() != null) {
            immuneEntities.add(entity.getUUID());
        }
    }
    
    @Override
    public boolean hasImmunity(LivingEntity entity) {
        if (entity == null || entity.getUUID() == null) {
            return false;
        }
        return immuneEntities.contains(entity.getUUID());
    }
    
    @Override
    public void removeImmunity(LivingEntity entity) {
        if (entity != null && entity.getUUID() != null) {
            immuneEntities.remove(entity.getUUID());
        }
    }
    
    // ==================== 内部方法 ====================
    
    /**
     * 检查是否是 Boss 或其他不可感染的生物
     */
    private boolean isBossOrImmune(LivingEntity entity) {
        // 检查是否是 Ender Dragon 或 Wither
        String entityType = entity.getType().toString();
        if (entityType.contains("ender_dragon") || entityType.contains("wither")) {
            return true;
        }
        
        // 检查是否有自定义免疫标签
        if (entity.getTags().contains("parasite_immune")) {
            return true;
        }
        
        return false;
    }
    
    // ==================== NBT 保存/加载 ====================
    
    @Override
    public void saveNBT(CompoundTag tag) {
        tag.putBoolean("CanSpreadInfection", canSpreadInfection);
        tag.putFloat("InfectionChance", infectionChance);
        tag.putFloat("InfectionRadius", infectionRadius);
        tag.putInt("MaxInfectOnDeath", maxInfectOnDeath);
        
        // 保存免疫实体列表
        ListTag immuneList = new ListTag();
        for (UUID uuid : immuneEntities) {
            CompoundTag uuidTag = new CompoundTag();
            uuidTag.putLong("MostSignificantBits", uuid.getMostSignificantBits());
            uuidTag.putLong("LeastSignificantBits", uuid.getLeastSignificantBits());
            immuneList.add(uuidTag);
        }
        tag.put("ImmuneEntities", immuneList);
    }
    
    @Override
    public void loadNBT(CompoundTag tag) {
        if (tag.contains("CanSpreadInfection")) {
            this.canSpreadInfection = tag.getBoolean("CanSpreadInfection");
        }
        if (tag.contains("InfectionChance")) {
            this.infectionChance = tag.getFloat("InfectionChance");
        }
        if (tag.contains("InfectionRadius")) {
            this.infectionRadius = tag.getFloat("InfectionRadius");
        }
        if (tag.contains("MaxInfectOnDeath")) {
            this.maxInfectOnDeath = tag.getInt("MaxInfectOnDeath");
        }
        
        // 加载免疫实体列表
        immuneEntities.clear();
        if (tag.contains("ImmuneEntities")) {
            ListTag immuneList = tag.getList("ImmuneEntities", 10);
            for (int i = 0; i < immuneList.size(); i++) {
                CompoundTag uuidTag = immuneList.getCompound(i);
                long mostSig = uuidTag.getLong("MostSignificantBits");
                long leastSig = uuidTag.getLong("LeastSignificantBits");
                immuneEntities.add(new UUID(mostSig, leastSig));
            }
        }
    }
}
