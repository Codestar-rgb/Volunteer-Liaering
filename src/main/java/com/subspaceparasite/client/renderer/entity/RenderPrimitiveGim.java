package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.primitive.EntityGim;
import com.subspaceparasite.client.model.entity.ModelPrimitiveGim;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for primitive_gim.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderPrimitiveGim extends GeoEntityRenderer<EntityGim> {

    public RenderPrimitiveGim(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelPrimitiveGim());
    }
}
