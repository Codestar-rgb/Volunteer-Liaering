package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.inborn.EntityMor;
import com.subspaceparasite.client.model.entity.ModelInbornMor;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for inborn_mor.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInbornMor extends GeoEntityRenderer<EntityMor> {

    public RenderInbornMor(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInbornMor());
    }
}
