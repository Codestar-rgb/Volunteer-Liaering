package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.primitive.EntityEmana;
import com.subspaceparasite.client.model.entity.ModelPrimitiveEmana;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for primitive_emana.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderPrimitiveEmana extends GeoEntityRenderer<EntityEmana> {

    public RenderPrimitiveEmana(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelPrimitiveEmana());
    }
}
