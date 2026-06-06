package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.primitive.EntityShyco;
import com.subspaceparasite.client.model.entity.ModelPrimitiveShyco;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for primitive_shyco.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderPrimitiveShyco extends GeoEntityRenderer<EntityShyco> {

    public RenderPrimitiveShyco(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelPrimitiveShyco());
    }
}
