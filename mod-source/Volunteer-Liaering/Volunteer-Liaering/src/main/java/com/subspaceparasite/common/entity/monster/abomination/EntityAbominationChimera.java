package com.subspaceparasite.common.entity.monster.abomination;

import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityAbominationBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * EntityAbominationChimera - Abomination Chimera
 * Parasite entity of the abomination tier.
 */
public class EntityAbominationChimera extends EntityAbominationBase {

    public EntityAbominationChimera(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.parasiteType = ParasiteType.ABO_HEAD;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityAbominationBase.createAttributes()
                .add(Attributes.MAX_HEALTH, 400.0)
                .add(Attributes.ATTACK_DAMAGE, 30.0)
                .add(Attributes.MOVEMENT_SPEED, 0.30)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }
}
