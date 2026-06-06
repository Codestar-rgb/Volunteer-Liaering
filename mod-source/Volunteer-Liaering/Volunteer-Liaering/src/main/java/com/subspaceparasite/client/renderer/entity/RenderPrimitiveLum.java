package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.primitive.EntityLum;
import com.subspaceparasite.client.model.entity.ModelPrimitiveLum;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for primitive_lum.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderPrimitiveLum extends GeoEntityRenderer<EntityLum> {

    public RenderPrimitiveLum(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelPrimitiveLum());
    }
}
