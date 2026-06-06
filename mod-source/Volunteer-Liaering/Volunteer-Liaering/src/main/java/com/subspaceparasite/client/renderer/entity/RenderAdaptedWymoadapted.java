package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.adapted.EntityWymoAdapted;
import com.subspaceparasite.client.model.entity.ModelAdaptedWymoadapted;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for adapted_wymoAdapted.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderAdaptedWymoadapted extends GeoEntityRenderer<EntityWymoAdapted> {

    public RenderAdaptedWymoadapted(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelAdaptedWymoadapted());
    }
}
