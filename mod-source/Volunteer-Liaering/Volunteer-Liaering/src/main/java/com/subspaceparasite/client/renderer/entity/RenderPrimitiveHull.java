package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.primitive.EntityHull;
import com.subspaceparasite.client.model.entity.ModelPrimitiveHull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for primitive_hull.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderPrimitiveHull extends GeoEntityRenderer<EntityHull> {

    public RenderPrimitiveHull(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelPrimitiveHull());
    }
}
