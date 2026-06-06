package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.pure.EntityFlog;
import com.subspaceparasite.client.model.entity.ModelPureOrch;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for pure_orch.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderPureOrch extends GeoEntityRenderer<EntityFlog> {

    public RenderPureOrch(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelPureOrch());
    }
}
