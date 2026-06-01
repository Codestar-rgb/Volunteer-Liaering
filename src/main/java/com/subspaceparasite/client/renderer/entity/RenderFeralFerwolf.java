package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.feral.EntityFeralWolf;
import com.subspaceparasite.client.model.entity.ModelFeralFerwolf;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for feral_ferWolf.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderFeralFerwolf extends GeoEntityRenderer<EntityFeralWolf> {

    public RenderFeralFerwolf(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelFeralFerwolf());
    }
}
