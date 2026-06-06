package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.pure.EntityGanro;
import com.subspaceparasite.client.model.entity.ModelPureGanro;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for pure_ganro.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderPureGanro extends GeoEntityRenderer<EntityGanro> {

    public RenderPureGanro(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelPureGanro());
    }
}
