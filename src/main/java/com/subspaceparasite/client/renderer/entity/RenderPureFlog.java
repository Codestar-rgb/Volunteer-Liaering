package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.pure.EntityFlog;
import com.subspaceparasite.client.model.entity.ModelPureFlog;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for pure_flog.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderPureFlog extends GeoEntityRenderer<EntityFlog> {

    public RenderPureFlog(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelPureFlog());
    }
}
