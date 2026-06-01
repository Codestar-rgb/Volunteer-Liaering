package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.deterrent.EntityVenkrolSIV;
import com.subspaceparasite.client.model.entity.ModelDeterrentVenkrol;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for deterrent_venkrol.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderDeterrentVenkrol extends GeoEntityRenderer<EntityVenkrolSIV> {

    public RenderDeterrentVenkrol(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelDeterrentVenkrol());
    }
}
