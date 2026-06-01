package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.infected.EntityInfectedHuman;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for infected/infHuman.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelInfectedInfhuman extends GeoModel<EntityInfectedHuman> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/infected/infHuman.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/infected/infHuman.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/infected_infHuman.animation.json");

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
