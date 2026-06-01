package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.crude.EntityCruxA;
import com.subspaceparasite.client.model.entity.ModelCrudeCruxa;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for crude_cruxA.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderCrudeCruxa extends GeoEntityRenderer<EntityCruxA> {

    public RenderCrudeCruxa(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelCrudeCruxa());
    }
}
