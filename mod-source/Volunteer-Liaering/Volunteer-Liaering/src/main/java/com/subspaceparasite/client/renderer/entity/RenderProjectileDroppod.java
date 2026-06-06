package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.projectile.EntityBeckonBlast;
import com.subspaceparasite.client.model.entity.ModelProjectileDroppod;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for projectile_dropPod.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderProjectileDroppod extends GeoEntityRenderer<EntityBeckonBlast> {

    public RenderProjectileDroppod(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelProjectileDroppod());
    }
}
