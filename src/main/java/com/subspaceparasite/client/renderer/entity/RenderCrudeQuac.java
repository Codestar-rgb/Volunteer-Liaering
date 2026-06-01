package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.crude.EntityQuac;
import com.subspaceparasite.client.model.entity.ModelCrudeQuac;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for crude_quac.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderCrudeQuac extends GeoEntityRenderer<EntityQuac> {

    public RenderCrudeQuac(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelCrudeQuac());
    }
}
