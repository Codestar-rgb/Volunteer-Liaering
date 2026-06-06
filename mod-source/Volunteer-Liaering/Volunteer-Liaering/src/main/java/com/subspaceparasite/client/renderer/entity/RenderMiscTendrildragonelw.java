package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.misc.EntityParasiteLarva;
import com.subspaceparasite.client.model.entity.ModelMiscTendrildragonelw;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for misc_tendrilDragonELW.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderMiscTendrildragonelw extends GeoEntityRenderer<EntityParasiteLarva> {

    public RenderMiscTendrildragonelw(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelMiscTendrildragonelw());
    }
}
