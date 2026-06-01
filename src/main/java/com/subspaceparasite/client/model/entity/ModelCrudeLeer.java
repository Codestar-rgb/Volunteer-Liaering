package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.crude.EntityLeer;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for crude/leer.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelCrudeLeer extends GeoModel<EntityLeer> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/crude/leer.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/crude/leer.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/crude_leer.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityLeer animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityLeer animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityLeer animatable) {
        return ANIMATION;
    }
}
