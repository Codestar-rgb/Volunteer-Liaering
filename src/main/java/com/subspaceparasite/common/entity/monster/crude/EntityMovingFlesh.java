package com.subspaceparasite.common.entity.monster.crude;

import com.subspaceparasite.api.entity.IParasiteEntity;
import com.subspaceparasite.api.enums.EvoPhase;
import com.subspaceparasite.api.enums.GeneType;
import com.subspaceparasite.common.entity.EntityParasiteBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * EntityMovingFlesh - 移动血肉（Tier 1 基础殖民地单位）
 * 
 * 功能：
 * - 基础殖民地建设者
 * - 资源收集者
 * - 低级战斗单位
 * - 可进化为更高级的殖民地单位
 * 
 * @author SubspaceParasite Team
 * @version 1.20.1
 */
public class EntityMovingFlesh extends EntityParasiteBase implements IParasiteEntity {
    
    // ==================== 数据同步 ====================
    
    private static final EntityDataAccessor<Integer> DATA_CARRYING_RESOURCE = 
        SynchedEntityData.defineId(EntityMovingFlesh.class, EntityDataSerializers.INT);
    
    private static final EntityDataAccessor<Boolean> DATA_IS_BUILDING = 
        SynchedEntityData.defineId(EntityMovingFlesh.class, EntityDataSerializers.BOOLEAN);
    
    // ==================== 常量定义 ====================
    
    private static final float BASE_HEALTH = 20.0f;
    private static final float BASE_ATTACK_DAMAGE = 4.0f;
    private static final float BASE_MOVEMENT_SPEED = 0.25f;
    private static final float BASE_ARMOR = 2.0f;
    
    private static final int MAX_CARRY_RESOURCES = 5;
    private static final double COLLECTION_RADIUS = 3.0;
    private static final double BUILD_RANGE = 2.0;
    
    // ==================== 成员变量 ====================
    
    /**
     * 当前携带的资源数量
     */
    private int carryingResources = 0;
    
    /**
     * 是否正在执行建造动作
     */
    private boolean isBuilding = false;
    
    /**
     * 资源收集计时器
     */
    private int collectionTimer = 0;
    
    /**
     * 建造进度（0-100）
     */
    private int buildProgress = 0;
    
    // ==================== 构造函数 ====================
    
    public EntityMovingFlesh(EntityType<? extends EntityMovingFlesh> entityType, Level level) {
        super(entityType, level);
        this.phase = EvoPhase.PRIMITIVE;
        this.parasiteType = GeneType.CRUDE_MOVING_FLESH;
        this.xpValue = 8;
    }
    
    // ==================== AI 行为注册 ====================
    
