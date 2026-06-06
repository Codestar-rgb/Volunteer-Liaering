package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.rooter.EntityLeemB;
import com.subspaceparasite.client.model.entity.ModelDeterrentLeemsii;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for deterrent_leemSII.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderDeterrentLeemsii extends GeoEntityRenderer<EntityLeemB> {

    public RenderDeterrentLeemsii(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelDeterrentLeemsii());
    }
}
