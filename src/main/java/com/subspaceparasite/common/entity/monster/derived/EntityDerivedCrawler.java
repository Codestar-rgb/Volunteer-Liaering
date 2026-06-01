package com.subspaceparasite.common.entity.monster.derived;

import com.subspaceparasite.api.parasite.ParasiteType;
import com.subspaceparasite.common.entity.base.EntityDerivedBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * EntityDerivedCrawler - Derived Crawler
 * Parasite entity of the derived tier.
 */
public class EntityDerivedCrawler extends EntityDerivedBase {

    public EntityDerivedCrawler(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.parasiteType = ParasiteType.DRACONITE;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityDerivedBase.createAttributes()
                .add(Attributes.MAX_HEALTH, 350.0)
                .add(Attributes.ATTACK_DAMAGE, 22.0)
                .add(Attributes.MOVEMENT_SPEED, 0.32)
                .add(Attributes.ARMOR, 14.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected boolean checkNativeBiome() {
        return false; // No specific native biome for generic derived types
    }
}
