package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.ancient.EntityAncientDreadnought;
import com.subspaceparasite.client.model.entity.ModelAncientOroncoten;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for ancient_oroncoTen.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderAncientOroncoten extends GeoEntityRenderer<EntityAncientDreadnought> {

    public RenderAncientOroncoten(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelAncientOroncoten());
    }
}
