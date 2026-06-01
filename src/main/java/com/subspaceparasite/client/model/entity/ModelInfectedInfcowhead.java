package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.infected.EntityInfectedCow;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for infected/infCowHead.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelInfectedInfcowhead extends GeoModel<EntityInfectedCow> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/infected/infCowHead.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/infected/infCowHead.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/infected_infCowHead.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityInfectedCow animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityInfectedCow animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityInfectedCow animatable) {
        return ANIMATION;
    }
}
