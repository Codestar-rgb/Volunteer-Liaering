package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.projectile.EntityBileBomb;
import com.subspaceparasite.client.model.entity.ModelMiscBombjinjo;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for misc_bombJinjo.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderMiscBombjinjo extends GeoEntityRenderer<EntityBileBomb> {

    public RenderMiscBombjinjo(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelMiscBombjinjo());
    }
}
