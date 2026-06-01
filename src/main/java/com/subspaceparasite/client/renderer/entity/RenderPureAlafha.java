package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.pure.EntityAlafha;
import com.subspaceparasite.client.model.entity.ModelPureAlafha;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for pure_alafha.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderPureAlafha extends GeoEntityRenderer<EntityAlafha> {

    public RenderPureAlafha(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelPureAlafha());
    }
}
