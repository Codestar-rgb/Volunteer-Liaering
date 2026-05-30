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
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
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
 * EntityCruxB - 交叉宿主 B 型（Tier 2 高级战斗单位）
 * 
 * 功能：
 * - 重型战斗单位
 * - 具备强力跳跃攻击和范围伤害
 * - 高护甲高生命值
 * - 可进化为更高级的突击形态
 * 
 * @author SubspaceParasite Team
 * @version 1.20.1
 */
public class EntityCruxB extends EntityParasiteBase implements IParasiteEntity {
    
    // ==================== 常量定义 ====================
    
    private static final float BASE_HEALTH = 48.0f;
    private static final float BASE_ATTACK_DAMAGE = 8.5f;
    private static final float BASE_MOVEMENT_SPEED = 0.28f;
    private static final float BASE_ARMOR = 5.0f;
    
    private static final int JUMP_COOLDOWN = 45; // 跳跃冷却时间（tick）
    private static final double JUMP_RANGE = 10.0; // 跳跃攻击触发距离
    private static final double SLAM_RADIUS = 4.0; // 落地冲击波半径
    
    // ==================== 成员变量 ====================
    
    /**
     * 跳跃冷却计时器
     */
    private int jumpCooldown = 0;
    
    /**
     * 是否处于空中
     */
    private boolean wasInAir = false;
    
    // ==================== 构造函数 ====================
    
    public EntityCruxB(EntityType<? extends EntityCruxB> entityType, Level level) {
        super(entityType, level);
        this.phase = EvoPhase.CRUDE;
        this.parasiteType = GeneType.CRUDE_CRUX_B;
        this.xpValue = 20;
    }
    
    // ==================== AI 行为注册 ====================
    
    @Override
    protected void registerGoals() {
        super.registerGoals();
        
        // 基础目标
        this.goalSelector.addGoal(0, new FloatGoal(this));
        
        // 跳跃攻击目标（高优先级）
        this.goalSelector.addGoal(1, new LeapAtTargetGoal(this, 0.7f));
        
        // 近战攻击目标
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.3, true));
        
        // 移动目标
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.0));
        
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
            .add(Attributes.FOLLOW_RANGE, 64.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
            .add(Attributes.JUMP_STRENGTH, 1.0);
    }
    
    // ==================== 生命周期方法 ====================
    
    @Override
    public void tick() {
        super.tick();
        
        boolean inAir = !onGround();
        
        if (!level().isClientSide()) {
            // 更新跳跃冷却
            if (jumpCooldown > 0) {
                jumpCooldown--;
            }
            
            // 检测落地冲击
            if (wasInAir && onGround()) {
                performSlamAttack();
            }
            
            wasInAir = inAir;
            
            // 检查是否进行跳跃攻击
            checkJumpAttack();
        } else {
            wasInAir = inAir;
        }
    }
    
    @Override
    public void aiStep() {
        super.aiStep();
        
        // 精英单位增益
        if (!level().isClientSide()) {
            applyEliteBonuses();
        }
    }
    
    // ==================== 跳跃攻击逻辑 ====================
    
    /**
     * 检查是否进行跳跃攻击
     */
    private void checkJumpAttack() {
        if (jumpCooldown > 0) return;
        
        LivingEntity target = getTarget();
        if (target == null) return;
        
        double distanceToTarget = distanceToSqr(target);
        if (distanceToTarget <= JUMP_RANGE * JUMP_RANGE && distanceToTarget > 3.0 * 3.0) {
            // 执行跳跃攻击
            performJumpAttack(target);
            jumpCooldown = JUMP_COOLDOWN;
        }
    }
    
    /**
     * 执行跳跃攻击
     */
    private void performJumpAttack(LivingEntity target) {
        double dx = target.getX() - getX();
        double dz = target.getZ() - getZ();
        double distance = Math.sqrt(dx * dx + dz * dz);
        
        if (distance > 0) {
            // 设置更高的跳跃速度
            setDeltaMovement(getDeltaMovement().add(
                dx / distance * 0.6,
                0.7,
                dz / distance * 0.6
            ));
            hasImpulse = true;
        }
    }
    
    /**
     * 执行落地冲击攻击
     */
    private void performSlamAttack() {
        if (!level().isClientSide()) {
            // 对周围生物造成伤害
            var nearbyEntities = level().getEntitiesOfClass(LivingEntity.class, 
                getBoundingBox().inflate(SLAM_RADIUS));
            
            for (LivingEntity entity : nearbyEntities) {
                if (entity != this && !(entity instanceof IParasiteEntity)) {
                    double distance = distanceToSqr(entity);
                    if (distance <= SLAM_RADIUS * SLAM_RADIUS) {
                        // 计算伤害（距离越近伤害越高）
                        float damageRatio = 1.0f - (float)Math.sqrt(distance) / SLAM_RADIUS;
                        float slamDamage = BASE_ATTACK_DAMAGE * 0.8f * damageRatio;
                        
                        if (slamDamage > 0) {
                            entity.hurt(damageSources().fall(), slamDamage);
                            
                            // 击退效果
                            double knockbackStrength = 0.5 * damageRatio;
                            entity.setDeltaMovement(entity.getDeltaMovement().add(
                                (entity.getX() - getX()) * 0.1 * knockbackStrength,
                                knockbackStrength,
                                (entity.getZ() - getZ()) * 0.1 * knockbackStrength
                            ));
                        }
                    }
                }
            }
            
            // 播放冲击粒子效果
            spawnSlamParticles();
        }
    }
    
    /**
     * 生成冲击粒子效果
     */
    private void spawnSlamParticles() {
        // TODO: 实现冲击波粒子效果
        // level().sendParticles(ParticleTypes.EXPLOSION, getX(), getY(), getZ(), 10, 2.0, 1.0, 2.0, 0.1);
    }
    
    // ==================== 精英增益 ====================
    
    /**
     * 应用精英单位增益
     */
    private void applyEliteBonuses() {
        // 低生命值时获得攻击力提升
        if (getHealth() < getMaxHealth() * 0.3f) {
            // TODO: 实现狂暴状态属性修饰符
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
            
            // 重击造成额外击退
            entity.setDeltaMovement(entity.getDeltaMovement().add(
                0,
                0.3,
                0
            ));
            
            return true;
        }
        return false;
    }
    
    /**
     * 尝试感染目标
     */
    private void attemptInfection(LivingEntity target) {
        float infectionChance = 0.4f; // 40% 基础感染几率
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
        // TODO: 使用 ModSounds.CRUX_AMBIENT
        return net.minecraft.sounds.SoundEvents.RAVAGER_AMBIENT;
    }
    
    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        // TODO: 使用 ModSounds.CRUX_HURT
        return net.minecraft.sounds.SoundEvents.RAVAGER_HURT;
    }
    
    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        // TODO: 使用 ModSounds.CRUX_DEATH
        return net.minecraft.sounds.SoundEvents.RAVAGER_DEATH;
    }
    
    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(net.minecraft.sounds.SoundEvents.RAVAGER_STEP, 0.3F, 0.4F);
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
