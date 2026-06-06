package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.primitive.EntityRanrac;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for primitive/ranrac.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelPrimitiveRanrac extends GeoModel<EntityRanrac> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/primitive/ranrac.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/primitive/ranrac.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/primitive_ranrac.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityRanrac animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityRanrac animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityRanrac animatable) {
        return ANIMATION;
    }
}
