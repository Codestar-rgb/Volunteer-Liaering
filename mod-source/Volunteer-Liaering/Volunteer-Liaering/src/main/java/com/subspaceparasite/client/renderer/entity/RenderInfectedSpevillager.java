package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.infected.EntityInfectedVillager;
import com.subspaceparasite.client.model.entity.ModelInfectedSpevillager;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for infected_speVillager.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInfectedSpevillager extends GeoEntityRenderer<EntityInfectedVillager> {

    public RenderInfectedSpevillager(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInfectedSpevillager());
    }
}
