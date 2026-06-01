package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.deterrent.EntityVenkrolSIV;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for deterrent/venkrolSIII.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelDeterrentVenkrolsiii extends GeoModel<EntityVenkrolSIV> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/deterrent/venkrolSIII.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/deterrent/venkrolSIII.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/deterrent_venkrolSIII.animation.json");

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
