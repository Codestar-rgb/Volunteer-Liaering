package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.adapted.EntityBanoAdapted;
import com.subspaceparasite.client.model.entity.ModelAdaptedBanoadapted;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for adapted_banoAdapted.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderAdaptedBanoadapted extends GeoEntityRenderer<EntityBanoAdapted> {

    public RenderAdaptedBanoadapted(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelAdaptedBanoadapted());
    }
}
