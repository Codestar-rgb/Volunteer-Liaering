package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.projectile.EntityOrbScary;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for misc/orbScary.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelMiscOrbscary extends GeoModel<EntityOrbScary> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/misc/orbScary.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/misc/orbScary.png");

    @Override
    public ResourceLocation getModelResource(EntityOrbScary animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityOrbScary animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityOrbScary animatable) {
        return null; // No animation defined for this model
    }
}
