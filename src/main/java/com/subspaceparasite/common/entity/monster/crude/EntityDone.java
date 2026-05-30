package com.subspaceparasite.common.entity.monster.crude;

import com.subspaceparasite.api.entity.IParasiteEntity;
import com.subspaceparasite.api.enums.EvoPhase;
import com.subspaceparasite.api.enums.GeneType;
import com.subspaceparasite.common.entity.EntityParasiteBase;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
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
 * EntityDone - 完成者（Tier 2 特殊功能单位）
 * 
 * 功能：
 * - 殖民地防御者
 * - 区域巡逻单位
 * - 中等战斗能力
 * - 可进化为更高级的守卫形态
 * 
 * @author SubspaceParasite Team
 * @version 1.20.1
 */
public class EntityDone extends EntityParasiteBase implements IParasiteEntity {
    
    // ==================== 常量定义 ====================
    
    private static final float BASE_HEALTH = 30.0f;
    private static final float BASE_ATTACK_DAMAGE = 5.5f;
    private static final float BASE_MOVEMENT_SPEED = 0.30f;
    private static final float BASE_ARMOR = 4.0f;
    
    private static final double PATROL_RANGE = 16.0; // 巡逻范围
    
    // ==================== 成员变量 ====================
    
    /**
     * 巡逻目标位置
     */
    private BlockPos patrolTarget = null;
    
    /**
     * 是否在巡逻状态
     */
    private boolean isPatrolling = false;
    
    // ==================== 构造函数 ====================
    
    public EntityDone(EntityType<? extends EntityDone> entityType, Level level) {
        super(entityType, level);
        this.phase = EvoPhase.CRUDE;
        this.parasiteType = GeneType.CRUDE_DONE;
        this.xpValue = 12;
    }
    
    // ==================== AI 行为注册 ====================
    
    @Override
    protected void registerGoals() {
        super.registerGoals();
        
        // 基础目标
        this.goalSelector.addGoal(0, new FloatGoal(this));
        
        // 近战攻击目标
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.3, true));
        
        // 移动目标
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.1));
        
        // 观察目标
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 10.0f));
        this.goalSelector.addGoal(5, new net.minecraft.world.entity.ai.goal.RandomLookAroundGoal(this));
        
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
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.35);
    }
    
    // ==================== 生命周期方法 ====================
    
    @Override
    public void tick() {
        super.tick();
        
        if (!level().isClientSide()) {
            // 更新巡逻逻辑
            updatePatrolBehavior();
        }
    }
    
    @Override
    public void aiStep() {
        super.aiStep();
        
        // 防御增益
        if (!level().isClientSide()) {
            applyDefenseBonuses();
        }
    }
    
    // ==================== 巡逻逻辑 ====================
    
    /**
     * 更新巡逻行为
     */
    private void updatePatrolBehavior() {
        // 没有目标时进行巡逻
        if (getTarget() == null && !isPatrolling) {
            startPatrol();
        }
        
        // 有敌人时停止巡逻
        if (getTarget() != null && isPatrolling) {
            stopPatrol();
        }
    }
    
    /**
     * 开始巡逻
     */
    private void startPatrol() {
        if (patrolTarget == null || reachedPatrolTarget()) {
            selectNewPatrolTarget();
        }
        isPatrolling = true;
    }
    
    /**
     * 停止巡逻
     */
    private void stopPatrol() {
        isPatrolling = false;
        patrolTarget = null;
    }
    
    /**
     * 选择新的巡逻目标点
     */
    private void selectNewPatrolTarget() {
        // 在周围随机选择一个位置作为巡逻点
        int offsetX = level().random.nextInt((int)PATROL_RANGE * 2) - (int)PATROL_RANGE;
        int offsetZ = level().random.nextInt((int)PATROL_RANGE * 2) - (int)PATROL_RANGE;
        
        BlockPos newPos = blockPosition().offset(offsetX, 0, offsetZ);
        
        // 确保新位置是安全的
        if (isValidPatrolPosition(newPos)) {
            patrolTarget = newPos;
        }
    }
    
    /**
     * 检查是否到达巡逻目标
     */
    private boolean reachedPatrolTarget() {
        if (patrolTarget == null) return true;
        
        double distance = distanceToSqr(patrolTarget.getX() + 0.5, patrolTarget.getY(), patrolTarget.getZ() + 0.5);
        return distance < 2.0;
    }
    
    /**
     * 检查巡逻位置是否有效
     */
    private boolean isValidPatrolPosition(BlockPos pos) {
        // 检查位置是否存在且安全
        return level().isInWorldBounds(pos) && 
               !level().getBlockState(pos).isAir() &&
               level().getBlockState(pos.above()).isAir();
    }
    
    // ==================== 防御增益 ====================
    
    /**
     * 应用防御增益
     */
    private void applyDefenseBonuses() {
        // 在殖民地附近获得防御加成
        // TODO: 检测殖民地建筑并给予增益
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
        float infectionChance = 0.3f; // 30% 基础感染几率
        if (level().random.nextFloat() < infectionChance) {
            // TODO: 触发感染逻辑
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
        // TODO: 使用 ModSounds.DONE_AMBIENT
        return net.minecraft.sounds.SoundEvents.VINDICATOR_AMBIENT;
    }
    
    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        // TODO: 使用 ModSounds.DONE_HURT
        return net.minecraft.sounds.SoundEvents.VINDICATOR_HURT;
    }
    
    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        // TODO: 使用 ModSounds.DONE_DEATH
        return net.minecraft.sounds.SoundEvents.VINDICATOR_DEATH;
    }
    
    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(net.minecraft.sounds.SoundEvents.VINDICATOR_STEP, 0.15F, 0.4F);
    }
    
    // ==================== 掉落物 ====================
    
    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHitIn) {
        super.dropCustomDeathLoot(source, looting, recentlyHitIn);
        
        // TODO: 添加特殊掉落物
    }
    
    // ==================== 生成规则 ====================
    
    @Override
    public boolean checkSpawnObstruction(ServerLevelAccessor level) {
        return level.isUnobstructed(this);
    }
}
