package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.inborn.EntityViin;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for inborn/viin.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelInbornViin extends GeoModel<EntityViin> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/inborn/viin.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/inborn/viin.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/inborn_viin.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityViin animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityViin animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityViin animatable) {
        return ANIMATION;
    }
}
