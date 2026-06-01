package com.subspaceparasite.common.entity.monster.misc;

import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * EntityParasiteLarva - Parasite Larva
 * Parasite entity of the misc tier.
 */
public class EntityParasiteLarva extends EntityParasiteBase {

    public EntityParasiteLarva(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.parasiteType = ParasiteType.LICE;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityParasiteBase.createAttributes()
                .add(Attributes.MAX_HEALTH, 2.0)
                .add(Attributes.ATTACK_DAMAGE, 0.5)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.ARMOR, 0.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }
}
