package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.primitive.EntityEmana;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for primitive/emana.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelPrimitiveEmana extends GeoModel<EntityEmana> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/primitive/emana.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/primitive/emana.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/primitive_emana.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityEmana animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityEmana animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityEmana animatable) {
        return ANIMATION;
    }
}
