package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.crude.EntityCruxB;
import com.subspaceparasite.client.model.entity.ModelCrudeCruxb;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for crude_cruxB.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderCrudeCruxb extends GeoEntityRenderer<EntityCruxB> {

    public RenderCrudeCruxb(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelCrudeCruxb());
    }
}
