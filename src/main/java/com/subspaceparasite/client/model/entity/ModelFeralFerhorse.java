package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.feral.EntityFeralCow;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for feral/ferHorse.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelFeralFerhorse extends GeoModel<EntityFeralCow> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/feral/ferHorse.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/feral/ferHorse.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/feral_ferHorse.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityFeralCow animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityFeralCow animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityFeralCow animatable) {
        return ANIMATION;
    }
}
