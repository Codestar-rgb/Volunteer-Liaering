package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.projectile.EntityOrbVoid;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for misc/orbVoid.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelMiscOrbvoid extends GeoModel<EntityOrbVoid> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/misc/orbVoid.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/misc/orbVoid.png");

    @Override
    public ResourceLocation getModelResource(EntityOrbVoid animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityOrbVoid animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityOrbVoid animatable) {
        return null; // No animation defined for this model
    }
}
