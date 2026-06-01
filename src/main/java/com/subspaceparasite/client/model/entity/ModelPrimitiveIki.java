package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.primitive.EntityIki;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for primitive/iki.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelPrimitiveIki extends GeoModel<EntityIki> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/primitive/iki.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/primitive/iki.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/primitive_iki.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityIki animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityIki animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityIki animatable) {
        return ANIMATION;
    }
}
