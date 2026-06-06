package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.primitive.EntityNogla;
import com.subspaceparasite.client.model.entity.ModelPrimitiveNogla;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for primitive_nogla.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderPrimitiveNogla extends GeoEntityRenderer<EntityNogla> {

    public RenderPrimitiveNogla(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelPrimitiveNogla());
    }
}
