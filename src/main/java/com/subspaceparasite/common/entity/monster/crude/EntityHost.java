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
 * EntityHost - 宿主（Tier 2 核心单位）
 * 
 * 功能：
 * - 殖民地核心单位
 * - 生成其他寄生虫
 * - 中等战斗能力
 * - 可进化为更高级的巢穴形态
 * 
 * @author SubspaceParasite Team
 * @version 1.20.1
 */
public class EntityHost extends EntityParasiteBase implements IParasiteEntity {
    
    // ==================== 常量定义 ====================
    
    private static final float BASE_HEALTH = 40.0f;
    private static final float BASE_ATTACK_DAMAGE = 6.0f;
    private static final float BASE_MOVEMENT_SPEED = 0.26f;
    private static final float BASE_ARMOR = 4.5f;
    
    private static final int SPAWN_COOLDOWN = 200; // 生成冷却时间（tick）
    private static final double SPAWN_RANGE = 6.0; // 生成范围
    
    // ==================== 成员变量 ====================
    
    /**
     * 生成冷却计时器
     */
    private int spawnCooldown = 0;
    
    /**
     * 是否正在生成单位
     */
    private boolean isSpawning = false;
    
    // ==================== 构造函数 ====================
    
    public EntityHost(EntityType<? extends EntityHost> entityType, Level level) {
        super(entityType, level);
        this.phase = EvoPhase.CRUDE;
        this.parasiteType = GeneType.CRUDE_HOST;
        this.xpValue = 18;
    }
    
    // ==================== AI 行为注册 ====================
    
    @Override
    protected void registerGoals() {
        super.registerGoals();
        
        // 基础目标
        this.goalSelector.addGoal(0, new FloatGoal(this));
        
        // 近战攻击目标
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, true));
        
        // 移动目标
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.9));
        
        // 观察目标
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 12.0f));
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
            .add(Attributes.FOLLOW_RANGE, 56.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.45);
    }
    
    // ==================== 生命周期方法 ====================
    
    @Override
    public void tick() {
        super.tick();
        
        if (!level().isClientSide()) {
            // 更新生成冷却
            if (spawnCooldown > 0) {
                spawnCooldown--;
            }
            
            // 检查是否可以生成单位
            checkSpawnUnit();
        }
    }
    
    @Override
    public void aiStep() {
        super.aiStep();
        
        // 核心增益
        if (!level().isClientSide()) {
            applyCoreBonuses();
        }
    }
    
    // ==================== 生成逻辑 ====================
    
    /**
     * 检查是否可以生成单位
     */
    private void checkSpawnUnit() {
        if (spawnCooldown > 0 || isSpawning) return;
        
        // 检查周围同类数量
        var nearbyAllies = level().getEntitiesOfClass(IParasiteEntity.class, 
            getBoundingBox().inflate(SPAWN_RANGE));
        
        // 最多生成一定数量的随从
        if (nearbyAllies.size() < 6) {
            performSpawn();
            spawnCooldown = SPAWN_COOLDOWN;
        }
    }
    
    /**
     * 执行生成
     */
    private void performSpawn() {
        isSpawning = true;
        
        // TODO: 生成低级寄生虫单位（如 MovingFlesh）
        // 播放生成动画和粒子效果
        
        isSpawning = false;
    }
    
    // ==================== 核心增益 ====================
    
    /**
     * 应用核心增益
     */
    private void applyCoreBonuses() {
        // 为周围同伴提供增益
        var nearbyAllies = level().getEntitiesOfClass(IParasiteEntity.class, 
            getBoundingBox().inflate(12.0));
        
        for (IParasiteEntity ally : nearbyAllies) {
            // TODO: 给予生命恢复或其他增益
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
            return true;
        }
        return false;
    }
    
    /**
     * 尝试感染目标
     */
    private void attemptInfection(LivingEntity target) {
        float infectionChance = 0.35f; // 35% 基础感染几率
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
        // TODO: 使用 ModSounds.HOST_AMBIENT
        return net.minecraft.sounds.SoundEvents.EVOKER_AMBIENT;
    }
    
    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        // TODO: 使用 ModSounds.HOST_HURT
        return net.minecraft.sounds.SoundEvents.EVOKER_HURT;
    }
    
    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        // TODO: 使用 ModSounds.HOST_DEATH
        return net.minecraft.sounds.SoundEvents.EVOKER_DEATH;
    }
    
    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(net.minecraft.sounds.SoundEvents.EVOKER_STEP, 0.18F, 0.4F);
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
