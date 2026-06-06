package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.misc.EntityParasiteLarva;
import com.subspaceparasite.client.model.entity.ModelMiscGore;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for misc_gore.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderMiscGore extends GeoEntityRenderer<EntityParasiteLarva> {

    public RenderMiscGore(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelMiscGore());
    }
}
