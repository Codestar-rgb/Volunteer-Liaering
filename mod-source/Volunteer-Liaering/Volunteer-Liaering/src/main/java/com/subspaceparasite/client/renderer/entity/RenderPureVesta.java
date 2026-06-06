package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.pure.EntityFlam;
import com.subspaceparasite.client.model.entity.ModelPureVesta;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for pure_vesta.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderPureVesta extends GeoEntityRenderer<EntityFlam> {

    public RenderPureVesta(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelPureVesta());
    }
}
