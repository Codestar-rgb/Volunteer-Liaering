package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.deterrent.EntityVenkrolSIV;
import com.subspaceparasite.client.model.entity.ModelDeterrentVenkrolsiv;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for deterrent_venkrolSIV.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderDeterrentVenkrolsiv extends GeoEntityRenderer<EntityVenkrolSIV> {

    public RenderDeterrentVenkrolsiv(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelDeterrentVenkrolsiv());
    }
}
