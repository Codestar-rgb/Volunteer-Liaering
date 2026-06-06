package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.feral.EntityFeralZombie;
import com.subspaceparasite.client.model.entity.ModelFeralFerpig;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for feral_ferPig.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderFeralFerpig extends GeoEntityRenderer<EntityFeralZombie> {

    public RenderFeralFerpig(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelFeralFerpig());
    }
}
