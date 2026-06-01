package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.monster.projectile.EntityBeckonBlast;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

/**
 * GeckoLib model for projectile/dropPod.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class ModelProjectileDroppod extends GeoModel<EntityBeckonBlast> {

    private static final ResourceLocation MODEL = new ResourceLocation("subspaceparasite", "gecko_models/projectile/dropPod.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("subspaceparasite", "gecko_models/projectile/dropPod.png");

    @Override
    public ResourceLocation getModelResource(EntityBeckonBlast animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EntityBeckonBlast animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EntityBeckonBlast animatable) {
        return null; // No animation defined for this model
    }
}
