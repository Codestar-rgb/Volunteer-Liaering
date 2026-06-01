package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.adapted.EntityEmanaAdapted;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for adapted/emanaAdapted.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelAdaptedEmanaadapted extends GeoModel<EntityEmanaAdapted> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/adapted/emanaAdapted.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/adapted/emanaAdapted.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/adapted_emanaAdapted.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityEmanaAdapted animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityEmanaAdapted animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityEmanaAdapted animatable) {
        return ANIMATION;
    }
}
