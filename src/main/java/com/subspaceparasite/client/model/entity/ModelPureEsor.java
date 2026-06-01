package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.pure.EntityEsor;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for pure/esor.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelPureEsor extends GeoModel<EntityEsor> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/pure/esor.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/pure/esor.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/pure_esor.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityEsor animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityEsor animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityEsor animatable) {
        return ANIMATION;
    }
}
