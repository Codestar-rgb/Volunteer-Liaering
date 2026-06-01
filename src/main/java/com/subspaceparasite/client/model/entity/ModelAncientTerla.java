package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.ancient.EntityAncientLeviathan;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for ancient/terla.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelAncientTerla extends GeoModel<EntityAncientLeviathan> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/ancient/terla.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/ancient/terla.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("subspaceparasite", "animations/ancient_terla.animation.json");

    @Override
    public ResourceLocation getModelResource(EntityAncientLeviathan animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityAncientLeviathan animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityAncientLeviathan animatable) {
        return ANIMATION;
    }
}
