package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.adapted.EntityRanracAdapted;
import com.subspaceparasite.client.model.entity.ModelAdaptedRanracadapted;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for adapted_ranracAdapted.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderAdaptedRanracadapted extends GeoEntityRenderer<EntityRanracAdapted> {

    public RenderAdaptedRanracadapted(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelAdaptedRanracadapted());
    }
}
