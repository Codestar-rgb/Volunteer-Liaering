package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.ancient.EntityAncientLeviathan;
import com.subspaceparasite.client.model.entity.ModelAncientTerla;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for ancient_terla.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderAncientTerla extends GeoEntityRenderer<EntityAncientLeviathan> {

    public RenderAncientTerla(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelAncientTerla());
    }
}
