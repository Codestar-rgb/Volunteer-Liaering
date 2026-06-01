package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.feral.EntityFeralZombie;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for feral/ferPig.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelFeralFerpig extends GeoModel<EntityFeralZombie> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/feral/ferPig.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/feral/ferPig.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/feral_ferPig.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityFeralZombie animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityFeralZombie animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityFeralZombie animatable) {
        return ANIMATION;
    }
}
