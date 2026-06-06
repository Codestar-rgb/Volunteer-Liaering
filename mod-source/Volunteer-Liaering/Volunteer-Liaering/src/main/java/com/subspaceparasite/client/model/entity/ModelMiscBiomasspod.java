package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.misc.EntityBoomOrb;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for misc/biomassPod.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelMiscBiomasspod extends GeoModel<EntityBoomOrb> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/misc/biomassPod.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/misc/biomassPod.png");

    @Override
    public ResourceLocation getModelResource(EntityBoomOrb animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityBoomOrb animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityBoomOrb animatable) {
        return null; // No animation defined for this model
    }
}
