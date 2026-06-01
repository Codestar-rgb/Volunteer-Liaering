package com.subspaceparasite.common.entity.monster.derived;

import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityDerivedBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * EntityDerivedFly - Derived Fly
 * Parasite entity of the derived tier.
 */
public class EntityDerivedFly extends EntityDerivedBase {

    public EntityDerivedFly(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.parasiteType = ParasiteType.HEBLU;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityDerivedBase.createAttributes()
                .add(Attributes.MAX_HEALTH, 80.0)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected boolean checkNativeBiome() {
        return false; // No specific native biome for generic derived types
    }
}
