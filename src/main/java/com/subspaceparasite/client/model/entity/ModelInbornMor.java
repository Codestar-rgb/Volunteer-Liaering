package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.inborn.EntityMor;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for inborn/mor.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelInbornMor extends GeoModel<EntityMor> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/inborn/mor.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/inborn/mor.png");

    @Override
    public ResourceLocation getModelResource(EntityMor animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityMor animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityMor animatable) {
        return null; // No animation defined for this model
    }
}
