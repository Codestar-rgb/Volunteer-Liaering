package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.inborn.EntityMudo;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for inborn/mudo.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelInbornMudo extends GeoModel<EntityMudo> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/inborn/mudo.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/inborn/mudo.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/inborn_mudo.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityMudo animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityMudo animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityMudo animatable) {
        return ANIMATION;
    }
}
