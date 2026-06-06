package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.infected.EntityInfectedVillager;
import com.subspaceparasite.client.model.entity.ModelInfectedInfvillagerhead;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for infected_infVillagerHead.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInfectedInfvillagerhead extends GeoEntityRenderer<EntityInfectedVillager> {

    public RenderInfectedInfvillagerhead(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInfectedInfvillagerhead());
    }
}
