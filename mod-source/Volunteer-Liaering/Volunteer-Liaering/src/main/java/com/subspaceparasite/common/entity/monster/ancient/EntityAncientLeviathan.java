package com.subspaceparasite.common.entity.monster.ancient;

import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityAncientBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * EntityAncientLeviathan - Ancient Leviathan
 * Parasite entity of the ancient tier.
 */
public class EntityAncientLeviathan extends EntityAncientBase {

    public EntityAncientLeviathan(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.parasiteType = ParasiteType.OVERLORD;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityAncientBase.createAttributes()
                .add(Attributes.MAX_HEALTH, 400.0)
                .add(Attributes.ATTACK_DAMAGE, 24.0)
                .add(Attributes.MOVEMENT_SPEED, 0.22)
                .add(Attributes.ARMOR, 20.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }
}
