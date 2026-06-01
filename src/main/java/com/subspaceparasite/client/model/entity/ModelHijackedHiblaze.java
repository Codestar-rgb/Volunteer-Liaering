package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.hijacked.EntityHijackedCreeper;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for hijacked/hiBlaze.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelHijackedHiblaze extends GeoModel<EntityHijackedCreeper> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/hijacked/hiBlaze.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/hijacked/hiBlaze.png");

    @Override
    public ResourceLocation getModelResource(EntityHijackedCreeper animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityHijackedCreeper animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityHijackedCreeper animatable) {
        return null; // No animation defined for this model
    }
}
