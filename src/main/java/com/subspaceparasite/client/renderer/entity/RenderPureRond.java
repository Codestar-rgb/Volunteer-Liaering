package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.pure.EntityGanro;
import com.subspaceparasite.client.model.entity.ModelPureRond;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for pure_rond.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderPureRond extends GeoEntityRenderer<EntityGanro> {

    public RenderPureRond(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelPureRond());
    }
}