    @Override
    protected void registerGoals() {
        super.registerGoals();
        
        // 清除基类的部分目标，添加自定义目标
        this.goalSelector.removeAllGoals(goal -> 
            goal instanceof MeleeAttackGoal || 
            goal instanceof RandomStrollGoal
        );
        
        // 基础目标
        this.goalSelector.addGoal(0, new FloatGoal(this));
        
        // 殖民地相关目标
        this.goalSelector.addGoal(2, new EntityMovingFlesh.AICollectResourceGoal());
        this.goalSelector.addGoal(3, new EntityMovingFlesh.AIBuildColonyGoal());
        
        // 战斗目标
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.2, true));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0));
        
        // 观察目标
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(7, new net.minecraft.world.entity.ai.goal.RandomLookAroundGoal(this));
        
        // 目标选择器
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers(true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Animal.class, true));
    }
    
    // ==================== 属性配置 ====================
    
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, BASE_HEALTH)
            .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
            .add(Attributes.MOVEMENT_SPEED, BASE_MOVEMENT_SPEED)
            .add(Attributes.ARMOR, BASE_ARMOR)
            .add(Attributes.FOLLOW_RANGE, 48.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.3);
    }
    
    // ==================== 数据同步 ====================
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_CARRYING_RESOURCE, 0);
        this.entityData.define(DATA_IS_BUILDING, false);
    }
    
    // ==================== 生命周期方法 ====================
    
    @Override
    public void tick() {
        super.tick();
        
        if (!level().isClientSide()) {
            // 更新数据同步
            entityData.set(DATA_CARRYING_RESOURCE, carryingResources);
            entityData.set(DATA_IS_BUILDING, isBuilding);
            
            // 资源收集逻辑
            if (carryingResources < MAX_CARRY_RESOURCES) {
                collectionTimer++;
                if (collectionTimer >= 20) { // 每秒尝试收集
                    collectionTimer = 0;
                    tryCollectResource();
                }
            }
            
            // 建造逻辑
            if (carryingResources > 0 && isNearColonySite()) {
                performBuilding();
            }
        }
    }
    
    @Override
    public void aiStep() {
        super.aiStep();
        
        // 殖民地增益效果
        if (!level().isClientSide()) {
            applyColonyBonuses();
        }
    }
    
    // ==================== 资源管理 ====================
    
    /**
     * 尝试收集资源
     */
    private void tryCollectResource() {
        if (carryingResources >= MAX_CARRY_RESOURCES) return;
        
        // 搜索周围的掉落物
        var nearbyItems = level().getEntitiesOfClass(ItemEntity.class, 
            getBoundingBox().inflate(COLLECTION_RADIUS));
        
        for (var item : nearbyItems) {
            if (!item.isAlive() || item.hasPickUpDelay()) continue;
            
            // 检查是否是可收集的资源
            if (isCollectableItem(item)) {
                carryingResources++;
                item.discard();
                
                if (carryingResources >= MAX_CARRY_RESOURCES) break;
            }
        }
    }
    
    /**
     * 检查物品是否可收集
     */
    private boolean isCollectableItem(ItemEntity item) {
        // 暂时收集所有物品，后续可扩展为特定资源
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
    
    // ==================== 建造逻辑 ====================
    
    /**
     * 检查是否在殖民地选址附近
     */
    private boolean isNearColonySite() {
        // TODO: 与殖民地系统交互
        BlockPos pos = blockPosition();
        return level().getBlockState(pos.below()).isSolidRender(level(), pos.below());
    }
    
    /**
     * 执行建造动作
     */
    private void performBuilding() {
        if (buildProgress >= 100) {
            // 建造完成
            completeBuilding();
            return;
        }
        
        isBuilding = true;
        buildProgress++;
        
        // 消耗资源
        if (buildProgress % 10 == 0) {
            consumeResource(1);
        }
        
        // 播放建造粒子效果
        if (level().isClientSide()) {
            spawnBuildingParticles();
        }
    }
    
    /**
     * 建造完成
     */
    private void completeBuilding() {
        buildProgress = 0;
        isBuilding = false;
        
        // TODO: 生成殖民地方块或结构
        // 通知殖民地系统
    }
    
    /**
     * 生成建造粒子效果
     */
    private void spawnBuildingParticles() {
        // TODO: 实现粒子效果
    }
    
    // ==================== 殖民地增益 ====================
    
    /**
     * 应用殖民地范围内的增益效果
     */
    private void applyColonyBonuses() {
        // TODO: 从殖民地系统获取增益
        // 在殖民地范围内获得速度/生命恢复增益
    }
    
    // ==================== 战斗相关 ====================
    
    @Override
    public boolean doHurtTarget(Entity entity) {
        if (super.doHurtTarget(entity)) {
            // 攻击时可能传播感染
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
        // TODO: 使用感染组件进行感染
        float infectionChance = 0.3f; // 30% 基础感染几率
        if (level().random.nextFloat() < infectionChance) {
            // 触发感染逻辑
        }
    }
    
    /**
     * 检查目标是否已是寄生虫
     */
    private boolean isParasiteOf(LivingEntity entity) {
        return entity instanceof IParasiteEntity;
    }
    
    // ==================== 音效 ====================
    
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SLIME_SQUISH;
    }
    
    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.SLIME_HURT;
    }
    
    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SLIME_DEATH;
    }
    
    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(SoundEvents.SLIME_STEP, 0.15F, 0.4F);
    }
    
    // ==================== 掉落物 ====================
    
    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHitIn) {
        super.dropCustomDeathLoot(source, looting, recentlyHitIn);
        
        // 死亡时掉落携带的资源
        if (carryingResources > 0 && !level().isClientSide()) {
            // TODO: 掉落资源物品
            carryingResources = 0;
        }
    }
    
    // ==================== NBT 数据 ====================
    
    @Override
    public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("CarryingResources", carryingResources);
        compound.putInt("BuildProgress", buildProgress);
        compound.putBoolean("IsBuilding", isBuilding);
    }
    
    @Override
    public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("CarryingResources")) {
            this.carryingResources = compound.getInt("CarryingResources");
        }
        if (compound.contains("BuildProgress")) {
            this.buildProgress = compound.getInt("BuildProgress");
        }
        if (compound.contains("IsBuilding")) {
            this.isBuilding = compound.getBoolean("IsBuilding");
        }
    }
    
    // ==================== 生成规则 ====================
    
    @Override
    public boolean checkSpawnObstruction(ServerLevelAccessor level) {
        return level.isUnobstructed(this);
    }
    
    // ==================== 内部 AI 类 ====================
    
    /**
     * 资源收集 AI 目标
     */
    private class AICollectResourceGoal extends net.minecraft.world.entity.ai.goal.Goal {
        
        private int searchTimer = 0;
        
        @Override
        public boolean canUse() {
            return carryingResources < MAX_CARRY_RESOURCES;
        }
        
        @Override
        public void tick() {
            searchTimer++;
            if (searchTimer >= 40) {
                searchTimer = 0;
                tryCollectResource();
            }
        }
    }
    
    /**
     * 殖民地建造 AI 目标
     */
    private class AIBuildColonyGoal extends net.minecraft.world.entity.ai.goal.Goal {
        
        @Override
        public boolean canUse() {
            return carryingResources > 0 && isNearColonySite() && !isBuilding;
        }
        
        @Override
        public boolean canContinueToUse() {
            return carryingResources > 0 && isNearColonySite();
        }
        
        @Override
        public void start() {
            isBuilding = true;
        }
        
        @Override
        public void stop() {
            if (buildProgress >= 100 || carryingResources <= 0) {
                isBuilding = false;
                buildProgress = 0;
            }
        }
        
        @Override
        public void tick() {
            performBuilding();
        }
    }
}
