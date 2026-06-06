package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.infected.EntityInfectedWolf;
import com.subspaceparasite.client.model.entity.ModelInfectedInfwolf;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for infected_infWolf.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInfectedInfwolf extends GeoEntityRenderer<EntityInfectedWolf> {

    public RenderInfectedInfwolf(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInfectedInfwolf());
    }
}
