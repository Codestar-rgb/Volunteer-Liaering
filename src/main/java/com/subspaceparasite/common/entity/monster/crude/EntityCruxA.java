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
 * EntityCruxA - 交叉宿主A型（Tier 2 基础战斗单位）
 * 
 * 功能：
 * - 中型战斗单位
 * - 具备跳跃攻击能力
 * - 群体协作攻击
 * - 可进化为更高级的战斗形态
 * 
 * @author SubspaceParasite Team
 * @version 1.20.1
 */
public class EntityCruxA extends EntityParasiteBase implements IParasiteEntity {
    
    // ==================== 常量定义 ====================
    
    private static final float BASE_HEALTH = 35.0f;
    private static final float BASE_ATTACK_DAMAGE = 6.5f;
    private static final float BASE_MOVEMENT_SPEED = 0.32f;
    private static final float BASE_ARMOR = 3.5f;
    
    private static final int JUMP_COOLDOWN = 30; // 跳跃冷却时间（tick）
    private static final double JUMP_RANGE = 8.0; // 跳跃攻击触发距离
    
    // ==================== 成员变量 ====================
    
    /**
     * 跳跃冷却计时器
     */
    private int jumpCooldown = 0;
    
    // ==================== 构造函数 ====================
    
    public EntityCruxA(EntityType<? extends EntityCruxA> entityType, Level level) {
        super(entityType, level);
        this.phase = EvoPhase.CRUDE;
        this.parasiteType = GeneType.CRUDE_CRUX_A;
        this.xpValue = 15;
    }
    
    // ==================== AI 行为注册 ====================
    
    @Override
    protected void registerGoals() {
        super.registerGoals();
        
        // 基础目标
        this.goalSelector.addGoal(0, new FloatGoal(this));
        
        // 跳跃攻击目标（高优先级）
        this.goalSelector.addGoal(1, new LeapAtTargetGoal(this, 0.6f));
        
        // 近战攻击目标
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.4, true));
        
        // 移动目标
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.2));
        
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
            .add(Attributes.FOLLOW_RANGE, 56.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.25)
            .add(Attributes.JUMP_STRENGTH, 0.8);
    }
    
    // ==================== 生命周期方法 ====================
    
    @Override
    public void tick() {
        super.tick();
        
        if (!level().isClientSide()) {
            // 更新跳跃冷却
            if (jumpCooldown > 0) {
                jumpCooldown--;
            }
            
            // 检查是否进行跳跃攻击
            checkJumpAttack();
        }
    }
    
    @Override
    public void aiStep() {
        super.aiStep();
        
        // 群体协作逻辑
        if (!level().isClientSide()) {
            applyPackBonuses();
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
        if (distanceToTarget <= JUMP_RANGE * JUMP_RANGE && distanceToTarget > 2.0 * 2.0) {
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
            // 设置跳跃速度
            setDeltaMovement(getDeltaMovement().add(
                dx / distance * 0.5,
                0.5,
                dz / distance * 0.5
            ));
            hasImpulse = true;
        }
    }
    
    // ==================== 群体协作 ====================
    
    /**
     * 应用群体协作增益
     */
    private void applyPackBonuses() {
        // 检测周围同类数量
        var nearbyAllies = level().getEntitiesOfClass(EntityCruxA.class, 
            getBoundingBox().inflate(12.0));
        
        if (nearbyAllies.size() >= 3) {
            // 3个以上同类时获得攻击力提升
            // TODO: 实现临时属性修饰符系统
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
            
            // 跳跃攻击造成额外击退
            if (jumpCooldown == JUMP_COOLDOWN - 1) {
                entity.setDeltaMovement(entity.getDeltaMovement().add(
                    0,
                    0.4,
                    0
                ));
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
        // TODO: 使用ModSounds.CRUX_AMBIENT
        return net.minecraft.sounds.SoundEvents.HUSK_AMBIENT;
    }
    
    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        // TODO: 使用ModSounds.CRUX_HURT
        return net.minecraft.sounds.SoundEvents.HUSK_HURT;
    }
    
    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        // TODO: 使用ModSounds.CRUX_DEATH
        return net.minecraft.sounds.SoundEvents.HUSK_DEATH;
    }
    
    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(net.minecraft.sounds.SoundEvents.HUSK_STEP, 0.2F, 0.5F);
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
