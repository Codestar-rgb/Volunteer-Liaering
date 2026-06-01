package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.infected.EntityInfectedHuman;
import com.subspaceparasite.client.model.entity.ModelInfectedSpehuman;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for infected_speHuman.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInfectedSpehuman extends GeoEntityRenderer<EntityInfectedHuman> {

    public RenderInfectedSpehuman(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInfectedSpehuman());
    }
}
