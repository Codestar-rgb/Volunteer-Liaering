package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.deterrent.EntityVenkrolSIV;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for deterrent/venkrolSV.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelDeterrentVenkrolsv extends GeoModel<EntityVenkrolSIV> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/deterrent/venkrolSV.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/deterrent/venkrolSV.png");

    @Override
    public ResourceLocation getModelResource(EntityVenkrolSIV animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityVenkrolSIV animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityVenkrolSIV animatable) {
        return null; // No animation defined for this model
    }
}
