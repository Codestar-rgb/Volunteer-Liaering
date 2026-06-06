package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.primitive.EntityShyco;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for primitive/shyco.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelPrimitiveShyco extends GeoModel<EntityShyco> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/primitive/shyco.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/primitive/shyco.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/primitive_shyco.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityShyco animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityShyco animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityShyco animatable) {
        return ANIMATION;
    }
}
