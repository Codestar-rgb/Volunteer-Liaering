package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.infected.EntityInfectedSheep;
import com.subspaceparasite.client.model.entity.ModelInfectedInfsheep;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for infected_infSheep.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInfectedInfsheep extends GeoEntityRenderer<EntityInfectedSheep> {

    public RenderInfectedInfsheep(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInfectedInfsheep());
    }
}
