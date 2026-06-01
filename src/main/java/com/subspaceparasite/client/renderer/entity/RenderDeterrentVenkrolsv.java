package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.deterrent.EntityVenkrolSIV;
import com.subspaceparasite.client.model.entity.ModelDeterrentVenkrolsv;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for deterrent_venkrolSV.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderDeterrentVenkrolsv extends GeoEntityRenderer<EntityVenkrolSIV> {

    public RenderDeterrentVenkrolsv(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelDeterrentVenkrolsv());
    }
}
