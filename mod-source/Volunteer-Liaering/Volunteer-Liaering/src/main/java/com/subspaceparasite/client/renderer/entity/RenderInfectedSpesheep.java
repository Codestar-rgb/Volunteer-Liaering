package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.infected.EntityInfectedSheep;
import com.subspaceparasite.client.model.entity.ModelInfectedSpesheep;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for infected_speSheep.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInfectedSpesheep extends GeoEntityRenderer<EntityInfectedSheep> {

    public RenderInfectedSpesheep(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInfectedSpesheep());
    }
}
