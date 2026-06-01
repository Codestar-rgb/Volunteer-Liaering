package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.rooter.EntityLeemB;
import com.subspaceparasite.client.model.entity.ModelDeterrentLeemsiv;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for deterrent_leemSIV.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderDeterrentLeemsiv extends GeoEntityRenderer<EntityLeemB> {

    public RenderDeterrentLeemsiv(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelDeterrentLeemsiv());
    }
}
