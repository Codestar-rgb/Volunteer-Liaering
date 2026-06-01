package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.projectile.EntityBileBomb;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for misc/nade.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelMiscNade extends GeoModel<EntityBileBomb> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/misc/nade.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/misc/nade.png");

    @Override
    public ResourceLocation getModelResource(EntityBileBomb animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityBileBomb animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityBileBomb animatable) {
        return null; // No animation defined for this model
    }
}
