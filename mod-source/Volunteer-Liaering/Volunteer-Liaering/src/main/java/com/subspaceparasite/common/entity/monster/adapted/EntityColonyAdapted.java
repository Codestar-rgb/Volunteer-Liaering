package com.subspaceparasite.common.entity.monster.adapted;

import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityAdaptedBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * EntityColonyAdapted - Adapted Colony
 * Parasite entity of the adapted tier.
 */
public class EntityColonyAdapted extends EntityAdaptedBase {

    public EntityColonyAdapted(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.parasiteType = ParasiteType.ADA_BOLSTER;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityAdaptedBase.createAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }
}
