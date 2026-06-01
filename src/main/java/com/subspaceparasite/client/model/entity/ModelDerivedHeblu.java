package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.derived.EntityHeblu;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for derived/heblu.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelDerivedHeblu extends GeoModel<EntityHeblu> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/derived/heblu.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/derived/heblu.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/derived_heblu.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityHeblu animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityHeblu animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityHeblu animatable) {
        return ANIMATION;
    }
}
