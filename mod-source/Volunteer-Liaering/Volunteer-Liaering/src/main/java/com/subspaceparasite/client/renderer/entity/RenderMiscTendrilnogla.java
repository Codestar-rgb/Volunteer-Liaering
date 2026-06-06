package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.misc.EntityParasiteLarva;
import com.subspaceparasite.client.model.entity.ModelMiscTendrilnogla;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for misc_tendrilNogla.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderMiscTendrilnogla extends GeoEntityRenderer<EntityParasiteLarva> {

    public RenderMiscTendrilnogla(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelMiscTendrilnogla());
    }
}
