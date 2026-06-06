package com.subspaceparasite.common.entity.monster.beckon;

import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityBeckonBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * EntityBeckonUncommon - Beckon Stage II
 * Parasite entity of the beckon tier.
 */
public class EntityBeckonUncommon extends EntityBeckonBase {

    public EntityBeckonUncommon(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.parasiteType = ParasiteType.BECKON_SII;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityBeckonBase.createAttributes()
                .add(Attributes.MAX_HEALTH, 120.0)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.ARMOR, 8.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }
}
