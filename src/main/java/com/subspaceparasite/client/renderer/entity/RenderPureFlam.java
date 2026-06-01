package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.pure.EntityFlam;
import com.subspaceparasite.client.model.entity.ModelPureFlam;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for pure_flam.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderPureFlam extends GeoEntityRenderer<EntityFlam> {

    public RenderPureFlam(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelPureFlam());
    }
}
