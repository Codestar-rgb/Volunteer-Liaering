package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.hijacked.EntityHijackedCreeper;
import com.subspaceparasite.client.model.entity.ModelHijackedHiblaze;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for hijacked_hiBlaze.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderHijackedHiblaze extends GeoEntityRenderer<EntityHijackedCreeper> {

    public RenderHijackedHiblaze(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelHijackedHiblaze());
    }
}
