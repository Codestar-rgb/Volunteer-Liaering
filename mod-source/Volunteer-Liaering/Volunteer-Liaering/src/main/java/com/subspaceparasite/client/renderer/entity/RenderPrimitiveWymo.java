package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.primitive.EntityWymo;
import com.subspaceparasite.client.model.entity.ModelPrimitiveWymo;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for primitive_wymo.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderPrimitiveWymo extends GeoEntityRenderer<EntityWymo> {

    public RenderPrimitiveWymo(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelPrimitiveWymo());
    }
}
