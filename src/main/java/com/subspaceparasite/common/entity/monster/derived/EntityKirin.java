package com.subspaceparasite.common.entity.monster.derived;

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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * EntityKirin - Derived 阶段寄生虫实体
 * 
 * 特性：
 * - Derived 阶段的另一代表性生物，具有强大的近战能力
 * - 高机动性，能够快速冲锋攻击目标
 * - 具备跳跃攻击和范围伤害能力
 * - 对殖民地有强烈的保护意识
 * 
 * @author SubspaceParasite Team
 * @version 1.20.1
 */
public class EntityKirin extends EntityParasiteBase implements IParasiteEntity {
    
    // ==================== 常量定义 ====================
    
    private static final float BASE_HEALTH = 90.0f;
    private static final float ATTACK_DAMAGE = 10.0f;
    private static final float MOVEMENT_SPEED = 0.38f;
    private static final float FOLLOW_RANGE = 28.0f;
    private static final float ARMOR = 7.0f;
    private static final float KNOCKBACK_RESISTANCE = 0.4f;
    
    private static final int XP_VALUE = 16;
    private static final float CHARGE_ATTACK_RANGE = 16.0f;
    private static final float JUMP_ATTACK_HEIGHT = 3.0f;
    
    // ==================== 构造函数 ====================
    
    public EntityKirin(EntityType<? extends EntityParasiteBase> entityType, Level level) {
        super(entityType, level);
        this.setPhase(EvoPhase.DERIVED);
        this.setParasiteType(ParasiteType.DER_KIRIN);
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
        this.goalSelector.addGoal(1, new AIParasiteAttack(this, 1.2, true)); // 近战为主
        this.goalSelector.addGoal(2, new AIParasiteFollowOwner(this, 1.3, 14.0F, 7.0F));
        this.goalSelector.addGoal(3, new AIParasiteLookAtEntity(this, Player.class, 10.0F));
        this.goalSelector.addGoal(4, new AIParasiteRandomStroll(this, 1.2));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.2));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 10.0F));
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
            .add(Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE)
            .add(Attributes.JUMP_STRENGTH, 1.2); // 强化跳跃能力
    }
    
    // ==================== 音效系统 ====================
    
    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return ModSounds.SUBSRP_ENTITY_KIRIN_AMBIENT.get();
    }
    
    @Override
    protected @Nullable SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return ModSounds.SUBSRP_ENTITY_KIRIN_HURT.get();
    }
    
    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return ModSounds.SUBSRP_ENTITY_KIRIN_DEATH.get();
    }
    
    // ==================== 环境交互 ====================
    
    @Override
    protected void checkFallDamage(double y, boolean onGround, @NotNull BlockState state, @NotNull BlockPos pos) {
        // Derived 阶段有强化的外骨骼和肌肉，大幅减免跌落伤害
        if (y > 6.0) {
            y = y * 0.3;
        }
        super.checkFallDamage(y, onGround, state, pos);
    }
    
    @Override
    public boolean canBreatheUnderwater() {
        return true; // 高级寄生虫可在水下呼吸
    }
    
    @Override
    public boolean isPushedByFluid() {
        return false; // 不被流体推动
    }
    
    // ==================== 感染传播机制 ====================
    
    @Override
    public void die(@NotNull DamageSource damageSource) {
        if (!this.level().isClientSide()) {
            // TODO: 调用 Dispatcher 检查是否触发进化到 Adapted 阶段
        }
        super.die(damageSource);
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
        return false; // 已经是高级寄生虫，不再被感染
    }
    
    @Override
    public boolean shouldDropLoot() {
        return true; // Derived 阶段可能掉落特殊物品
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
