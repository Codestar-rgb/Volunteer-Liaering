package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.adapted.EntityHullAdapted;
import com.subspaceparasite.client.model.entity.ModelAdaptedHulladapted;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for adapted_hullAdapted.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderAdaptedHulladapted extends GeoEntityRenderer<EntityHullAdapted> {

    public RenderAdaptedHulladapted(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelAdaptedHulladapted());
    }
}
