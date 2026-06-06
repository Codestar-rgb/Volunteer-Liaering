package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.feral.EntityFeralCow;
import com.subspaceparasite.client.model.entity.ModelFeralFerhorse;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for feral_ferHorse.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderFeralFerhorse extends GeoEntityRenderer<EntityFeralCow> {

    public RenderFeralFerhorse(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelFeralFerhorse());
    }
}
