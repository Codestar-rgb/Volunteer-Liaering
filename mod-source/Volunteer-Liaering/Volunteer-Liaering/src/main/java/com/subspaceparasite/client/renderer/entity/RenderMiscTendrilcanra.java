package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.misc.EntityParasiteLarva;
import com.subspaceparasite.client.model.entity.ModelMiscTendrilcanra;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for misc_tendrilCanra.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderMiscTendrilcanra extends GeoEntityRenderer<EntityParasiteLarva> {

    public RenderMiscTendrilcanra(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelMiscTendrilcanra());
    }
}
