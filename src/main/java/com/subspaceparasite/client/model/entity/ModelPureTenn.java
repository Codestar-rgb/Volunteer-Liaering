package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.pure.EntityAnged;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for pure/tenn.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelPureTenn extends GeoModel<EntityAnged> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/pure/tenn.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/pure/tenn.png");

    @Override
    public ResourceLocation getModelResource(EntityAnged animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityAnged animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityAnged animatable) {
        return null; // No animation defined for this model
    }
}
