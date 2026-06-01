package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.misc.EntityParasiteLarva;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for misc/tendrilDragonERW.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelMiscTendrildragonerw extends GeoModel<EntityParasiteLarva> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/misc/tendrilDragonERW.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/misc/tendrilDragonERW.png");

    @Override
    public ResourceLocation getModelResource(EntityParasiteLarva animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityParasiteLarva animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityParasiteLarva animatable) {
        return null; // No animation defined for this model
    }
}
