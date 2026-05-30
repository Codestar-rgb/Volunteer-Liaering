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
 * EntityHeblu - Derived 阶段寄生虫实体
 * 
 * 特性：
 * - Derived 阶段的代表性生物，具有强大的远程攻击能力
 * - 能够发射酸性投射物腐蚀目标
 * - 中等体型，高机动性，中等血量
 * - 具备殖民地意识，会保护附近的殖民地结构
 * 
 * @author SubspaceParasite Team
 * @version 1.20.1
 */
public class EntityHeblu extends EntityParasiteBase implements IParasiteEntity {
    
    // ==================== 常量定义 ====================
    
    private static final float BASE_HEALTH = 80.0f;
    private static final float ATTACK_DAMAGE = 8.0f;
    private static final float MOVEMENT_SPEED = 0.32f;
    private static final float FOLLOW_RANGE = 32.0f;
    private static final float ARMOR = 6.0f;
    private static final float KNOCKBACK_RESISTANCE = 0.35f;
    
    private static final int XP_VALUE = 15;
    private static final float ACID_ATTACK_RANGE = 24.0f;
    private static final int ACID_DAMAGE = 6;
    
    // ==================== 构造函数 ====================
    
    public EntityHeblu(EntityType<? extends EntityParasiteBase> entityType, Level level) {
        super(entityType, level);
        this.setPhase(EvoPhase.DERIVED);
        this.setParasiteType(ParasiteType.DER_HEBLU);
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
        this.goalSelector.addGoal(1, new AIParasiteAttack(this, 1.0, false)); // 不近战，主要远程
        this.goalSelector.addGoal(2, new AIParasiteFollowOwner(this, 1.1, 16.0F, 8.0F));
        this.goalSelector.addGoal(3, new AIParasiteLookAtEntity(this, Player.class, 12.0F));
        this.goalSelector.addGoal(4, new AIParasiteRandomStroll(this, 1.1));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.1));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 12.0F));
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
        return ModSounds.SUBSRP_ENTITY_HEBLU_AMBIENT.get();
    }
    
    @Override
    protected @Nullable SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return ModSounds.SUBSRP_ENTITY_HEBLU_HURT.get();
    }
    
    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return ModSounds.SUBSRP_ENTITY_HEBLU_DEATH.get();
    }
    
    // ==================== 环境交互 ====================
    
    @Override
    protected void checkFallDamage(double y, boolean onGround, @NotNull BlockState state, @NotNull BlockPos pos) {
        // Derived 阶段有强化的外骨骼，减免跌落伤害
        if (y > 5.0) {
            y = y * 0.4;
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
            // EvolutionComponent evolutionComponent = this.getCapability(EvolutionComponent.CAPABILITY)...
            // if (evolutionComponent.shouldEvolveToAdapted()) { ... }
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
        // 服务器端 AI 逻辑更新
    }
    
    @Override
    public void aiStep() {
        super.aiStep();
        // 客户端和服务端通用的 AI 逻辑
    }
    
    @Override
    public void tick() {
        super.tick();
        // 每刻更新逻辑
    }
}
