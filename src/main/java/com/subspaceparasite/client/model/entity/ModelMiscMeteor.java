package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.projectile.EntityMeteor;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for misc/meteor.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelMiscMeteor extends GeoModel<EntityMeteor> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/misc/meteor.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/misc/meteor.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/misc_meteor.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityMeteor animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityMeteor animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityMeteor animatable) {
        return ANIMATION;
    }
}
