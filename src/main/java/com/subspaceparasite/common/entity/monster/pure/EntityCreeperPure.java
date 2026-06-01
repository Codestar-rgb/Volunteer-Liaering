package com.subspaceparasite.common.entity.monster.pure;

import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityPureBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * EntityCreeperPure - Pure Creeper
 * Parasite entity of the pure tier.
 */
public class EntityCreeperPure extends EntityPureBase {

    public EntityCreeperPure(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.parasiteType = ParasiteType.BOMBER_LIGHT;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityPureBase.createAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.ARMOR, 0.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }
}
