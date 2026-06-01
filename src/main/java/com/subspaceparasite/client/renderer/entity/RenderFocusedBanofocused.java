package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.adapted.EntityBanoAdapted;
import com.subspaceparasite.client.model.entity.ModelFocusedBanofocused;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for focused_banoFocused.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderFocusedBanofocused extends GeoEntityRenderer<EntityBanoAdapted> {

    public RenderFocusedBanofocused(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelFocusedBanofocused());
    }
}
