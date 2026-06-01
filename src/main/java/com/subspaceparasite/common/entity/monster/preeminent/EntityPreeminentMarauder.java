package com.subspaceparasite.common.entity.monster.preeminent;

import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityPreeminentBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * EntityPreeminentMarauder - Preeminent Marauder
 * Parasite entity of the preeminent tier.
 */
public class EntityPreeminentMarauder extends EntityPreeminentBase {

    public EntityPreeminentMarauder(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.parasiteType = ParasiteType.MARAUDER;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityPreeminentBase.createAttributes()
                .add(Attributes.MAX_HEALTH, 80.0)
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }
}
