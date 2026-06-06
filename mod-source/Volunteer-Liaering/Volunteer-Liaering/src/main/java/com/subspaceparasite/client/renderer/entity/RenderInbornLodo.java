package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.inborn.EntityLodo;
import com.subspaceparasite.client.model.entity.ModelInbornLodo;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for inborn_lodo.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInbornLodo extends GeoEntityRenderer<EntityLodo> {

    public RenderInbornLodo(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInbornLodo());
    }
}
