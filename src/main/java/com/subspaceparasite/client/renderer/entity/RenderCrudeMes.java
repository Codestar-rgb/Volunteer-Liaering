package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.crude.EntityMes;
import com.subspaceparasite.client.model.entity.ModelCrudeMes;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for crude_mes.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderCrudeMes extends GeoEntityRenderer<EntityMes> {

    public RenderCrudeMes(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelCrudeMes());
    }
}
