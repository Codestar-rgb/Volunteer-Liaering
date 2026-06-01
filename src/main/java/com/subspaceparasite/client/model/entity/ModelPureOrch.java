package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.pure.EntityFlog;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for pure/orch.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelPureOrch extends GeoModel<EntityFlog> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/pure/orch.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/pure/orch.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/pure_orch.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityFlog animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityFlog animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityFlog animatable) {
        return ANIMATION;
    }
}
