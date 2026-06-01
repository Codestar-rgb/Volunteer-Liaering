package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.infected.EntityInfectedHuman;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for infected/speHuman.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelInfectedSpehuman extends GeoModel<EntityInfectedHuman> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/infected/speHuman.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/infected/speHuman.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/infected_speHuman.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityInfectedHuman animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityInfectedHuman animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityInfectedHuman animatable) {
        return ANIMATION;
    }
}
