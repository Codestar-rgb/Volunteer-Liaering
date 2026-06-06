package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.misc.EntityVoidOrb;
import com.subspaceparasite.client.model.entity.ModelMiscNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for misc_nULL.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderMiscNull extends GeoEntityRenderer<EntityVoidOrb> {

    public RenderMiscNull(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelMiscNull());
    }
}
