package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.misc.EntityParasiteLarva;
import com.subspaceparasite.client.model.entity.ModelMiscTendrilesor;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for misc_tendrilEsor.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderMiscTendrilesor extends GeoEntityRenderer<EntityParasiteLarva> {

    public RenderMiscTendrilesor(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelMiscTendrilesor());
    }
}
