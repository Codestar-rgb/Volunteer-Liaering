package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.primitive.EntityCanra;
import com.subspaceparasite.client.model.entity.ModelPrimitiveCanra;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for primitive_canra.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderPrimitiveCanra extends GeoEntityRenderer<EntityCanra> {

    public RenderPrimitiveCanra(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelPrimitiveCanra());
    }
}
