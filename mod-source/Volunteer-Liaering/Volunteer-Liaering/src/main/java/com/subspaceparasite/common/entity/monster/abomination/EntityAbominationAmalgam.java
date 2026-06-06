package com.subspaceparasite.common.entity.monster.abomination;

import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityAbominationBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * EntityAbominationAmalgam - Abomination Amalgam
 * Parasite entity of the abomination tier.
 */
public class EntityAbominationAmalgam extends EntityAbominationBase {

    public EntityAbominationAmalgam(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.parasiteType = ParasiteType.ABO_BODIES;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityAbominationBase.createAttributes()
                .add(Attributes.MAX_HEALTH, 600.0)
                .add(Attributes.ATTACK_DAMAGE, 24.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ARMOR, 16.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }
}
