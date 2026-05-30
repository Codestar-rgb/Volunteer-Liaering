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
 * EntityShyco - Primitive stage versatile parasite with multiple forms.
 * <p>
 * Original SRP 1.12.2: EntityShyco in primitive stage.
 * Modernized for Forge 1.20.1 with component-based architecture.
 * <p>
 * Stats (configurable):
 * - Health: 50.0
 * - Attack Damage: 7.0
 * - Speed: 0.22
 * - Armor: 3.5
 * <p>
 * Behavior:
 * - Versatile combatant with adaptive capabilities
 * - Multiple texture variants representing different forms
 * - Evolves to ShycoFocused or other adapted forms
 */
public class EntityShyco extends EntityParasiteBase {

    private static final double BASE_HEALTH = 50.0;
    private static final double BASE_ATTACK_DAMAGE = 7.0;
    private static final double BASE_SPEED = 0.22;
    private static final double BASE_ARMOR = 3.5;

    public EntityShyco(EntityType<? extends EntityShyco> type, Level world) {
        super(type, world);
        this.xpReward = 12;
        this.setCanPickUpLoot(false);
        this.phaseCreated = EvoPhase.ONE;
        this.parasiteType = ParasiteType.PRI_SHYCO;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ParasiteMeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected SoundEvent getAmbientSound() { 
        return ModSounds.SUBSRP_ENTITY_SHYCO_AMBIENT.get(); 
    }
    
    @Override
    protected SoundEvent getHurtSound(DamageSource source) { 
        return ModSounds.SUBSRP_ENTITY_SHYCO_HURT.get(); 
    }
    
    @Override
    protected SoundEvent getDeathSound() { 
        return ModSounds.SUBSRP_ENTITY_SHYCO_DEATH.get(); 
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
        return "textures/entity/primitive/subsrp_shyco.png"; 
    }
    
    @Override
    public String getModelName() { 
        return "shyco"; 
    }
}
