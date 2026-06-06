package com.subspaceparasite.common.entity.monster.adapted;

import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityAdaptedBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * EntityCreeperAdapted - Adapted Creeper
 * Parasite entity of the adapted tier.
 */
public class EntityCreeperAdapted extends EntityAdaptedBase {

    public EntityCreeperAdapted(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        // TODO: Add ParasiteType.ADA_CREEPER to ParasiteType enum
        // this.parasiteType = ParasiteType.ADA_CREEPER;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityAdaptedBase.createAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ARMOR, 0.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }
}
