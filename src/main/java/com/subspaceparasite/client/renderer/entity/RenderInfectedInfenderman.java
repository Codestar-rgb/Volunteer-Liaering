package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.infected.EntityInfectedEnderman;
import com.subspaceparasite.client.model.entity.ModelInfectedInfenderman;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for infected_infEnderman.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInfectedInfenderman extends GeoEntityRenderer<EntityInfectedEnderman> {

    public RenderInfectedInfenderman(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInfectedInfenderman());
    }
}
