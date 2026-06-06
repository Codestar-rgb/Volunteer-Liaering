package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.inborn.EntityRathol;
import com.subspaceparasite.client.model.entity.ModelInbornRathol;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for inborn_rathol.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInbornRathol extends GeoEntityRenderer<EntityRathol> {

    public RenderInbornRathol(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInbornRathol());
    }
}
