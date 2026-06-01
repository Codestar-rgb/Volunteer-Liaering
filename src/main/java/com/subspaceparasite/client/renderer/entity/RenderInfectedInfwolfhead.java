package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.infected.EntityInfectedWolf;
import com.subspaceparasite.client.model.entity.ModelInfectedInfwolfhead;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for infected_infWolfHead.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInfectedInfwolfhead extends GeoEntityRenderer<EntityInfectedWolf> {

    public RenderInfectedInfwolfhead(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInfectedInfwolfhead());
    }
}
