package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.primitive.EntityLum;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for primitive/lum.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelPrimitiveLum extends GeoModel<EntityLum> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/primitive/lum.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/primitive/lum.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/primitive_lum.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityLum animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityLum animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityLum animatable) {
        return ANIMATION;
    }
}
