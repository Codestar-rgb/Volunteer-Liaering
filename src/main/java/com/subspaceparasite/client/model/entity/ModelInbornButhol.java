package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.inborn.EntityButhol;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for inborn/buthol.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelInbornButhol extends GeoModel<EntityButhol> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/inborn/buthol.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/inborn/buthol.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/inborn_buthol.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityButhol animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityButhol animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityButhol animatable) {
        return ANIMATION;
    }
}
