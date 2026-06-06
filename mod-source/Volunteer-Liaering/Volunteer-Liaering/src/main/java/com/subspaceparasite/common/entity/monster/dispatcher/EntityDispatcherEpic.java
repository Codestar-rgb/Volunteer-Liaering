package com.subspaceparasite.common.entity.monster.dispatcher;

import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityDispatcherBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * EntityDispatcherEpic - Dispatcher Stage IV
 * Parasite entity of the dispatcher tier.
 */
public class EntityDispatcherEpic extends EntityDispatcherBase {

    public EntityDispatcherEpic(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.parasiteType = ParasiteType.DISPATCHER_SIV;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityDispatcherBase.createAttributes()
                .add(Attributes.MAX_HEALTH, 240.0)
                .add(Attributes.ATTACK_DAMAGE, 14.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.ARMOR, 14.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }
}
