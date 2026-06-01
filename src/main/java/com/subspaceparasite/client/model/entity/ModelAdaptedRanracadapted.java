package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.adapted.EntityRanracAdapted;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for adapted/ranracAdapted.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelAdaptedRanracadapted extends GeoModel<EntityRanracAdapted> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/adapted/ranracAdapted.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/adapted/ranracAdapted.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/adapted_ranracAdapted.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityRanracAdapted animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityRanracAdapted animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityRanracAdapted animatable) {
        return ANIMATION;
    }
}
