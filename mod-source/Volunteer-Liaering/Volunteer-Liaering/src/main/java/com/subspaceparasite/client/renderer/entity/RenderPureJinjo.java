package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.pure.EntityOmboo;
import com.subspaceparasite.client.model.entity.ModelPureJinjo;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for pure_jinjo.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderPureJinjo extends GeoEntityRenderer<EntityOmboo> {

    public RenderPureJinjo(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelPureJinjo());
    }
}
