package com.subspaceparasite.common.entity.monster.infected;

import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityInfectedBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * EntityInfectedBat - Infected Bat
 * Parasite entity of the infected tier.
 */
public class EntityInfectedBat extends EntityInfectedBase {

    public EntityInfectedBat(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.parasiteType = ParasiteType.SIM_SQUID;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityInfectedBase.createAttributes()
                .add(Attributes.MAX_HEALTH, 8.0)
                .add(Attributes.ATTACK_DAMAGE, 1.0)
                .add(Attributes.MOVEMENT_SPEED, 0.40)
                .add(Attributes.ARMOR, 0.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }
}
