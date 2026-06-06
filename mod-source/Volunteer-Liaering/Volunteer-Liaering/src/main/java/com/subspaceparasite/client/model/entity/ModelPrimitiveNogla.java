package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.primitive.EntityNogla;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for primitive/nogla.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelPrimitiveNogla extends GeoModel<EntityNogla> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/primitive/nogla.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/primitive/nogla.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/primitive_nogla.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityNogla animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityNogla animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityNogla animatable) {
        return ANIMATION;
    }
}
