package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.nexus.EntityRof;
import com.subspaceparasite.client.model.entity.ModelDeterrentRof;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for deterrent_rof.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderDeterrentRof extends GeoEntityRenderer<EntityRof> {

    public RenderDeterrentRof(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelDeterrentRof());
    }
}
