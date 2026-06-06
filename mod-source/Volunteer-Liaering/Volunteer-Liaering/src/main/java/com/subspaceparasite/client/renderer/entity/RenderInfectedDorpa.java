package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.infected.EntityInfectedPig;
import com.subspaceparasite.client.model.entity.ModelInfectedDorpa;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for infected_dorpa.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInfectedDorpa extends GeoEntityRenderer<EntityInfectedPig> {

    public RenderInfectedDorpa(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInfectedDorpa());
    }
}
