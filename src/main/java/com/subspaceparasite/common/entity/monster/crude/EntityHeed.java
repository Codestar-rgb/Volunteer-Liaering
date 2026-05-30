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
 * EntityHeed - 注视者（Tier 2 感知单位）
 * 
 * 功能：
 * - 侦察单位
 * - 高感知范围
 * - 警告周围同伴
 * - 中等战斗能力
 * 
 * @author SubspaceParasite Team
 * @version 1.20.1
 */
public class EntityHeed extends EntityParasiteBase implements IParasiteEntity {
    
    // ==================== 常量定义 ====================
    
    private static final float BASE_HEALTH = 28.0f;
    private static final float BASE_ATTACK_DAMAGE = 5.0f;
    private static final float BASE_MOVEMENT_SPEED = 0.35f;
    private static final float BASE_ARMOR = 3.0f;
    
    private static final double DETECTION_RANGE = 32.0; // 探测范围
    private static final int ALERT_COOLDOWN = 60; // 警报冷却时间（tick）
    
    // ==================== 成员变量 ====================
    
    /**
     * 警报冷却计时器
     */
    private int alertCooldown = 0;
    
    /**
     * 是否处于警戒状态
     */
    private boolean isAlerted = false;
    
    // ==================== 构造函数 ====================
    
    public EntityHeed(EntityType<? extends EntityHeed> entityType, Level level) {
        super(entityType, level);
        this.phase = EvoPhase.CRUDE;
        this.parasiteType = GeneType.CRUDE_HEED;
        this.xpValue = 14;
    }
    
    // ==================== AI 行为注册 ====================
    
    @Override
    protected void registerGoals() {
        super.registerGoals();
        
        // 基础目标
        this.goalSelector.addGoal(0, new FloatGoal(this));
        
        // 近战攻击目标
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.5, true));
        
        // 移动目标（较快）
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.3));
        
        // 观察目标（更大的视野）
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 16.0f));
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
            .add(Attributes.FOLLOW_RANGE, 80.0) // 超远追踪距离
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.2);
    }
    
    // ==================== 生命周期方法 ====================
    
    @Override
    public void tick() {
        super.tick();
        
        if (!level().isClientSide()) {
            // 更新警报冷却
            if (alertCooldown > 0) {
                alertCooldown--;
            }
            
            // 更新警戒状态
            updateAlertStatus();
            
            // 探测敌人
            detectEnemies();
        }
    }
    
    @Override
    public void aiStep() {
        super.aiStep();
        
        // 感知增益
        if (!level().isClientSide()) {
            applySensoryBonuses();
        }
    }
    
    // ==================== 感知逻辑 ====================
    
    /**
     * 更新警戒状态
     */
    private void updateAlertStatus() {
        if (getTarget() != null || isAlerted) {
            isAlerted = true;
        } else {
            isAlerted = false;
        }
    }
    
    /**
     * 探测敌人
     */
    private void detectEnemies() {
        if (alertCooldown > 0) return;
        
        // 搜索周围的敌对生物
        var nearbyPlayers = level().getEntitiesOfClass(Player.class, 
            getBoundingBox().inflate(DETECTION_RANGE));
        
        for (Player player : nearbyPlayers) {
            if (!player.isSpectator() && canSeeEntity(player)) {
                // 发现玩家，发出警报
                alertNearbyAllies();
                setTarget(player);
                alertCooldown = ALERT_COOLDOWN;
                break;
            }
        }
    }
    
    /**
     * 检查是否能看见目标
     */
    private boolean canSeeEntity(LivingEntity entity) {
        // 简单的视线检查
        return hasLineOfSight(entity);
    }
    
    /**
     * 警报附近同伴
     */
    private void alertNearbyAllies() {
        var nearbyAllies = level().getEntitiesOfClass(IParasiteEntity.class, 
            getBoundingBox().inflate(DETECTION_RANGE));
        
        for (IParasiteEntity ally : nearbyAllies) {
            if (ally instanceof EntityHeed heed) {
                // 提醒其他注视者
                heed.alertCooldown = Math.min(heed.alertCooldown, 20);
            }
            // TODO: 提醒其他类型的寄生虫
        }
    }
    
    // ==================== 感知增益 ====================
    
    /**
     * 应用感知增益
     */
    private void applySensoryBonuses() {
        // 警戒状态下获得速度提升
        if (isAlerted) {
            // TODO: 应用临时速度增益
        }
    }
    
    // ==================== 战斗相关 ====================
    
    @Override
    public boolean doHurtTarget(Entity entity) {
        if (super.doHurtTarget(entity)) {
            // 攻击时可能传播感染
            if (entity instanceof LivingEntity living && !isParasiteOf(living)) {
                attemptInfection(living);
            }
            
            // 警戒状态下造成额外伤害
            if (isAlerted) {
                // 已在上层处理
            }
            
            return true;
        }
        return false;
    }
    
    /**
     * 尝试感染目标
     */
    private void attemptInfection(LivingEntity target) {
        float infectionChance = isAlerted ? 0.4f : 0.25f; // 警戒状态下更高感染率
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
        // TODO: 使用 ModSounds.HEED_AMBIENT
        return net.minecraft.sounds.SoundEvents.PILLAGER_AMBIENT;
    }
    
    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        // TODO: 使用 ModSounds.HEED_HURT
        return net.minecraft.sounds.SoundEvents.PILLAGER_HURT;
    }
    
    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        // TODO: 使用 ModSounds.HEED_DEATH
        return net.minecraft.sounds.SoundEvents.PILLAGER_DEATH;
    }
    
    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(net.minecraft.sounds.SoundEvents.PILLAGER_STEP, 0.12F, 0.5F);
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
