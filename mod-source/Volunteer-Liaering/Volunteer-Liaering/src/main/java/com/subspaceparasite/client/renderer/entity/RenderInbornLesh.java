package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.inborn.EntityLesh;
import com.subspaceparasite.client.model.entity.ModelInbornLesh;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for inborn_lesh.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInbornLesh extends GeoEntityRenderer<EntityLesh> {

    public RenderInbornLesh(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInbornLesh());
    }
}
