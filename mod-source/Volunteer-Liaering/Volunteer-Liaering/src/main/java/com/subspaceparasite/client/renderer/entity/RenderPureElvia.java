package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.pure.EntityElvia;
import com.subspaceparasite.client.model.entity.ModelPureElvia;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for pure_elvia.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderPureElvia extends GeoEntityRenderer<EntityElvia> {

    public RenderPureElvia(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelPureElvia());
    }
}
