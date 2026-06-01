package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.nexus.EntityTonro;
import com.subspaceparasite.client.model.entity.ModelDeterrentTonro;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for deterrent_tonro.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderDeterrentTonro extends GeoEntityRenderer<EntityTonro> {

    public RenderDeterrentTonro(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelDeterrentTonro());
    }
}
