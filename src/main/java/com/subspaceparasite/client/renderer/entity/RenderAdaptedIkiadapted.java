package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.adapted.EntityIkiAdapted;
import com.subspaceparasite.client.model.entity.ModelAdaptedIkiadapted;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for adapted_ikiAdapted.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderAdaptedIkiadapted extends GeoEntityRenderer<EntityIkiAdapted> {

    public RenderAdaptedIkiadapted(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelAdaptedIkiadapted());
    }
}
