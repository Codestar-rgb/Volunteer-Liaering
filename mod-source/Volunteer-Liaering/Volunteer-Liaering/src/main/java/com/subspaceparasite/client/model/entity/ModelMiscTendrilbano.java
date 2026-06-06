package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.misc.EntityParasiteLarva;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for misc/tendrilBano.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelMiscTendrilbano extends GeoModel<EntityParasiteLarva> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/misc/tendrilBano.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/misc/tendrilBano.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/misc_tendrilBano.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityParasiteLarva animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityParasiteLarva animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityParasiteLarva animatable) {
        return ANIMATION;
    }
}
