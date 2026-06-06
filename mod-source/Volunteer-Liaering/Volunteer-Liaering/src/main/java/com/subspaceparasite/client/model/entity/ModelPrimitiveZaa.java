package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.primitive.EntityZaa;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for primitive/zaa.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelPrimitiveZaa extends GeoModel<EntityZaa> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/primitive/zaa.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/primitive/zaa.png");

    @Override
    public ResourceLocation getModelResource(EntityZaa animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityZaa animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityZaa animatable) {
        return null; // No animation defined for this model
    }
}
