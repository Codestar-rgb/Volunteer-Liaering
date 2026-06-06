package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.pure.EntityGanro;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for pure/pheon.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelPurePheon extends GeoModel<EntityGanro> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/pure/pheon.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/pure/pheon.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/pure_pheon.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityGanro animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityGanro animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityGanro animatable) {
        return ANIMATION;
    }
}
