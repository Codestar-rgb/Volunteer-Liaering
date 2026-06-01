package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.feral.EntityFeralIronGolem;
import com.subspaceparasite.client.model.entity.ModelFeralFerbear;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for feral_ferBear.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderFeralFerbear extends GeoEntityRenderer<EntityFeralIronGolem> {

    public RenderFeralFerbear(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelFeralFerbear());
    }
}
