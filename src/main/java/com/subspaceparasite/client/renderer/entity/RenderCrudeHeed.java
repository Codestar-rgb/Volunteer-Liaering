package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.crude.EntityHeed;
import com.subspaceparasite.client.model.entity.ModelCrudeHeed;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for crude_heed.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderCrudeHeed extends GeoEntityRenderer<EntityHeed> {

    public RenderCrudeHeed(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelCrudeHeed());
    }
}
