package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.primitive.EntityIki;
import com.subspaceparasite.client.model.entity.ModelPrimitiveIki;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for primitive_iki.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderPrimitiveIki extends GeoEntityRenderer<EntityIki> {

    public RenderPrimitiveIki(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelPrimitiveIki());
    }
}
