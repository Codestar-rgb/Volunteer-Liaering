package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.infected.EntityInfectedPig;
import com.subspaceparasite.client.model.entity.ModelInfectedInfpig;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for infected_infPig.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInfectedInfpig extends GeoEntityRenderer<EntityInfectedPig> {

    public RenderInfectedInfpig(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInfectedInfpig());
    }
}
