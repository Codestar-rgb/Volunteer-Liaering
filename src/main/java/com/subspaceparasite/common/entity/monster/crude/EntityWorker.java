package com.subspaceparasite.common.entity.monster.crude;

import net.minecraft.world.entity.Entity;
import com.subspaceparasite.api.IParasite;
import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.api.parasite.GeneType;
import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.ColonyComponent;
import com.subspaceparasite.common.entity.base.EntityCrudeBase;
import com.subspaceparasite.common.entity.base.InfectionComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * EntityWorker - 工虫（Tier 1 高级殖民地单位）
 * 
 * 功能：
 * - 高级殖民地建设者
 * - 资源加工者
 * - 殖民地维护者
 * - 可进化为更高级的工程师单位
 * 
 * @author SubspaceParasite Team
 * @version 1.20.1
 */
public class EntityWorker extends EntityCrudeBase implements IParasite {
    
    // ==================== 数据同步 ====================
    
    private static final EntityDataAccessor<Integer> DATA_WORK_PROGRESS = 
        SynchedEntityData.defineId(EntityWorker.class, EntityDataSerializers.INT);
    
    private static final EntityDataAccessor<Integer> DATA_CARRYING_RESOURCE = 
        SynchedEntityData.defineId(EntityWorker.class, EntityDataSerializers.INT);
    
    private static final EntityDataAccessor<Boolean> DATA_IS_WORKING = 
        SynchedEntityData.defineId(EntityWorker.class, EntityDataSerializers.BOOLEAN);
    
    // ==================== 常量定义 ====================
    
    private static final float BASE_HEALTH = 25.0f;
    private static final float BASE_ATTACK_DAMAGE = 5.0f;
    private static final float BASE_MOVEMENT_SPEED = 0.28f;
    private static final float BASE_ARMOR = 4.0f;
    
    private static final int MAX_CARRY_RESOURCES = 8;
    private static final double WORK_RANGE = 5.0;
    private static final int BASE_WORK_SPEED = 2; // 每 tick 增加的工作进度
    
    // ==================== 成员变量 ====================
    
    /**
     * 当前工作进度（0-100）
     */
    private int workProgress = 0;
    
    /**
     * 当前携带的资源数量
     */
    private int carryingResources = 0;
    
    /**
     * 是否正在工作
     */
    private boolean isWorking = false;
    
    /**
     * 工作效率倍率（可通过进化提升）
     */
    private float workEfficiency = 1.0f;
    
    /**
     * 工作计时器
     */
    private int workTimer = 0;
    
    // ==================== 构造函数 ====================
    
    public EntityWorker(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.setPhaseCreated(EvoPhase.PHASE_1);
        this.parasiteType = ParasiteType.WORKER;
        this.xpReward = 12;
    }
    
    // ==================== AI 行为注册 ====================
    
