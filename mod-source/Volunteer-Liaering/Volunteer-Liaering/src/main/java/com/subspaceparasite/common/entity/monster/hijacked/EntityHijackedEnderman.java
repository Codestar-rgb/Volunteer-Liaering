package com.subspaceparasite.common.entity.monster.hijacked;

import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityHijackedBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * EntityHijackedEnderman - Hijacked Enderman
 * Parasite entity of the hijacked tier.
 */
public class EntityHijackedEnderman extends EntityHijackedBase {

    public EntityHijackedEnderman(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.parasiteType = ParasiteType.HI_GOLEM;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityHijackedBase.createAttributes()
                .add(Attributes.MAX_HEALTH, 50.0)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.MOVEMENT_SPEED, 0.30)
                .add(Attributes.ARMOR, 2.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }
}
