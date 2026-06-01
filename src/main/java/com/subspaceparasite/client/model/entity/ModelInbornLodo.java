package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.inborn.EntityLodo;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for inborn/lodo.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelInbornLodo extends GeoModel<EntityLodo> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/inborn/lodo.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/inborn/lodo.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/inborn_lodo.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityLodo animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityLodo animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityLodo animatable) {
        return ANIMATION;
    }
}
