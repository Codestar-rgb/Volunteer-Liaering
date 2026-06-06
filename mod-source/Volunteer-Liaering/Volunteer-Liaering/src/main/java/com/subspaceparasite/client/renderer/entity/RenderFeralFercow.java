package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.feral.EntityFeralCow;
import com.subspaceparasite.client.model.entity.ModelFeralFercow;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for feral_ferCow.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderFeralFercow extends GeoEntityRenderer<EntityFeralCow> {

    public RenderFeralFercow(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelFeralFercow());
    }
}
