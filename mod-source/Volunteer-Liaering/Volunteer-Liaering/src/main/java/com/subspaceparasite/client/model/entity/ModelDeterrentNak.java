package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.nexus.EntityNak;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for deterrent/nak.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelDeterrentNak extends GeoModel<EntityNak> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/deterrent/nak.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/deterrent/nak.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/deterrent_nak.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityNak animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityNak animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityNak animatable) {
        return ANIMATION;
    }
}
