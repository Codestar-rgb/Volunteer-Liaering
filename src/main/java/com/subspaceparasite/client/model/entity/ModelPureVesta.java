package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.pure.EntityFlam;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for pure/vesta.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelPureVesta extends GeoModel<EntityFlam> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/pure/vesta.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/pure/vesta.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/pure_vesta.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityFlam animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityFlam animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityFlam animatable) {
        return ANIMATION;
    }
}
