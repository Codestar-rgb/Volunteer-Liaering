package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.primitive.EntityBano;
import com.subspaceparasite.client.model.entity.ModelPrimitiveBano;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for primitive_bano.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderPrimitiveBano extends GeoEntityRenderer<EntityBano> {

    public RenderPrimitiveBano(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelPrimitiveBano());
    }
}
