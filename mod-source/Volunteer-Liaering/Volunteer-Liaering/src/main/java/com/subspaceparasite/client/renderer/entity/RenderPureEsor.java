package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.pure.EntityEsor;
import com.subspaceparasite.client.model.entity.ModelPureEsor;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for pure_esor.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderPureEsor extends GeoEntityRenderer<EntityEsor> {

    public RenderPureEsor(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelPureEsor());
    }
}
