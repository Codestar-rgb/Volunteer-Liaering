package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.pure.EntityAlafha;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for pure/alafha.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelPureAlafha extends GeoModel<EntityAlafha> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/pure/alafha.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/pure/alafha.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/pure_alafha.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityAlafha animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityAlafha animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityAlafha animatable) {
        return ANIMATION;
    }
}
