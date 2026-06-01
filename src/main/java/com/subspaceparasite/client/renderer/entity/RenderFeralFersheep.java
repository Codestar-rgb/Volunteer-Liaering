package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.feral.EntityFeralCow;
import com.subspaceparasite.client.model.entity.ModelFeralFersheep;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for feral_ferSheep.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderFeralFersheep extends GeoEntityRenderer<EntityFeralCow> {

    public RenderFeralFersheep(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelFeralFersheep());
    }
}
