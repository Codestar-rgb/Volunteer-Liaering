package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.deterrent.EntityVenkrolSIV;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for deterrent/venkrolSIV.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelDeterrentVenkrolsiv extends GeoModel<EntityVenkrolSIV> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/deterrent/venkrolSIV.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/deterrent/venkrolSIV.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/deterrent_venkrolSIV.animation.json");

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
        return ANIMATION;
    }
}
