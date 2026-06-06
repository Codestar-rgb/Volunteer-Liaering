package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.feral.EntityFeralEnderman;
import com.subspaceparasite.client.model.entity.ModelFeralFerenderman;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for feral_ferEnderman.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderFeralFerenderman extends GeoEntityRenderer<EntityFeralEnderman> {

    public RenderFeralFerenderman(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelFeralFerenderman());
    }
}
