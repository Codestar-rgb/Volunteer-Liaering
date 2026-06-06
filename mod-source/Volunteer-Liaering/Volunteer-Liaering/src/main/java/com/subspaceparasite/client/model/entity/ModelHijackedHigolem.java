package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.hijacked.EntityHijackedRavager;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for hijacked/hiGolem.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelHijackedHigolem extends GeoModel<EntityHijackedRavager> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/hijacked/hiGolem.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/hijacked/hiGolem.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/hijacked_hiGolem.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityHijackedRavager animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityHijackedRavager animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityHijackedRavager animatable) {
        return ANIMATION;
    }
}
