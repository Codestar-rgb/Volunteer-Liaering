package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.ancient.EntityAncientDreadnought;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for ancient/oroncoTen.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelAncientOroncoten extends GeoModel<EntityAncientDreadnought> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/ancient/oroncoTen.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/ancient/oroncoTen.png");

    @Override
    public ResourceLocation getModelResource(EntityAncientDreadnought animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityAncientDreadnought animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityAncientDreadnought animatable) {
        return null; // No animation defined for this model
    }
}
