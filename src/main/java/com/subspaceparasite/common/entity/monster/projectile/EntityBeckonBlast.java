package com.subspaceparasite.common.entity.monster.projectile;

import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * EntityBeckonBlast - Beckon Blast Projectile
 * Parasite entity of the projectile tier.
 */
public class EntityBeckonBlast extends EntityParasiteBase {

    public EntityBeckonBlast(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        // TODO: Add ParasiteType.PROJ_BECKON_BLAST to ParasiteType enum
        // this.parasiteType = ParasiteType.PROJ_BECKON_BLAST;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityParasiteBase.createAttributes()
                .add(Attributes.MAX_HEALTH, 2.0)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ARMOR, 0.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }
}
