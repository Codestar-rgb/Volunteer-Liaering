package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.feral.EntityFeralHuman;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for feral/ferHuman.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelFeralFerhuman extends GeoModel<EntityFeralHuman> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/feral/ferHuman.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/feral/ferHuman.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/feral_ferHuman.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityFeralHuman animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityFeralHuman animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityFeralHuman animatable) {
        return ANIMATION;
    }
}
