package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.adapted.EntityShycoAdapted;
import com.subspaceparasite.client.model.entity.ModelFocusedShycofocused;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for focused_shycoFocused.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderFocusedShycofocused extends GeoEntityRenderer<EntityShycoAdapted> {

    public RenderFocusedShycofocused(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelFocusedShycofocused());
    }
}
