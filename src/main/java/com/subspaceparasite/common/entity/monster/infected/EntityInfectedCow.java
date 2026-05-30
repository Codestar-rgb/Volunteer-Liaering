package com.subspaceparasite.common.entity.monster.infected;

import com.subspaceparasite.api.capability.component.CombatComponent;
import com.subspaceparasite.api.capability.component.EvolutionComponent;
import com.subspaceparasite.api.capability.component.InfectionComponent;
import com.subspaceparasite.api.entity.IParasiteEntity;
import com.subspaceparasite.api.entity.ParasiteType;
import com.subspaceparasite.api.enums.EvoPhase;
import com.subspaceparasite.common.ai.AIParasiteAttack;
import com.subspaceparasite.common.ai.AIParasiteFollowOwner;
import com.subspaceparasite.common.ai.AIParasiteLookAtEntity;
import com.subspaceparasite.common.ai.AIParasiteRandomStroll;
import com.subspaceparasite.common.entity.monster.EntityParasiteBase;
import com.subspaceparasite.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * EntityInfectedCow - 基础感染者牛
 * 
 * 特性：
 * - 被寄生虫感染的牛转化而来
 * - 保留牛的体型特征，但行为完全受寄生虫控制
 * - 移动速度较慢，血量较高
 * - 死亡时传播感染给附近生物
 * 
 * @author SubspaceParasite Team
 * @version 1.20.1
 */
public class EntityInfectedCow extends EntityParasiteBase implements IParasiteEntity {
    
    // ==================== 常量定义 ====================
    
    private static final float BASE_HEALTH = 30.0f;
    private static final float ATTACK_DAMAGE = 4.0f;
    private static final float MOVEMENT_SPEED = 0.18f;
    private static final float FOLLOW_RANGE = 18.0f;
    private static final float ARMOR = 2.0f;
    private static final float KNOCKBACK_RESISTANCE = 0.15f;
    
    private static final int XP_VALUE = 6;
    private static final int MAX_INFECTED_ON_DEATH = 4;
    private static final float INFECTION_RADIUS = 7.0f;
    
    // ==================== 构造函数 ====================
    
    public EntityInfectedCow(EntityType<? extends EntityParasiteBase> entityType, Level level) {
        super(entityType, level);
        this.setPhase(EvoPhase.INFECTED);
        this.setParasiteType(ParasiteType.INF_COW);
        this.xpValue = XP_VALUE;
    }
    
    // ==================== AI 行为注册 ====================
    
    @Override
    protected void registerGoals() {
        super.registerGoals();
        
        // 目标选择器（攻击优先级）
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Animal.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Monster.class, false));
        
        // 任务选择器（行为模式）
        this.goalSelector.addGoal(1, new AIParasiteAttack(this, 0.8, true));
        this.goalSelector.addGoal(2, new AIParasiteFollowOwner(this, 0.9, 8.0F, 4.0F));
        this.goalSelector.addGoal(3, new AIParasiteLookAtEntity(this, Player.class, 6.0F));
        this.goalSelector.addGoal(4, new AIParasiteRandomStroll(this, 0.8));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }
    
    // ==================== 属性配置 ====================
    
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, BASE_HEALTH)
            .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
            .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE)
            .add(Attributes.FOLLOW_RANGE, FOLLOW_RANGE)
            .add(Attributes.ARMOR, ARMOR)
            .add(Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE);
    }
    
    // ==================== 音效系统 ====================
    
    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return ModSounds.SUBSRP_ENTITY_INFECTED_COW_AMBIENT.get();
    }
    
    @Override
    protected @Nullable SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return ModSounds.SUBSRP_ENTITY_INFECTED_COW_HURT.get();
    }
    
    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return ModSounds.SUBSRP_ENTITY_INFECTED_COW_DEATH.get();
    }
    
    // ==================== 环境交互 ====================
    
    @Override
    protected void checkFallDamage(double y, boolean onGround, @NotNull BlockState state, @NotNull BlockPos pos) {
        // 感染者牛有较厚的脂肪层，减免跌落伤害
        if (y > 3.0) {
            y = y * 0.6;
        }
        super.checkFallDamage(y, onGround, state, pos);
    }
    
    @Override
    public boolean canBreatheUnderwater() {
        return false; // 牛型感染者不能在水下呼吸
    }
    
    @Override
    public boolean isPushedByFluid() {
        return true; // 可被流体推动
    }
    
    // ==================== 感染传播机制 ====================
    
    @Override
    public void die(@NotNull DamageSource damageSource) {
        if (!this.level().isClientSide()) {
            // 死亡时传播感染
            this.spreadInfectionOnDeath();
        }
        super.die(damageSource);
    }
    
    /**
     * 死亡时传播感染给附近生物
     */
    private void spreadInfectionOnDeath() {
        InfectionComponent infectionComponent = this.getInfectionComponent();
        if (infectionComponent != null) {
            infectionComponent.spreadInfection(this.level(), this.blockPosition(), INFECTION_RADIUS, MAX_INFECTED_ON_DEATH);
        }
    }
    
    // ==================== 组件访问器 ====================
    
    @Override
    public CombatComponent getCombatComponent() {
        return this.getCapability(CombatComponent.CAPABILITY).orElse(null);
    }
    
    @Override
    public EvolutionComponent getEvolutionComponent() {
        return this.getCapability(EvolutionComponent.CAPABILITY).orElse(null);
    }
    
    @Override
    public InfectionComponent getInfectionComponent() {
        return this.getCapability(InfectionComponent.CAPABILITY).orElse(null);
    }
    
    // ==================== 寄生特性 ====================
    
    @Override
    public boolean isParasite() {
        return true;
    }
    
    @Override
    public boolean canBeInfected() {
        return false; // 已经是感染者，不再被感染
    }
    
    @Override
    public boolean shouldDropLoot() {
        return false; // 感染者不掉落原版物品
    }
    
    // ==================== 繁殖与生成 ====================
    
    @Override
    public boolean canSpawn(ServerLevelAccessor level, SpawnReason spawnReason) {
        return true; // 可在任何维度生成
    }
    
    @Override
    public CheckSpawnResult checkSpawnRules(ServerLevelAccessor level, SpawnReason spawnReason) {
        return CheckSpawnResult.SUCCESS;
    }
    
    @Override
    public boolean checkSpawnObstruction(ServerLevelAccessor level) {
        return level.isUnobstructed(this);
    }
    
    // ==================== 其他方法 ====================
    
    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
    }
    
    @Override
    public void aiStep() {
        super.aiStep();
    }
    
    @Override
    public void tick() {
        super.tick();
    }
}
