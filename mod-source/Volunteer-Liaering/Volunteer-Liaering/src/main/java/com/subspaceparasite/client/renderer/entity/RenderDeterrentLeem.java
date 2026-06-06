package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.rooter.EntityLeemB;
import com.subspaceparasite.client.model.entity.ModelDeterrentLeem;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for deterrent_leem.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderDeterrentLeem extends GeoEntityRenderer<EntityLeemB> {

    public RenderDeterrentLeem(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelDeterrentLeem());
    }
}
