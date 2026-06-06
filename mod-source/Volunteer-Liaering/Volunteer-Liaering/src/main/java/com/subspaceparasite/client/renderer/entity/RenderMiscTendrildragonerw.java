package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.misc.EntityParasiteLarva;
import com.subspaceparasite.client.model.entity.ModelMiscTendrildragonerw;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for misc_tendrilDragonERW.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderMiscTendrildragonerw extends GeoEntityRenderer<EntityParasiteLarva> {

    public RenderMiscTendrildragonerw(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelMiscTendrildragonerw());
    }
}
