package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.feral.EntityFeralHuman;
import com.subspaceparasite.client.model.entity.ModelFeralFerhuman;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for feral_ferHuman.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderFeralFerhuman extends GeoEntityRenderer<EntityFeralHuman> {

    public RenderFeralFerhuman(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelFeralFerhuman());
    }
}
