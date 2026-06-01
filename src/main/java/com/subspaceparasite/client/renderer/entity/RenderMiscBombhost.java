package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.projectile.EntityBileBomb;
import com.subspaceparasite.client.model.entity.ModelMiscBombhost;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for misc_bombHost.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderMiscBombhost extends GeoEntityRenderer<EntityBileBomb> {

    public RenderMiscBombhost(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelMiscBombhost());
    }
}
