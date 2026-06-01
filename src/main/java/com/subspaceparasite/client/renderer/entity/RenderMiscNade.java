package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.projectile.EntityBileBomb;
import com.subspaceparasite.client.model.entity.ModelMiscNade;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for misc_nade.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderMiscNade extends GeoEntityRenderer<EntityBileBomb> {

    public RenderMiscNade(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelMiscNade());
    }
}
