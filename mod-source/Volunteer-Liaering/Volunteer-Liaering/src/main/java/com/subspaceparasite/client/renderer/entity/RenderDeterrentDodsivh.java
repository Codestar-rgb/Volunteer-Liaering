package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.deterrent.EntityVenkrolSIV;
import com.subspaceparasite.client.model.entity.ModelDeterrentDodsivh;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for deterrent_dodSIVH.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderDeterrentDodsivh extends GeoEntityRenderer<EntityVenkrolSIV> {

    public RenderDeterrentDodsivh(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelDeterrentDodsivh());
    }
}
