package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.crude.EntityInhooM;
import com.subspaceparasite.client.model.entity.ModelCrudeInhoom;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for crude_inhooM.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderCrudeInhoom extends GeoEntityRenderer<EntityInhooM> {

    public RenderCrudeInhoom(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelCrudeInhoom());
    }
}
