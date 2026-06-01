package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.abomination.EntityAbominationAmalgam;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for abomination/aboBodies.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelAbominationAbobodies extends GeoModel<EntityAbominationAmalgam> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/abomination/aboBodies.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/abomination/aboBodies.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/abomination_aboBodies.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityAbominationAmalgam animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityAbominationAmalgam animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityAbominationAmalgam animatable) {
        return ANIMATION;
    }
}
