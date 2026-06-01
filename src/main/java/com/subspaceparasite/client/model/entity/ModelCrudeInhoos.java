package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.crude.EntityInhooS;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for crude/inhooS.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelCrudeInhoos extends GeoModel<EntityInhooS> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/crude/inhooS.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/crude/inhooS.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/crude_inhooS.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityInhooS animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityInhooS animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityInhooS animatable) {
        return ANIMATION;
    }
}
