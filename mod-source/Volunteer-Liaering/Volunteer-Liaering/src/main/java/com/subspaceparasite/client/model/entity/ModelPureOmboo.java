package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.pure.EntityOmboo;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for pure/omboo.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelPureOmboo extends GeoModel<EntityOmboo> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/pure/omboo.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/pure/omboo.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/pure_omboo.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityOmboo animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityOmboo animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityOmboo animatable) {
        return ANIMATION;
    }
}
