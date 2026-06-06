package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.inborn.EntityKol;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for inborn/kol.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelInbornKol extends GeoModel<EntityKol> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/inborn/kol.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/inborn/kol.png");

    @Override
    public ResourceLocation getModelResource(EntityKol animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityKol animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityKol animatable) {
        return null; // No animation defined for this model
    }
}
