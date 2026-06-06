package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.inborn.EntityNuuh;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for inborn/nuuh.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelInbornNuuh extends GeoModel<EntityNuuh> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/inborn/nuuh.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/inborn/nuuh.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/inborn_nuuh.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityNuuh animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityNuuh animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityNuuh animatable) {
        return ANIMATION;
    }
}
