package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.inborn.EntityAta;
import com.subspaceparasite.client.model.entity.ModelInbornAta;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for inborn_ata.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInbornAta extends GeoEntityRenderer<EntityAta> {

    public RenderInbornAta(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInbornAta());
    }
}
