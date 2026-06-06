package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.hijacked.EntityHijackedRavager;
import com.subspaceparasite.client.model.entity.ModelHijackedHigolem;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for hijacked_hiGolem.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderHijackedHigolem extends GeoEntityRenderer<EntityHijackedRavager> {

    public RenderHijackedHigolem(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelHijackedHigolem());
    }
}
