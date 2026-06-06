package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.ancient.EntityAncientDreadnought;
import com.subspaceparasite.client.model.entity.ModelAwakenedOroncoawfl;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for awakened_oroncoAWFL.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderAwakenedOroncoawfl extends GeoEntityRenderer<EntityAncientDreadnought> {

    public RenderAwakenedOroncoawfl(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelAwakenedOroncoawfl());
    }
}
