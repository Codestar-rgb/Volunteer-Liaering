package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.adapted.EntityNoglaAdapted;
import com.subspaceparasite.client.model.entity.ModelAdaptedNoglaadapted;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for adapted_noglaAdapted.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderAdaptedNoglaadapted extends GeoEntityRenderer<EntityNoglaAdapted> {

    public RenderAdaptedNoglaadapted(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelAdaptedNoglaadapted());
    }
}
