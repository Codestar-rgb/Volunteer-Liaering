package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.adapted.EntityBanoAdapted;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for adapted/banoAdapted.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelAdaptedBanoadapted extends GeoModel<EntityBanoAdapted> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/adapted/banoAdapted.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/adapted/banoAdapted.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/adapted_banoAdapted.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityBanoAdapted animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityBanoAdapted animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityBanoAdapted animatable) {
        return ANIMATION;
    }
}