    @Override
    protected void registerGoals() {
        super.registerGoals();
        
        // 清除基类的部分目标（1.20.1 GoalSelector 没有 removeAllGoals(Predicate) 方法）
        this.goalSelector.getAvailableGoals().removeIf(w ->
            w.getGoal() instanceof MeleeAttackGoal ||
            w.getGoal() instanceof RandomStrollGoal
        );
        
        // 基础目标
        this.goalSelector.addGoal(0, new FloatGoal(this));
        
        // 工作相关目标
        this.goalSelector.addGoal(2, new EntityWorker.AIWorkGoal());
        this.goalSelector.addGoal(3, new EntityWorker.AICollectResourceGoal());
        
        // 战斗目标
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.3, true));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.1));
        
        // 观察目标
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 10.0f));
        this.goalSelector.addGoal(7, new net.minecraft.world.entity.ai.goal.RandomLookAroundGoal(this));
        
        // 目标选择器
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Animal.class, true));
    }
    
    // ==================== 属性配置 ====================
    
    public static AttributeSupplier.Builder createAttributes() {
        return EntityCrudeBase.createAttributes()
            .add(Attributes.MAX_HEALTH, BASE_HEALTH)
            .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
            .add(Attributes.MOVEMENT_SPEED, BASE_MOVEMENT_SPEED)
            .add(Attributes.ARMOR, BASE_ARMOR)
            .add(Attributes.FOLLOW_RANGE, 64.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.4);
    }
    
    // ==================== 数据同步 ====================
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_WORK_PROGRESS, 0);
        this.entityData.define(DATA_CARRYING_RESOURCE, 0);
        this.entityData.define(DATA_IS_WORKING, false);
    }
    
    // ==================== 生命周期方法 ====================
    
    @Override
    public void tick() {
        super.tick();
        
        if (!level().isClientSide()) {
            // 更新数据同步
            entityData.set(DATA_WORK_PROGRESS, workProgress);
            entityData.set(DATA_CARRYING_RESOURCE, carryingResources);
            entityData.set(DATA_IS_WORKING, isWorking);
            
            // 工作逻辑
            if (isWorking && workProgress < 100) {
                workTimer++;
                if (workTimer >= 10) { // 每 0.5 秒执行一次工作
                    workTimer = 0;
                    performWork();
                }
            }
            
            // 自动收集资源
            if (carryingResources < MAX_CARRY_RESOURCES && !isWorking) {
                tryCollectNearbyResources();
            }
        }
    }
    
    @Override
    public void aiStep() {
        super.aiStep();
        
        if (!level().isClientSide()) {
            // 应用殖民地增益
            applyColonyBonuses();
            
            // 工作完成检查
            if (workProgress >= 100) {
                completeWork();
            }
        }
    }
    
    // ==================== 资源管理 ====================
    
    /**
     * 尝试收集附近资源
     */
    private void tryCollectNearbyResources() {
        if (carryingResources >= MAX_CARRY_RESOURCES) return;
        
        var nearbyItems = level().getEntitiesOfClass(ItemEntity.class, 
            getBoundingBox().inflate(WORK_RANGE));
        
        for (var item : nearbyItems) {
            if (!item.isAlive() || item.hasPickUpDelay()) continue;
            
            if (isValidResource(item)) {
                carryingResources++;
                item.discard();
                
                if (carryingResources >= MAX_CARRY_RESOURCES) break;
            }
        }
    }
    
    /**
     * 检查是否是有效资源
     */
    private boolean isValidResource(ItemEntity item) {
        // Accept parasite-related drops and organic materials
        return true;
    }
    
    /**
     * 消耗资源
     */
    public boolean consumeResource(int amount) {
        if (carryingResources >= amount) {
            carryingResources -= amount;
            return true;
        }
        return false;
    }
    
    // ==================== 工作逻辑 ====================
    
    /**
     * 执行工作
     */
    private void performWork() {
        if (!canWork()) return;
        
        isWorking = true;
        int progressGain = (int)(BASE_WORK_SPEED * workEfficiency);
        workProgress += progressGain;
        
        // 消耗资源
        if (workProgress % 20 == 0) {
            consumeResource(1);
        }
        
        // 播放工作粒子
        if (level().isClientSide()) {
            spawnWorkParticles();
        }
    }
    
    /**
     * 检查是否可以工作
     */
    private boolean canWork() {
        return carryingResources > 0 && isNearWorkSite();
    }
    
    /**
     * 检查是否在工作地点附近
     */
    private boolean isNearWorkSite() {
        // Check if within a ColonyComponent's radius
        if (getColonyComponent() != null && getColonyComponent().isColonyMember()) {
            return true;
        }
        BlockPos pos = blockPosition();
        return level().getBlockState(pos.below()).isSolidRender(level(), pos.below());
    }
    
    /**
     * 工作完成
     */
    private void completeWork() {
        // 根据工作类型执行不同效果
        WorkType currentWork = getCurrentWorkType();
        
        switch (currentWork) {
            case BUILDING:
                completeBuilding();
                break;
            case REPAIRING:
                completeRepairing();
                break;
            case PROCESSING:
                completeProcessing();
                break;
            default:
                break;
        }
        
        workProgress = 0;
        isWorking = false;
    }
    
    /**
     * 获取当前工作类型
     */
    private WorkType getCurrentWorkType() {
        // Determine work type from colony component context
        ColonyComponent colony = getColonyComponent();
        if (colony != null && colony.isColonyLeader()) {
            return WorkType.BUILDING;
        }
        return WorkType.PROCESSING;
    }
    
    /**
     * 完成建造
     */
    private void completeBuilding() {
        // Add colony points via ColonyComponent
        if (getColonyComponent() != null) {
            getColonyComponent().addColonyPoints(20.0);
        }
    }
    
    /**
     * 完成修复
     */
    private void completeRepairing() {
        // Heal nearby colony structures by giving regeneration
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.HEART,
                    getX(), getY() + 1.0, getZ(), 2, 0.5, 0.5, 0.5, 0.01);
        }
    }
    
    /**
     * 完成加工
     */
    private void completeProcessing() {
        // Convert resources into colony points
        if (getColonyComponent() != null) {
            getColonyComponent().addColonyPoints(15.0 * workEfficiency);
        }
    }
    
    /**
     * 生成工作粒子效果
     */
    private void spawnWorkParticles() {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER,
                    getX(), getY() + 0.5, getZ(), 3, 0.3, 0.3, 0.3, 0.01);
        }
    }
    
    // ==================== 殖民地增益 ====================
    
    /**
     * 应用殖民地范围内的增益效果
     */
    private void applyColonyBonuses() {
        ColonyComponent colony = getColonyComponent();
        if (colony == null) return;
        if (colony.isColonyMember() && colony.getColonyCenter() != null) {
            double distToCenter = blockPosition().distManhattan(colony.getColonyCenter());
            if (distToCenter <= colony.getColonyRadius()) {
                // Work efficiency boost within colony radius
                workEfficiency = 1.0f + colony.getColonyRadius() * 0.01f;
                // Regeneration within colony radius
                if (!hasEffect(net.minecraft.world.effect.MobEffects.REGENERATION)) {
                    addEffect(new net.minecraft.world.effect.MobEffectInstance(
                            net.minecraft.world.effect.MobEffects.REGENERATION, 60, 0, false, false));
                }
            }
        }
    }
    
    // ==================== 战斗相关 ====================
    
    @Override
    public boolean doHurtTarget(Entity entity) {
        if (super.doHurtTarget(entity)) {
            // 工虫攻击时有几率传播感染
            if (entity instanceof LivingEntity living && !isParasiteOf(living)) {
                attemptInfection(living);
            }
            return true;
        }
        return false;
    }
    
    /**
     * 尝试感染目标
     */
    private void attemptInfection(LivingEntity target) {
        // Use infection component to spread COTH
        InfectionComponent infection = getInfectionComponent();
        if (infection != null) {
            infection.spreadCOTH(target);
        }
    }
    
    /**
     * 检查目标是否已是寄生虫
     */
    private boolean isParasiteOf(LivingEntity entity) {
        return entity instanceof IParasite;
    }
    
    // ==================== 音效 ====================
    
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SILVERFISH_AMBIENT;
    }
    
    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.SILVERFISH_HURT;
    }
    
    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SILVERFISH_DEATH;
    }
    
    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(SoundEvents.SILVERFISH_STEP, 0.1F, 0.3F);
    }
    
    // ==================== 掉落物 ====================

    @Override
    public void die(DamageSource source) {
        super.die(source);

        // 死亡时掉落携带的资源
        if (carryingResources > 0 && !this.level().isClientSide()) {
            // TODO: 掉落资源物品
            carryingResources = 0;
        }
    }
    
    // ==================== NBT 数据 ====================
    
    @Override
    public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("WorkProgress", workProgress);
        compound.putInt("CarryingResources", carryingResources);
        compound.putBoolean("IsWorking", isWorking);
        compound.putFloat("WorkEfficiency", workEfficiency);
    }
    
    @Override
    public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("WorkProgress")) {
            this.workProgress = compound.getInt("WorkProgress");
        }
        if (compound.contains("CarryingResources")) {
            this.carryingResources = compound.getInt("CarryingResources");
        }
        if (compound.contains("IsWorking")) {
            this.isWorking = compound.getBoolean("IsWorking");
        }
        if (compound.contains("WorkEfficiency")) {
            this.workEfficiency = compound.getFloat("WorkEfficiency");
        }
    }
    
    // ==================== 生成规则 ====================
    
    @Override
    public boolean checkSpawnObstruction(net.minecraft.world.level.LevelReader level) {
        return true;
    }
    
    // ==================== 内部枚举 ====================
    
    /**
     * 工作类型枚举
     */
    private enum WorkType {
        BUILDING,      // 建造
        REPAIRING,     // 修复
        PROCESSING,    // 加工
        HARVESTING     // 采集
    }
    
    // ==================== 内部 AI 类 ====================
    
    /**
     * 工作 AI 目标
     */
    private class AIWorkGoal extends net.minecraft.world.entity.ai.goal.Goal {
        
        @Override
        public boolean canUse() {
            return carryingResources > 0 && !isWorking && workProgress < 100;
        }
        
        @Override
        public boolean canContinueToUse() {
            return carryingResources > 0 && workProgress < 100;
        }
        
        @Override
        public void start() {
            isWorking = true;
        }
        
        @Override
        public void stop() {
            if (workProgress >= 100 || carryingResources <= 0) {
                isWorking = false;
            }
        }
        
        @Override
        public void tick() {
            if (canWork()) {
                performWork();
            }
        }
    }
    
    /**
     * 资源收集 AI 目标
     */
    private class AICollectResourceGoal extends net.minecraft.world.entity.ai.goal.Goal {
        
        private int searchTimer = 0;
        
        @Override
        public boolean canUse() {
            return carryingResources < MAX_CARRY_RESOURCES && !isWorking;
        }
        
        @Override
        public void tick() {
            searchTimer++;
            if (searchTimer >= 30) {
                searchTimer = 0;
                tryCollectNearbyResources();
            }
        }
    }
}
