package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.crude.EntityInhooS;
import com.subspaceparasite.client.model.entity.ModelCrudeInhoos;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for crude_inhooS.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderCrudeInhoos extends GeoEntityRenderer<EntityInhooS> {

    public RenderCrudeInhoos(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelCrudeInhoos());
    }
}
