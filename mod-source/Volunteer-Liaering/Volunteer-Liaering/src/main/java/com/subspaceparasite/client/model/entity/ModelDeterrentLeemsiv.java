package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.rooter.EntityLeemB;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for deterrent/leemSIV.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelDeterrentLeemsiv extends GeoModel<EntityLeemB> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/deterrent/leemSIV.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/deterrent/leemSIV.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/deterrent_leemSIV.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityLeemB animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityLeemB animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityLeemB animatable) {
        return ANIMATION;
    }
}
