package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.crude.EntityQuac;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for crude/quac.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelCrudeQuac extends GeoModel<EntityQuac> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/crude/quac.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/crude/quac.png");

    @Override
    public ResourceLocation getModelResource(EntityQuac animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityQuac animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityQuac animatable) {
        return null; // No animation defined for this model
    }
}
