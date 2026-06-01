package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.ancient.EntityAncientColossus;
import com.subspaceparasite.client.model.entity.ModelAncientOronco;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for ancient_oronco.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderAncientOronco extends GeoEntityRenderer<EntityAncientColossus> {

    public RenderAncientOronco(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelAncientOronco());
    }
}
