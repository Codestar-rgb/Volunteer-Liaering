package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.nexus.EntityNak;
import com.subspaceparasite.client.model.entity.ModelDeterrentNak;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for deterrent_nak.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderDeterrentNak extends GeoEntityRenderer<EntityNak> {

    public RenderDeterrentNak(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelDeterrentNak());
    }
}
