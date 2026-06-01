package com.subspaceparasite.common.entity.monster.beckon;

import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityBeckonBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * EntityBeckonRare - Beckon Stage III
 * Parasite entity of the beckon tier.
 */
public class EntityBeckonRare extends EntityBeckonBase {

    public EntityBeckonRare(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.parasiteType = ParasiteType.BECKON_SIII;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityBeckonBase.createAttributes()
                .add(Attributes.MAX_HEALTH, 160.0)
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }
}
