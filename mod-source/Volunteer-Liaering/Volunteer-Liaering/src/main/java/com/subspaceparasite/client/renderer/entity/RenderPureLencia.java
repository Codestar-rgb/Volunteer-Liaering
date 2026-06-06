package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.pure.EntityEsor;
import com.subspaceparasite.client.model.entity.ModelPureLencia;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for pure_lencia.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderPureLencia extends GeoEntityRenderer<EntityEsor> {

    public RenderPureLencia(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelPureLencia());
    }
}
