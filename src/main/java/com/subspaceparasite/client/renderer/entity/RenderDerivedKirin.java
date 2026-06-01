package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.derived.EntityKirin;
import com.subspaceparasite.client.model.entity.ModelDerivedKirin;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for derived_kirin.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderDerivedKirin extends GeoEntityRenderer<EntityKirin> {

    public RenderDerivedKirin(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelDerivedKirin());
    }
}
