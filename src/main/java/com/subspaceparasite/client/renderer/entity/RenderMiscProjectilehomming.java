package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.projectile.EntityVirulentShot;
import com.subspaceparasite.client.model.entity.ModelMiscProjectilehomming;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for misc_projectileHomming.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderMiscProjectilehomming extends GeoEntityRenderer<EntityVirulentShot> {

    public RenderMiscProjectilehomming(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelMiscProjectilehomming());
    }
}
