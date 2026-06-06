package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.ancient.EntityAncientColossus;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for awakened/oroncoAW.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelAwakenedOroncoaw extends GeoModel<EntityAncientColossus> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/awakened/oroncoAW.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/awakened/oroncoAW.png");

    @Override
    public ResourceLocation getModelResource(EntityAncientColossus animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityAncientColossus animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityAncientColossus animatable) {
        return null; // No animation defined for this model
    }
}
