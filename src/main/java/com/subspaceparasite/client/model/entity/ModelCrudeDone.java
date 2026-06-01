package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.crude.EntityDone;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for crude/done.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelCrudeDone extends GeoModel<EntityDone> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/crude/done.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/crude/done.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/crude_done.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityDone animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityDone animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityDone animatable) {
        return ANIMATION;
    }
}
