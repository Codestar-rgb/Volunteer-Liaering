package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.projectile.EntityMeteor;
import com.subspaceparasite.client.model.entity.ModelMiscMeteor;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for misc_meteor.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderMiscMeteor extends GeoEntityRenderer<EntityMeteor> {

    public RenderMiscMeteor(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelMiscMeteor());
    }
}
