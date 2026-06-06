package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.primitive.EntityRanrac;
import com.subspaceparasite.client.model.entity.ModelPrimitiveRanrac;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for primitive_ranrac.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderPrimitiveRanrac extends GeoEntityRenderer<EntityRanrac> {

    public RenderPrimitiveRanrac(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelPrimitiveRanrac());
    }
}
