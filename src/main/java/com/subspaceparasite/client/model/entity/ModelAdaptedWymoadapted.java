package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.adapted.EntityWymoAdapted;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for adapted/wymoAdapted.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelAdaptedWymoadapted extends GeoModel<EntityWymoAdapted> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/adapted/wymoAdapted.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/adapted/wymoAdapted.png");

    @Override
    public ResourceLocation getModelResource(EntityWymoAdapted animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityWymoAdapted animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityWymoAdapted animatable) {
        return null; // No animation defined for this model
    }
}
