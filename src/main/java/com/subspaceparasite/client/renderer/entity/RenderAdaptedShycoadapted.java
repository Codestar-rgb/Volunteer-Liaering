package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.adapted.EntityShycoAdapted;
import com.subspaceparasite.client.model.entity.ModelAdaptedShycoadapted;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for adapted_shycoAdapted.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderAdaptedShycoadapted extends GeoEntityRenderer<EntityShycoAdapted> {

    public RenderAdaptedShycoadapted(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelAdaptedShycoadapted());
    }
}
