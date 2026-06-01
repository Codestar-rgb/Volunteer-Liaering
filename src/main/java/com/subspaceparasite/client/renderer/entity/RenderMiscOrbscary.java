package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.projectile.EntityOrbScary;
import com.subspaceparasite.client.model.entity.ModelMiscOrbscary;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for misc_orbScary.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderMiscOrbscary extends GeoEntityRenderer<EntityOrbScary> {

    public RenderMiscOrbscary(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelMiscOrbscary());
    }
}
