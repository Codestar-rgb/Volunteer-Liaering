package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.pure.EntityGanro;
import com.subspaceparasite.client.model.entity.ModelPurePheon;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for pure_pheon.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderPurePheon extends GeoEntityRenderer<EntityGanro> {

    public RenderPurePheon(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelPurePheon());
    }
}
