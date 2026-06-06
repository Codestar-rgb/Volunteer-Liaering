package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.feral.EntityFeralHuman;
import com.subspaceparasite.client.model.entity.ModelFeralFervillager;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for feral_ferVillager.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderFeralFervillager extends GeoEntityRenderer<EntityFeralHuman> {

    public RenderFeralFervillager(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelFeralFervillager());
    }
}
