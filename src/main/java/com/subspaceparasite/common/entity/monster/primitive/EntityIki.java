package com.subspaceparasite.common.entity.monster.primitive;

import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.common.entity.ai.ParasiteMeleeAttackGoal;
import com.subspaceparasite.core.ModSounds;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class EntityIki extends EntityParasiteBase {
    private static final double BASE_HEALTH = 48.0;
    private static final double BASE_ATTACK_DAMAGE = 6.5;
    private static final double BASE_SPEED = 0.21;
    private static final double BASE_ARMOR = 3.6;
    
    public EntityIki(EntityType<? extends EntityIki> type, Level world) {
        super(type, world);
        this.xpReward = 10;
        this.setCanPickUpLoot(false);
        this.phaseCreated = EvoPhase.ONE;
        this.parasiteType = ParasiteType.PRI_IKI;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ParasiteMeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.85));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected SoundEvent getAmbientSound() { return ModSounds.SUBSRP_ENTITY_IKI_AMBIENT.get(); }
    @Override
    protected SoundEvent getHurtSound(DamageSource source) { return ModSounds.SUBSRP_ENTITY_IKI_HURT.get(); }
    @Override
    protected SoundEvent getDeathSound() { return ModSounds.SUBSRP_ENTITY_IKI_DEATH.get(); }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, BASE_HEALTH)
            .add(Attributes.MOVEMENT_SPEED, BASE_SPEED)
            .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
            .add(Attributes.ARMOR, BASE_ARMOR);
    }

    @Override
    public String getTextureName() { return "textures/entity/primitive/subsrp_iki.png"; }
    @Override
    public String getModelName() { return "iki"; }
}
