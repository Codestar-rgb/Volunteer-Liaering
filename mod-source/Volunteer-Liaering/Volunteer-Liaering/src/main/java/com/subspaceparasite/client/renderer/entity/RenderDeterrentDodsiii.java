package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.deterrent.EntityDeterrentSentry;
import com.subspaceparasite.client.model.entity.ModelDeterrentDodsiii;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for deterrent_dodSIII.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderDeterrentDodsiii extends GeoEntityRenderer<EntityDeterrentSentry> {

    public RenderDeterrentDodsiii(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelDeterrentDodsiii());
    }
}
