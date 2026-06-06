package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.hijacked.EntityHijackedSkeleton;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for hijacked/hiSkeleton.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelHijackedHiskeleton extends GeoModel<EntityHijackedSkeleton> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/hijacked/hiSkeleton.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/hijacked/hiSkeleton.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/hijacked_hiSkeleton.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityHijackedSkeleton animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityHijackedSkeleton animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityHijackedSkeleton animatable) {
        return ANIMATION;
    }
}
