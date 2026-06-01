package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.infected.EntityInfectedVillager;
import com.subspaceparasite.client.model.entity.ModelInfectedInfvillager;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for infected_infVillager.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInfectedInfvillager extends GeoEntityRenderer<EntityInfectedVillager> {

    public RenderInfectedInfvillager(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInfectedInfvillager());
    }
}
