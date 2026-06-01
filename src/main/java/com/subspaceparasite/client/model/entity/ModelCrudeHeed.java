package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.crude.EntityHeed;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for crude/heed.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelCrudeHeed extends GeoModel<EntityHeed> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/crude/heed.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/crude/heed.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/crude_heed.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityHeed animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityHeed animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityHeed animatable) {
        return ANIMATION;
    }
}
