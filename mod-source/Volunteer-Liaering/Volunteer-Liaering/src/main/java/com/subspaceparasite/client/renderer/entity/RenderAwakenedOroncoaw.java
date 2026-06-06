package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.ancient.EntityAncientColossus;
import com.subspaceparasite.client.model.entity.ModelAwakenedOroncoaw;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for awakened_oroncoAW.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderAwakenedOroncoaw extends GeoEntityRenderer<EntityAncientColossus> {

    public RenderAwakenedOroncoaw(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelAwakenedOroncoaw());
    }
}
