package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.infected.EntityInfectedCow;
import com.subspaceparasite.client.model.entity.ModelInfectedSpecow;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for infected_speCow.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInfectedSpecow extends GeoEntityRenderer<EntityInfectedCow> {

    public RenderInfectedSpecow(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInfectedSpecow());
    }
}
