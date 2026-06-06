package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.infected.EntityInfectedPolarBear;
import com.subspaceparasite.client.model.entity.ModelInfectedInfbear;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for infected_infBear.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInfectedInfbear extends GeoEntityRenderer<EntityInfectedPolarBear> {

    public RenderInfectedInfbear(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInfectedInfbear());
    }
}
