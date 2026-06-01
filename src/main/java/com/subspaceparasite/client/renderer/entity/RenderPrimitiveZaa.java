package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.primitive.EntityZaa;
import com.subspaceparasite.client.model.entity.ModelPrimitiveZaa;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for primitive_zaa.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderPrimitiveZaa extends GeoEntityRenderer<EntityZaa> {

    public RenderPrimitiveZaa(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelPrimitiveZaa());
    }
}
