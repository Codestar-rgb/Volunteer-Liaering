package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.nexus.EntityTonro;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for deterrent/tonro.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelDeterrentTonro extends GeoModel<EntityTonro> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/deterrent/tonro.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/deterrent/tonro.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/deterrent_tonro.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityTonro animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityTonro animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityTonro animatable) {
        return ANIMATION;
    }
}
