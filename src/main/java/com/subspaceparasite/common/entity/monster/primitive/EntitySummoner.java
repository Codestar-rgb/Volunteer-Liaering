package com.subspaceparasite.common.entity.monster.primitive;

import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityPrimitiveBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * EntitySummoner - Primitive Summoner
 * Parasite entity of the primitive tier.
 */
public class EntitySummoner extends EntityPrimitiveBase {

    public EntitySummoner(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.parasiteType = ParasiteType.PRI_SUMMONER;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityPrimitiveBase.createAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ARMOR, 1.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }
}
