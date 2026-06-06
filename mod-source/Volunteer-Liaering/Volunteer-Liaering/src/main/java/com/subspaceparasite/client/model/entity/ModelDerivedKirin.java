package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.derived.EntityKirin;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for derived/kirin.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelDerivedKirin extends GeoModel<EntityKirin> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/derived/kirin.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/derived/kirin.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/derived_kirin.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityKirin animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityKirin animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityKirin animatable) {
        return ANIMATION;
    }
}
