package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.inborn.EntityViin;
import com.subspaceparasite.client.model.entity.ModelInbornViin;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for inborn_viin.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInbornViin extends GeoEntityRenderer<EntityViin> {

    public RenderInbornViin(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInbornViin());
    }
}
