package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.inborn.EntityKol;
import com.subspaceparasite.client.model.entity.ModelInbornKol;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for inborn_kol.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInbornKol extends GeoEntityRenderer<EntityKol> {

    public RenderInbornKol(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInbornKol());
    }
}
