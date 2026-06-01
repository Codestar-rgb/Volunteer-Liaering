package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.crude.EntityHost;
import com.subspaceparasite.client.model.entity.ModelCrudeHost;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for crude_host.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderCrudeHost extends GeoEntityRenderer<EntityHost> {

    public RenderCrudeHost(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelCrudeHost());
    }
}
