package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.crude.EntityCruxA;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for crude/cruxA.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelCrudeCruxa extends GeoModel<EntityCruxA> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/crude/cruxA.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/crude/cruxA.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/crude_cruxA.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityCruxA animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityCruxA animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityCruxA animatable) {
        return ANIMATION;
    }
}
