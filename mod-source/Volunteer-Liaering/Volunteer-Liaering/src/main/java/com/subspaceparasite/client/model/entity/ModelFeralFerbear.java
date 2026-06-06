package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.feral.EntityFeralIronGolem;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for feral/ferBear.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelFeralFerbear extends GeoModel<EntityFeralIronGolem> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/feral/ferBear.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/feral/ferBear.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/feral_ferBear.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityFeralIronGolem animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityFeralIronGolem animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityFeralIronGolem animatable) {
        return ANIMATION;
    }
}
