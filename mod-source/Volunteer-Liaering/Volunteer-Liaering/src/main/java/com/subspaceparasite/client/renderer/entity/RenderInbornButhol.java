package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.inborn.EntityButhol;
import com.subspaceparasite.client.model.entity.ModelInbornButhol;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for inborn_buthol.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInbornButhol extends GeoEntityRenderer<EntityButhol> {

    public RenderInbornButhol(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInbornButhol());
    }
}
