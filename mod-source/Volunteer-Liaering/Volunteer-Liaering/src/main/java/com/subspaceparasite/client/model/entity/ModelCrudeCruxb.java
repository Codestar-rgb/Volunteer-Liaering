package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.crude.EntityCruxB;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for crude/cruxB.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelCrudeCruxb extends GeoModel<EntityCruxB> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/crude/cruxB.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/crude/cruxB.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/crude_cruxB.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityCruxB animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityCruxB animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityCruxB animatable) {
        return ANIMATION;
    }
}
