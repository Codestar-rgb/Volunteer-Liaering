package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.crude.EntityMes;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for crude/mes.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelCrudeMes extends GeoModel<EntityMes> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/crude/mes.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/crude/mes.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/crude_mes.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityMes animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityMes animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityMes animatable) {
        return ANIMATION;
    }
}
