package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.adapted.EntityCanraAdapted;
import com.subspaceparasite.client.model.entity.ModelAdaptedCanraadapted;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for adapted_canraAdapted.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderAdaptedCanraadapted extends GeoEntityRenderer<EntityCanraAdapted> {

    public RenderAdaptedCanraadapted(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelAdaptedCanraadapted());
    }
}
