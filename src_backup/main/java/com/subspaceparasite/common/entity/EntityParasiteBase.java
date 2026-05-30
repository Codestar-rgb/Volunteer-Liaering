package com.subspaceparasite.common.entity;

import com.subspaceparasite.api.entity.IParasiteEntity;
import com.subspaceparasite.api.entity.ParasiteType;
import com.subspaceparasite.api.enums.EvoPhase;
import com.subspaceparasite.api.enums.GeneType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

/**
 * 寄生虫实体基类
 * 
 * 所有寄生虫实体的共同父类，提供基础的寄生虫特性实现
 * 
 * @author SubspaceParasite Team
 * @version 1.20.1
 */
public abstract class EntityParasiteBase extends Mob implements IParasiteEntity {

    // ==================== 数据同步 ====================
    
    /**
     * 当前进化阶段
     */
    protected EvoPhase phase = EvoPhase.INFECTED;
    
    /**
     * 寄生虫类型（基因类型）
     */
    protected GeneType parasiteType = GeneType.INF_HUMAN;
    
    /**
     * 击杀计数（用于进化判定）
     */
    protected int killCount = 0;
    
    /**
     * XP 奖励值
     */
    protected int xpValue = 5;

    // ==================== 构造函数 ====================

    public EntityParasiteBase(EntityType<? extends EntityParasiteBase> entityType, Level level) {
        super(entityType, level);
    }

    // ==================== IParasiteEntity 实现 ====================

    @Override
    public boolean isParasite() {
        return true;
    }

    @Override
    public EvoPhase getPhase() {
        return phase;
    }

    @Override
    public void setPhase(EvoPhase phase) {
        this.phase = phase;
    }

    @Override
    public GeneType getParasiteType() {
        return parasiteType;
    }

    @Override
    public void setParasiteType(GeneType type) {
        this.parasiteType = type;
    }

    @Override
    public boolean canBeInfected() {
        // 默认不能被感染，子类可重写
        return false;
    }

    @Override
    public boolean shouldDropLoot() {
        // 默认不掉落原版物品，子类可重写
        return false;
    }

    @Override
    public int getKillCount() {
        return killCount;
    }

    @Override
    public void addKillCount(int amount) {
        this.killCount += amount;
    }

    @Override
    public void resetKillCount() {
        this.killCount = 0;
    }

    @Override
    public boolean canEvolve() {
        // 默认检查是否可以进化到下一阶段
        return phase.canEvolve() && killCount >= getKillsRequiredForEvolution();
    }

    @Override
    public void evolve() {
        if (canEvolve()) {
            EvoPhase nextPhase = phase.nextPhase();
            setPhase(nextPhase);
            onEvolved(nextPhase);
            resetKillCount();
        }
    }

    /**
     * 进化完成后的回调
     * @param newPhase 新的进化阶段
     */
    protected void onEvolved(EvoPhase newPhase) {
        // 子类可重写此方法以添加进化效果
        if (!level().isClientSide()) {
            // TODO: 播放进化特效
            // TODO: 恢复生命值
            // TODO: 应用增益效果
        }
    }

    @Override
    public float getInfectionRadius() {
        return 5.0f;
    }

    @Override
    public int getMaxInfectOnDeath() {
        return 3;
    }

    // ==================== 配置方法 ====================

    /**
     * 获取进化所需的击杀数
     * @return 击杀阈值
     */
    protected int getKillsRequiredForEvolution() {
        return 10;
    }

    // ==================== AI 行为注册 ====================

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // 基础目标选择器
        this.targetSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal<>(this, net.minecraft.world.entity.player.Player.class, true));
        this.targetSelector.addGoal(3, new net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal<>(this, net.minecraft.world.entity.animal.Animal.class, true));
        
        // 基础任务选择器
        this.goalSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(2, new net.minecraft.world.entity.ai.goal.RandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(3, new net.minecraft.world.entity.ai.goal.LookAtPlayerGoal(this, net.minecraft.world.entity.player.Player.class, 8.0f));
        this.goalSelector.addGoal(4, new net.minecraft.world.entity.ai.goal.RandomLookAroundGoal(this));
    }

    // ==================== 生成规则 ====================

    @Override
    public boolean canSpawn(ServerLevelAccessor level, SpawnReason spawnReason) {
        return true;
    }

    @Override
    public CheckSpawnResult checkSpawnRules(ServerLevelAccessor level, SpawnReason spawnReason) {
        return CheckSpawnResult.SUCCESS;
    }

    @Override
    public boolean checkSpawnObstruction(ServerLevelAccessor level) {
        return level.isUnobstructed(this);
    }

    // ==================== NBT 数据保存/加载 ====================

    @Override
    public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("ParasitePhase", phase.getName());
        compound.putString("ParasiteType", parasiteType.getId());
        compound.putInt("KillCount", killCount);
        compound.putInt("XPValue", xpValue);
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("ParasitePhase")) {
            this.phase = EvoPhase.fromName(compound.getString("ParasitePhase"));
        }
        if (compound.contains("ParasiteType")) {
            this.parasiteType = GeneType.fromId(compound.getString("ParasiteType"));
        }
        if (compound.contains("KillCount")) {
            this.killCount = compound.getInt("KillCount");
        }
        if (compound.contains("XPValue")) {
            this.xpValue = compound.getInt("XPValue");
        }
    }

    // ==================== 音效方法（子类实现） ====================

    // 子类应重写以下方法以提供特定音效：
    // - getAmbientSound()
    // - getHurtSound(DamageSource)
    // - getDeathSound()
}
