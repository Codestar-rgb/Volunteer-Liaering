package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.dispatcher.EntityDodT;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for deterrent/dodT.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelDeterrentDodt extends GeoModel<EntityDodT> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/deterrent/dodT.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/deterrent/dodT.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/deterrent_dodT.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityDodT animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityDodT animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityDodT animatable) {
        return ANIMATION;
    }
}
