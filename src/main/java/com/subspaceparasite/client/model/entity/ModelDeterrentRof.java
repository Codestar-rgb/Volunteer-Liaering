package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.nexus.EntityRof;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for deterrent/rof.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelDeterrentRof extends GeoModel<EntityRof> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/deterrent/rof.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/deterrent/rof.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/deterrent_rof.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityRof animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityRof animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityRof animatable) {
        return ANIMATION;
    }
}
