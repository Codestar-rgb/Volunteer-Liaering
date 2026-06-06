package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.infected.EntityInfectedHorse;
import com.subspaceparasite.client.model.entity.ModelInfectedInfhorse;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for infected_infHorse.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInfectedInfhorse extends GeoEntityRenderer<EntityInfectedHorse> {

    public RenderInfectedInfhorse(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInfectedInfhorse());
    }
}
