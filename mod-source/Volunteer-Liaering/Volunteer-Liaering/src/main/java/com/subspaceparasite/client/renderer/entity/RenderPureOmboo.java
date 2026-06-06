package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.pure.EntityOmboo;
import com.subspaceparasite.client.model.entity.ModelPureOmboo;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for pure_omboo.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderPureOmboo extends GeoEntityRenderer<EntityOmboo> {

    public RenderPureOmboo(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelPureOmboo());
    }
}
