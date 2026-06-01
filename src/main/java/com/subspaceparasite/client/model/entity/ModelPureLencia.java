package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.pure.EntityEsor;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for pure/lencia.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelPureLencia extends GeoModel<EntityEsor> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/pure/lencia.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/pure/lencia.png");

    @Override
    public ResourceLocation getModelResource(EntityEsor animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityEsor animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityEsor animatable) {
        return null; // No animation defined for this model
    }
}
