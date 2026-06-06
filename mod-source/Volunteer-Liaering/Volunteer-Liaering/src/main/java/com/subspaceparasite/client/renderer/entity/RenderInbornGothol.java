package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.inborn.EntityGothol;
import com.subspaceparasite.client.model.entity.ModelInbornGothol;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for inborn_gothol.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInbornGothol extends GeoEntityRenderer<EntityGothol> {

    public RenderInbornGothol(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInbornGothol());
    }
}
