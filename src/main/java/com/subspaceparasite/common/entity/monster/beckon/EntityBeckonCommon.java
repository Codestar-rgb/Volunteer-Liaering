package com.subspaceparasite.common.entity.monster.beckon;

import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityBeckonBase;
import com.subspaceparasite.core.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * EntityBeckonCommon - Beckon Stage I
 * Parasite entity of the beckon tier.
 */
public class EntityBeckonCommon extends EntityBeckonBase {

    public EntityBeckonCommon(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.parasiteType = ParasiteType.BECKON_SI;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.BECKON_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.BECKON_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.BECKON_DEATH.get();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityBeckonBase.createAttributes()
                .add(Attributes.MAX_HEALTH, 80.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }
}
