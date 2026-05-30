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
 * EntityNogla - Primitive stage flying scout parasite.
 * <p>
 * Original SRP 1.12.2: EntityNogla in primitive stage.
 * Modernized for Forge 1.20.1 with component-based architecture.
 * <p>
 * Stats (configurable):
 * - Health: 35.0
 * - Attack Damage: 4.0
 * - Speed: 0.28 (flying movement)
 * - Armor: 2.0
 * <p>
 * Behavior:
 * - Flying mobility (can traverse over obstacles)
 * - Scout behavior - detects targets from long range
 * - Evolves to NoglaAdapted after reaching kill threshold
 */
public class EntityNogla extends EntityParasiteBase {

    private static final double BASE_HEALTH = 35.0;
    private static final double BASE_ATTACK_DAMAGE = 4.0;
    private static final double BASE_SPEED = 0.28;
    private static final double BASE_ARMOR = 2.0;

    public EntityNogla(EntityType<? extends EntityNogla> type, Level world) {
        super(type, world);
        this.xpReward = 10;
        this.setCanPickUpLoot(false);
        this.phaseCreated = EvoPhase.ONE;
        this.parasiteType = ParasiteType.PRI_NOGLA;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ParasiteMeleeAttackGoal(this, 1.2, true));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected SoundEvent getAmbientSound() { 
        return ModSounds.SUBSRP_ENTITY_NOGLA_AMBIENT.get(); 
    }
    
    @Override
    protected SoundEvent getHurtSound(DamageSource source) { 
        return ModSounds.SUBSRP_ENTITY_NOGLA_HURT.get(); 
    }
    
    @Override
    protected SoundEvent getDeathSound() { 
        return ModSounds.SUBSRP_ENTITY_NOGLA_DEATH.get(); 
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
        return "textures/entity/primitive/subsrp_nogla.png"; 
    }
    
    @Override
    public String getModelName() { 
        return "nogla"; 
    }
}
