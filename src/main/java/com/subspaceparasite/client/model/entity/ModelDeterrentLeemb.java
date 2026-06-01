package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.rooter.EntityLeemB;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for deterrent/leemB.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelDeterrentLeemb extends GeoModel<EntityLeemB> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/deterrent/leemB.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/deterrent/leemB.png");

    @Override
    public ResourceLocation getModelResource(EntityLeemB animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityLeemB animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityLeemB animatable) {
        return null; // No animation defined for this model
    }
}
