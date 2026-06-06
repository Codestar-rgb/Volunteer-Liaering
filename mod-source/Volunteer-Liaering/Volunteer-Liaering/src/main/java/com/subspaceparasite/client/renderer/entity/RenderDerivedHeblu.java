package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.derived.EntityHeblu;
import com.subspaceparasite.client.model.entity.ModelDerivedHeblu;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for derived_heblu.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderDerivedHeblu extends GeoEntityRenderer<EntityHeblu> {

    public RenderDerivedHeblu(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelDerivedHeblu());
    }
}
