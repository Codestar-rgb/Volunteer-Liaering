package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.pure.EntityAnged;
import com.subspaceparasite.client.model.entity.ModelPureTenn;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for pure_tenn.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderPureTenn extends GeoEntityRenderer<EntityAnged> {

    public RenderPureTenn(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelPureTenn());
    }
}
