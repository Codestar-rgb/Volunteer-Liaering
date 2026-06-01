package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.crude.EntityDone;
import com.subspaceparasite.client.model.entity.ModelCrudeDone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for crude_done.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderCrudeDone extends GeoEntityRenderer<EntityDone> {

    public RenderCrudeDone(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelCrudeDone());
    }
}
