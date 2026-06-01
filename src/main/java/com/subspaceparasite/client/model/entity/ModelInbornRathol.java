package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.inborn.EntityRathol;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for inborn/rathol.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelInbornRathol extends GeoModel<EntityRathol> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/inborn/rathol.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/inborn/rathol.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/inborn_rathol.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityRathol animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityRathol animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityRathol animatable) {
        return ANIMATION;
    }
}
