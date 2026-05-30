package com.subspaceparasite.common.entity.monster.primitive;

import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.api.capability.component.CombatComponent;
import com.subspaceparasite.api.capability.component.EvolutionComponent;
import com.subspaceparasite.api.capability.component.InfectionComponent;
import com.subspaceparasite.api.entity.IParasiteEntity;
import com.subspaceparasite.api.entity.ParasiteType;
import com.subspaceparasite.api.enums.EvoPhase;
import com.subspaceparasite.api.enums.GeneType;
import com.subspaceparasite.common.ai.AIParasiteAttack;
import com.subspaceparasite.common.ai.AIParasiteFollowOwner;
import com.subspaceparasite.common.ai.AIParasiteLookAtEntity;
import com.subspaceparasite.common.ai.AIParasiteRandomStroll;
import com.subspaceparasite.common.entity.monster.EntityParasiteBase;
import com.subspaceparasite.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * EntityCanra - Primitive阶段寄生虫实体
 * 
 * 特性：
 * - 飞行能力，可在空中悬停和俯冲攻击
 * - 对鸡类生物有特殊仇恨（源于原版感染逻辑）
 * - 高机动性，低血量，群体出现
 * - 死亡时可能传播感染给附近生物
 * 
 * @author SubspaceParasite Team
 * @version 1.20.1
 */
public class EntityCanra extends EntityParasiteBase implements IParasiteEntity {
    
    // ==================== 常量定义 ====================
    
    private static final float BASE_HEALTH = 12.0f;
    private static final float ATTACK_DAMAGE = 3.5f;
    private static final float MOVEMENT_SPEED = 0.28f;
    private static final float FOLLOW_RANGE = 24.0f;
    private static final float ARMOR = 2.0f;
    private static final float KNOCKBACK_RESISTANCE = 0.1f;
    
    private static final int XP_VALUE = 6;
    private static final int MAX_INFECTED_ON_DEATH = 2;
    private static final float INFECTION_RADIUS = 6.0f;
    
    // ==================== 构造函数 ====================
    
    public EntityCanra(EntityType<? extends EntityParasiteBase> entityType, Level level) {
        super(entityType, level);
        this.setPhase(EvoPhase.PRIMITIVE);
        this.setParasiteType(ParasiteType.PRIM_CANRA);
        this.xpValue = XP_VALUE;
    }
    
    // ==================== AI行为注册 ====================
    
