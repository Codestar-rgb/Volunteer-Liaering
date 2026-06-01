package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.deterrent.EntityDeterrentSentry;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for deterrent/dodSIII.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelDeterrentDodsiii extends GeoModel<EntityDeterrentSentry> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/deterrent/dodSIII.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/deterrent/dodSIII.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/deterrent_dodSIII.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityDeterrentSentry animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityDeterrentSentry animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityDeterrentSentry animatable) {
        return ANIMATION;
    }
}
