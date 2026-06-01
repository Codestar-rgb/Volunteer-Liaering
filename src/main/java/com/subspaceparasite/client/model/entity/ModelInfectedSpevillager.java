package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.infected.EntityInfectedVillager;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for infected/speVillager.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelInfectedSpevillager extends GeoModel<EntityInfectedVillager> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/infected/speVillager.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/infected/speVillager.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/infected_speVillager.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityInfectedVillager animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityInfectedVillager animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityInfectedVillager animatable) {
        return ANIMATION;
    }
}