    @Override
    protected void registerGoals() {
        super.registerGoals();
        
        // 目标选择器（攻击优先级）
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Animal.class, true, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Chicken.class, true, true)); // 优先攻击鸡
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, true, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Monster.class, false, false));
        
        // 行为选择器
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AIParasiteAttack<>(this, 1.0, true));
        this.goalSelector.addGoal(2, new AIParasiteFollowOwner(this, 1.2, 10.0f, 2.0f));
        this.goalSelector.addGoal(3, new FlyGoal(this)); // 飞行AI
        this.goalSelector.addGoal(4, new AIParasiteLookAtEntity(this, Player.class, 8.0f));
        this.goalSelector.addGoal(5, new AIParasiteRandomStroll(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }
    
    // ==================== 属性配置 ====================
    
    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.FOLLOW_RANGE, FOLLOW_RANGE)
            .add(Attributes.MAX_HEALTH, BASE_HEALTH)
            .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
            .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE)
            .add(Attributes.ARMOR, ARMOR)
            .add(Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE)
            .add(Attributes.FLYING_SPEED, MOVEMENT_SPEED * 1.3f); // 飞行速度略快
    }
    
    // ==================== 生命周期方法 ====================
    
    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level,
                                        @NotNull DifficultyInstance difficulty,
                                        @NotNull MobSpawnType spawnType,
                                        @Nullable SpawnGroupData spawnData,
                                        @Nullable net.minecraft.nbt.CompoundTag spawnTag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnData, spawnTag);
        
        // 初始化组件
        if (this.getCapability(CombatComponent.COMPONENT_CAPABILITY).isPresent()) {
            this.getCapability(CombatComponent.COMPONENT_CAPABILITY)
                .ifPresent(combat -> combat.initialize(BASE_HEALTH, ATTACK_DAMAGE, ARMOR));
        }
        
        if (this.getCapability(EvolutionComponent.COMPONENT_CAPABILITY).isPresent()) {
            this.getCapability(EvolutionComponent.COMPONENT_CAPABILITY)
                .ifPresent(evo -> evo.initialize(EvoPhase.PRIMITIVE, ParasiteType.PRIM_CANRA, GeneType.NONE));
        }
        
        if (this.getCapability(InfectionComponent.COMPONENT_CAPABILITY).isPresent()) {
            this.getCapability(InfectionComponent.COMPONENT_CAPABILITY)
                .ifPresent(inf -> inf.initialize(true, 0.6f)); // 60%感染概率
        }
        
        return data;
    }
    
    // ==================== 战斗逻辑 ====================
    
    @Override
    public boolean doHurtTarget(@NotNull Entity target) {
        boolean result = super.doHurtTarget(target);
        
        if (result && !level().isClientSide) {
            // 攻击时有概率传播感染
            if (level().random.nextFloat() < 0.4f) { // 40%传播概率
                attemptInfect(target);
            }
            
            // 对鸡类造成额外伤害
            if (target instanceof Chicken) {
                target.hurt(damageSources().mobAttack(this), ATTACK_DAMAGE * 1.5f);
            }
        }
        
        return result;
    }
    
    @Override
    public void die(@NotNull DamageSource source) {
        if (!level().isClientSide) {
            // 死亡时尝试感染周围生物
            spreadInfectionOnDeath();
            
            // 播放死亡音效
            playSound(ModSounds.SUBSRP_ENTITY_CANRA_DEATH.get(), 1.0f, 1.0f);
        }
        
        super.die(source);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (!level().isClientSide) {
            // 更新组件状态
            updateComponents();
            
            // 检查进化条件（等待 Dispatcher 系统实现）
            checkEvolutionConditions();
        }
    }
    
    // ==================== 感染传播逻辑 ====================
    
    /**
     * 尝试感染目标生物
     */
    private void attemptInfect(Entity target) {
        if (target instanceof LivingEntity living) {
            InfectionComponent.InfectedResult result = canInfect(living);
            if (result == InfectionComponent.InfectedResult.CAN_INFECT) {
                infect(living);
            }
        }
    }
    
    /**
     * 死亡时传播感染
     */
    private void spreadInfectionOnDeath() {
        var nearbyEntities = level().getEntitiesOfClass(
            LivingEntity.class,
            getBoundingBox().inflate(INFECTION_RADIUS),
            entity -> entity != this && !(entity instanceof IParasiteEntity)
        );
        
        int infectedCount = 0;
        for (LivingEntity nearby : nearbyEntities) {
            if (infectedCount >= MAX_INFECTED_ON_DEATH) break;
            
            if (level().random.nextFloat() < 0.5f) { // 50%概率
                InfectionComponent.InfectedResult result = canInfect(nearby);
                if (result == InfectionComponent.InfectedResult.CAN_INFECT) {
                    infect(nearby);
                    infectedCount++;
                }
            }
        }
        
        SubspaceParasite.LOGGER.debug("EntityCanra died, infected {} entities", infectedCount);
    }
    
    // ==================== 音效系统 ====================
    
    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return ModSounds.SUBSRP_ENTITY_CANRA_AMBIENT.get();
    }
    
    @Override
    protected @Nullable SoundEvent getHurtSound(@NotNull DamageSource source) {
        return ModSounds.SUBSRP_ENTITY_CANRA_HURT.get();
    }
    
    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return ModSounds.SUBSRP_ENTITY_CANRA_DEATH.get();
    }
    
    @Override
    protected void playStepSound(@NotNull BlockPos pos, @NotNull BlockState block) {
        playSound(SoundEvents.BAT_TAKEOFF, 0.15f, 1.0f); // 使用蝙蝠起飞声作为脚步替代
    }
    
    // ==================== 特性方法 ====================
    
    @Override
    public boolean isFlying() {
        return true; // Canra 始终处于飞行状态
    }
    
    @Override
    public boolean canBeLeashed() {
        return false;
    }
    
    @Override
    public boolean requiresCustomPersistence() {
        return true;
    }
    
    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false; // 不因距离消失
    }
    
    @Override
    public int getMaxHeadXRot() {
        return 90; // 允许大幅度转头
    }
    
    // ==================== 进化检查（TODO） ====================
    
    /**
     * 检查进化条件
     * TODO: 等待 EntityDispatcher 系统实现后完善
     */
    private void checkEvolutionConditions() {
        // 预留进化逻辑接口
        // 未来将在此处检查：
        // - 感染计数是否达标
        // - 存活时间是否足够
        // - 是否位于殖民地范围内
        // - 是否满足基因突变条件
    }
    
    // ==================== 数据同步 ====================
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        // 添加 Canra 特有的同步数据（如飞行高度、扑击状态等）
    }
    
    // ==================== NBT 读写 ====================
    
    @Override
    public void addAdditionalSaveData(@NotNull net.minecraft.nbt.CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        // 保存 Canra 特有数据
    }
    
    @Override
    public void readAdditionalSaveData(@NotNull net.minecraft.nbt.CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        // 读取 Canra 特有数据
    }
    
    // ==================== 渲染支持 ====================
    
    /**
     * 获取模型位置
     * TODO: 待集成 GeckoLib/AzureLib 后实现
     */
    @Override
    public String getModelLocation() {
        return SubspaceParasite.MOD_ID + ":geo/canra.geo.json";
    }
    
    /**
     * 获取纹理位置
     */
    @Override
    public String getTextureLocation() {
        return SubspaceParasite.MOD_ID + ":textures/entity/primitive/subsrp_canra.png";
    }
    
    /**
     * 获取动画文件位置
     * TODO: 待提供动画文件后实现
     */
    @Override
    public String getAnimationFileLocation() {
        return SubspaceParasite.MOD_ID + ":animations/canra.animation.json";
    }
}
