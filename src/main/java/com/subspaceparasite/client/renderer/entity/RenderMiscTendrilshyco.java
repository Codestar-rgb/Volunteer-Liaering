package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.misc.EntityParasiteLarva;
import com.subspaceparasite.client.model.entity.ModelMiscTendrilshyco;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for misc_tendrilShyco.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderMiscTendrilshyco extends GeoEntityRenderer<EntityParasiteLarva> {

    public RenderMiscTendrilshyco(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelMiscTendrilshyco());
    }
}
