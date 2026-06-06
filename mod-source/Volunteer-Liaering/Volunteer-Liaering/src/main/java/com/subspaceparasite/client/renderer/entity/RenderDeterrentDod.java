package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.deterrent.EntityDeterrentSentry;
import com.subspaceparasite.client.model.entity.ModelDeterrentDod;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for deterrent_dod.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderDeterrentDod extends GeoEntityRenderer<EntityDeterrentSentry> {

    public RenderDeterrentDod(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelDeterrentDod());
    }
}
