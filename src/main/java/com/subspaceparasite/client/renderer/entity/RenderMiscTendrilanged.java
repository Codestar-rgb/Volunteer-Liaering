package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.misc.EntityParasiteLarva;
import com.subspaceparasite.client.model.entity.ModelMiscTendrilanged;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for misc_tendrilAnged.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderMiscTendrilanged extends GeoEntityRenderer<EntityParasiteLarva> {

    public RenderMiscTendrilanged(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelMiscTendrilanged());
    }
}
