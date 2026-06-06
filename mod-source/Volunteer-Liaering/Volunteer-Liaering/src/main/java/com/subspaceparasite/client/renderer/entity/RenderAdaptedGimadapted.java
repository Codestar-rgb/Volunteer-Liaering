package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.adapted.EntityGimAdapted;
import com.subspaceparasite.client.model.entity.ModelAdaptedGimadapted;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for adapted_gimAdapted.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderAdaptedGimadapted extends GeoEntityRenderer<EntityGimAdapted> {

    public RenderAdaptedGimadapted(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelAdaptedGimadapted());
    }
}
