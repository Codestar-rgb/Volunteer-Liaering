package com.subspaceparasite.common.entity.monster.projectile;

import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * EntityBileBomb - Bile Bomb Projectile
 * Parasite entity of the projectile tier.
 */
public class EntityBileBomb extends EntityParasiteBase {

    public EntityBileBomb(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        // TODO: Add ParasiteType.PROJ_BILE_BOMB to ParasiteType enum
        // this.parasiteType = ParasiteType.PROJ_BILE_BOMB;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityParasiteBase.createAttributes()
                .add(Attributes.MAX_HEALTH, 2.0)
                .add(Attributes.ATTACK_DAMAGE, 1.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ARMOR, 0.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }
}
