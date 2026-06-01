package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.projectile.EntityVirulentShot;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for misc/projectileHomming.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelMiscProjectilehomming extends GeoModel<EntityVirulentShot> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/misc/projectileHomming.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/misc/projectileHomming.png");

    @Override
    public ResourceLocation getModelResource(EntityVirulentShot animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityVirulentShot animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityVirulentShot animatable) {
        return null; // No animation defined for this model
    }
}
