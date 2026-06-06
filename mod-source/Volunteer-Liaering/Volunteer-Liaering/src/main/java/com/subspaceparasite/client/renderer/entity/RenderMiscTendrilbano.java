package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.misc.EntityParasiteLarva;
import com.subspaceparasite.client.model.entity.ModelMiscTendrilbano;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for misc_tendrilBano.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderMiscTendrilbano extends GeoEntityRenderer<EntityParasiteLarva> {

    public RenderMiscTendrilbano(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelMiscTendrilbano());
    }
}
