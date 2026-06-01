package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.primitive.EntityHull;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for primitive/hull.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelPrimitiveHull extends GeoModel<EntityHull> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/primitive/hull.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/primitive/hull.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/primitive_hull.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityHull animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityHull animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityHull animatable) {
        return ANIMATION;
    }
}
