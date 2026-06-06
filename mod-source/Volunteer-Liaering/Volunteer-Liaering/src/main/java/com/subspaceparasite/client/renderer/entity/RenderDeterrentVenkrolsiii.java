package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.deterrent.EntityVenkrolSIV;
import com.subspaceparasite.client.model.entity.ModelDeterrentVenkrolsiii;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for deterrent_venkrolSIII.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderDeterrentVenkrolsiii extends GeoEntityRenderer<EntityVenkrolSIV> {

    public RenderDeterrentVenkrolsiii(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelDeterrentVenkrolsiii());
    }
}
