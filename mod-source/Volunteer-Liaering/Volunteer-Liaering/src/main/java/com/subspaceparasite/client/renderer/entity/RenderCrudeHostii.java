package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.crude.EntityInhooM;
import com.subspaceparasite.client.model.entity.ModelCrudeHostii;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for crude_hostII.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderCrudeHostii extends GeoEntityRenderer<EntityInhooM> {

    public RenderCrudeHostii(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelCrudeHostii());
    }
}
