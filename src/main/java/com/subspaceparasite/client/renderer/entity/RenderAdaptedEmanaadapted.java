package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.adapted.EntityEmanaAdapted;
import com.subspaceparasite.client.model.entity.ModelAdaptedEmanaadapted;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for adapted_emanaAdapted.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderAdaptedEmanaadapted extends GeoEntityRenderer<EntityEmanaAdapted> {

    public RenderAdaptedEmanaadapted(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelAdaptedEmanaadapted());
    }
}
