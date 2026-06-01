package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.inborn.EntityLesh;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for inborn/lesh.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelInbornLesh extends GeoModel<EntityLesh> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/inborn/lesh.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/inborn/lesh.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/inborn_lesh.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityLesh animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityLesh animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityLesh animatable) {
        return ANIMATION;
    }
}
