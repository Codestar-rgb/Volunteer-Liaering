package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.adapted.EntityZaaAdapted;
import com.subspaceparasite.client.model.entity.ModelAdaptedZaaadapted;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for adapted_zaaAdapted.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderAdaptedZaaadapted extends GeoEntityRenderer<EntityZaaAdapted> {

    public RenderAdaptedZaaadapted(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelAdaptedZaaadapted());
    }
}
