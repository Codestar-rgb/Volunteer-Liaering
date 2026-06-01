package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.primitive.EntityGim;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for primitive/gim.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelPrimitiveGim extends GeoModel<EntityGim> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/primitive/gim.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/primitive/gim.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/primitive_gim.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityGim animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityGim animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityGim animatable) {
        return ANIMATION;
    }
}
