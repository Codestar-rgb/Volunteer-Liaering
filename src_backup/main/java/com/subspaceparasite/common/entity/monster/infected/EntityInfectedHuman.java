package com.subspaceparasite.common.entity.monster.infected;

import com.subspaceparasite.api.enums.EvoPhase;
import com.subspaceparasite.api.entity.ParasiteType;
import com.subspaceparasite.common.entity.EntityParasiteBase;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * EntityInfectedHuman - 基础感染者人类
 *
 * 特性：
 * - 被寄生虫感染的人类村民/玩家转化而来
 * - 保留部分原本人形特征，但行为完全受寄生虫控制
 * - 可作为进化链的起点，在满足条件时转化为 Primitive 阶段
 * - 死亡时传播感染给附近生物
 *
 * @author SubspaceParasite Team
 * @version 1.20.1
 */
public class EntityInfectedHuman extends EntityParasiteBase {

    private static final float BASE_HEALTH = 20.0f;
    private static final float ATTACK_DAMAGE = 3.0f;
    private static final float MOVEMENT_SPEED = 0.25f;
    private static final float FOLLOW_RANGE = 20.0f;
    private static final float ARMOR = 1.0f;
    private static final float KNOCKBACK_RESISTANCE = 0.05f;
    private static final int XP_VALUE = 5;

    public EntityInfectedHuman(EntityType<? extends EntityInfectedHuman> entityType, Level level) {
        super(entityType, level);
        this.setPhase(EvoPhase.INFECTED);
        this.setParasiteType(ParasiteType.INF_HUMAN);
        this.xpValue = XP_VALUE;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Animal.class, true));
        this.goalSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, BASE_HEALTH)
            .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
            .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE)
            .add(Attributes.FOLLOW_RANGE, FOLLOW_RANGE)
            .add(Attributes.ARMOR, ARMOR)
            .add(Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE);
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() { return null; }

    @Override
    protected @Nullable SoundEvent getHurtSound(@NotNull DamageSource damageSource) { return null; }

    @Override
    protected @Nullable SoundEvent getDeathSound() { return null; }

    @Override
    protected void checkFallDamage(double y, boolean onGround, @NotNull BlockState state, @NotNull BlockPos pos) {}

    @Override
    public boolean canBreatheUnderwater() { return true; }

    @Override
    public boolean isPushedByFluid() { return false; }

    @Override
    public boolean canBeInfected() { return false; }

    @Override
    public boolean shouldDropLoot() { return false; }

    @Override
    public boolean canSpawn(ServerLevelAccessor level, SpawnReason spawnReason) { return true; }
}
