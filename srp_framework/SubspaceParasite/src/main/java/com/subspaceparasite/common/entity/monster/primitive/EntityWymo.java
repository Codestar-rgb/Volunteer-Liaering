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
 * EntityWymo - Primitive stage small agile parasite.
 * <p>
 * Original SRP 1.12.2: EntityWymo in primitive stage.
 * Modernized for Forge 1.20.1 with component-based architecture.
 * <p>
 * Stats (configurable):
 * - Health: 30.0
 * - Attack Damage: 5.0
 * - Speed: 0.30
 * - Armor: 2.5
 * <p>
 * Behavior:
 * - Fast and agile hunter
 * - Pack behavior when multiple Wymo are present
 * - Evolves to WymoAdapted after reaching kill threshold
 */
public class EntityWymo extends EntityParasiteBase {

    private static final double BASE_HEALTH = 30.0;
    private static final double BASE_ATTACK_DAMAGE = 5.0;
    private static final double BASE_SPEED = 0.30;
    private static final double BASE_ARMOR = 2.5;

    public EntityWymo(EntityType<? extends EntityWymo> type, Level world) {
        super(type, world);
        this.xpReward = 8;
        this.setCanPickUpLoot(false);
        this.phaseCreated = EvoPhase.ONE;
        this.parasiteType = ParasiteType.PRI_WYMO;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ParasiteMeleeAttackGoal(this, 1.3, true));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.1));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected SoundEvent getAmbientSound() { 
        return ModSounds.SUBSRP_ENTITY_WYMO_AMBIENT.get(); 
    }
    
    @Override
    protected SoundEvent getHurtSound(DamageSource source) { 
        return ModSounds.SUBSRP_ENTITY_WYMO_HURT.get(); 
    }
    
    @Override
    protected SoundEvent getDeathSound() { 
        return ModSounds.SUBSRP_ENTITY_WYMO_DEATH.get(); 
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, BASE_HEALTH)
            .add(Attributes.MOVEMENT_SPEED, BASE_SPEED)
            .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
            .add(Attributes.ARMOR, BASE_ARMOR);
    }

    @Override
    public String getTextureName() { 
        return "textures/entity/primitive/subsrp_wymo.png"; 
    }
    
    @Override
    public String getModelName() { 
        return "wymo"; 
    }
}
