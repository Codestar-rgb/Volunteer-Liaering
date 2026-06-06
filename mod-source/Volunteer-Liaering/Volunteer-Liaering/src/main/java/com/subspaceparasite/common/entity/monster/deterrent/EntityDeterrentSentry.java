package com.subspaceparasite.common.entity.monster.deterrent;

import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityDeterrentBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * EntityDeterrentSentry - Deterrent Sentry
 * Parasite entity of the deterrent tier.
 */
public class EntityDeterrentSentry extends EntityDeterrentBase {

    public EntityDeterrentSentry(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.parasiteType = ParasiteType.SENTRY;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityDeterrentBase.createAttributes()
                .add(Attributes.MAX_HEALTH, 60.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.22)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }
}
