package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.projectile.EntityBileBomb;
import com.subspaceparasite.client.model.entity.ModelMiscBombomboo;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for misc_bombOmboo.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderMiscBombomboo extends GeoEntityRenderer<EntityBileBomb> {

    public RenderMiscBombomboo(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelMiscBombomboo());
    }
}
