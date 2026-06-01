package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.infected.EntityInfectedSheep;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for infected/infSheep.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelInfectedInfsheep extends GeoModel<EntityInfectedSheep> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/infected/infSheep.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/infected/infSheep.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/infected_infSheep.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityInfectedSheep animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityInfectedSheep animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityInfectedSheep animatable) {
        return ANIMATION;
    }
}
