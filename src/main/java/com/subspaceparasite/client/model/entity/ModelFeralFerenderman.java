package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.feral.EntityFeralEnderman;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for feral/ferEnderman.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelFeralFerenderman extends GeoModel<EntityFeralEnderman> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/feral/ferEnderman.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/feral/ferEnderman.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/feral_ferEnderman.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityFeralEnderman animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityFeralEnderman animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityFeralEnderman animatable) {
        return ANIMATION;
    }
}
