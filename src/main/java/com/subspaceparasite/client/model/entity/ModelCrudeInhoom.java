package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.crude.EntityInhooM;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for crude/inhooM.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelCrudeInhoom extends GeoModel<EntityInhooM> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/crude/inhooM.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/crude/inhooM.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/crude_inhooM.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityInhooM animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityInhooM animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityInhooM animatable) {
        return ANIMATION;
    }
}
