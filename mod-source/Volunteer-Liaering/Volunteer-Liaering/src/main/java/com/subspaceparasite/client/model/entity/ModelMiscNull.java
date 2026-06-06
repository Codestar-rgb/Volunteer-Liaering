package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.misc.EntityVoidOrb;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for misc/nULL.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelMiscNull extends GeoModel<EntityVoidOrb> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/misc/nULL.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/misc/nULL.png");

    @Override
    public ResourceLocation getModelResource(EntityVoidOrb animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityVoidOrb animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityVoidOrb animatable) {
        return null; // No animation defined for this model
    }
}
