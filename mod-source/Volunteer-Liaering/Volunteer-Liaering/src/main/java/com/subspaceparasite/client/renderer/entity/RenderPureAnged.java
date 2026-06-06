package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.pure.EntityAnged;
import com.subspaceparasite.client.model.entity.ModelPureAnged;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for pure_anged.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderPureAnged extends GeoEntityRenderer<EntityAnged> {

    public RenderPureAnged(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelPureAnged());
    }
}
