package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.hijacked.EntityHijackedSkeleton;
import com.subspaceparasite.client.model.entity.ModelHijackedHiskeleton;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for hijacked_hiSkeleton.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderHijackedHiskeleton extends GeoEntityRenderer<EntityHijackedSkeleton> {

    public RenderHijackedHiskeleton(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelHijackedHiskeleton());
    }
}
