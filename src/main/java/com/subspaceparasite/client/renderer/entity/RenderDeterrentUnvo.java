package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.nexus.EntityUnvo;
import com.subspaceparasite.client.model.entity.ModelDeterrentUnvo;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for deterrent_unvo.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderDeterrentUnvo extends GeoEntityRenderer<EntityUnvo> {

    public RenderDeterrentUnvo(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelDeterrentUnvo());
    }
}
