package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.adapted.EntityLumAdapted;
import com.subspaceparasite.client.model.entity.ModelAdaptedLumadapted;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for adapted_lumAdapted.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderAdaptedLumadapted extends GeoEntityRenderer<EntityLumAdapted> {

    public RenderAdaptedLumadapted(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelAdaptedLumadapted());
    }
}
