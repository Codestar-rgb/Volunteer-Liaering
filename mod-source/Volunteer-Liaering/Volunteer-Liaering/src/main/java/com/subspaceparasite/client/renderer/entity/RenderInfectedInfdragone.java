package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.infected.EntityInfectedEnderman;
import com.subspaceparasite.client.model.entity.ModelInfectedInfdragone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for infected_infDragonE.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInfectedInfdragone extends GeoEntityRenderer<EntityInfectedEnderman> {

    public RenderInfectedInfdragone(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInfectedInfdragone());
    }
}
