package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.adapted.EntityLumAdapted;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for adapted/lumAdapted.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelAdaptedLumadapted extends GeoModel<EntityLumAdapted> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/adapted/lumAdapted.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/adapted/lumAdapted.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/adapted_lumAdapted.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityLumAdapted animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityLumAdapted animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityLumAdapted animatable) {
        return ANIMATION;
    }
}
