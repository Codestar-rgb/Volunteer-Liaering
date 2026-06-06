package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.nexus.EntityUnvo;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for deterrent/unvo.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelDeterrentUnvo extends GeoModel<EntityUnvo> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/deterrent/unvo.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/deterrent/unvo.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/deterrent_unvo.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityUnvo animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityUnvo animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityUnvo animatable) {
        return ANIMATION;
    }
}
