package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.abomination.EntityAbominationHydra;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for abomination/aboHead.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelAbominationAbohead extends GeoModel<EntityAbominationHydra> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/abomination/aboHead.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/abomination/aboHead.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/abomination_aboHead.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityAbominationHydra animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityAbominationHydra animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityAbominationHydra animatable) {
        return ANIMATION;
    }
}
