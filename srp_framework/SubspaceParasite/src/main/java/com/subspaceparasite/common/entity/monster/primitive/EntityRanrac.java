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

/**
 * EntityRanrac - Primitive stage large brute parasite.
 * <p>
 * Original SRP 1.12.2: EntityRanrac in primitive stage.
 * Modernized for Forge 1.20.1 with component-based architecture.
 * <p>
 * Stats (configurable):
 * - Health: 80.0
 * - Attack Damage: 12.0
 * - Speed: 0.15
 * - Armor: 6.0
 * - Knockback Resistance: High
 * <p>
 * Behavior:
 * - Heavy melee attacker with high durability
 * - Slow but powerful strikes
 * - Evolves to RanracAdapted after reaching kill threshold
 */
public class EntityRanrac extends EntityParasiteBase {

    private static final double BASE_HEALTH = 80.0;
    private static final double BASE_ATTACK_DAMAGE = 12.0;
    private static final double BASE_SPEED = 0.15;
    private static final double BASE_ARMOR = 6.0;
    private static final double BASE_KNOCKBACK_RESISTANCE = 0.7;

    public EntityRanrac(EntityType<? extends EntityRanrac> type, Level world) {
        super(type, world);
        this.xpReward = 20;
        this.setCanPickUpLoot(false);
        this.phaseCreated = EvoPhase.ONE;
        this.parasiteType = ParasiteType.PRI_RANRAC;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ParasiteMeleeAttackGoal(this, 0.9, true));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void knockback(double strength, double ratioX, double ratioZ) {
        super.knockback(strength * (1.0 - BASE_KNOCKBACK_RESISTANCE), ratioX, ratioZ);
    }

    @Override
    protected SoundEvent getAmbientSound() { 
        return ModSounds.SUBSRP_ENTITY_RANRAC_AMBIENT.get(); 
    }
    
    @Override
    protected SoundEvent getHurtSound(DamageSource source) { 
        return ModSounds.SUBSRP_ENTITY_RANRAC_HURT.get(); 
    }
    
    @Override
    protected SoundEvent getDeathSound() { 
        return ModSounds.SUBSRP_ENTITY_RANRAC_DEATH.get(); 
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, BASE_HEALTH)
            .add(Attributes.MOVEMENT_SPEED, BASE_SPEED)
            .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
            .add(Attributes.ARMOR, BASE_ARMOR)
            .add(Attributes.KNOCKBACK_RESISTANCE, BASE_KNOCKBACK_RESISTANCE);
    }

    @Override
    public String getTextureName() { 
        return "textures/entity/primitive/subsrp_ranrac.png"; 
    }
    
    @Override
    public String getModelName() { 
        return "ranrac"; 
    }
}
