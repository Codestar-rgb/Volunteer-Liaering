package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.crude.EntityLeer;
import com.subspaceparasite.client.model.entity.ModelCrudeLeer;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for crude_leer.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderCrudeLeer extends GeoEntityRenderer<EntityLeer> {

    public RenderCrudeLeer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelCrudeLeer());
    }
}
