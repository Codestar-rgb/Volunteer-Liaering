package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.inborn.EntityMudo;
import com.subspaceparasite.client.model.entity.ModelInbornMudo;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for inborn_mudo.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInbornMudo extends GeoEntityRenderer<EntityMudo> {

    public RenderInbornMudo(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInbornMudo());
    }
}
