package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.projectile.EntityOrbVoid;
import com.subspaceparasite.client.model.entity.ModelMiscOrbvoid;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for misc_orbVoid.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderMiscOrbvoid extends GeoEntityRenderer<EntityOrbVoid> {

    public RenderMiscOrbvoid(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelMiscOrbvoid());
    }
}
