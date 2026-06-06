package com.subspaceparasite.common.entity.monster.rooter;

import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityRooterBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * EntityRooterRare - Rooter Stage III
 * Parasite entity of the rooter tier.
 */
public class EntityRooterRare extends EntityRooterBase {

    public EntityRooterRare(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.parasiteType = ParasiteType.ROOTER_SIII;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityRooterBase.createAttributes()
                .add(Attributes.MAX_HEALTH, 160.0)
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }
}
