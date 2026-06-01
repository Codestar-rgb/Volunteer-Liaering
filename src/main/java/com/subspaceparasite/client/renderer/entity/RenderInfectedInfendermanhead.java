package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.infected.EntityInfectedEnderman;
import com.subspaceparasite.client.model.entity.ModelInfectedInfendermanhead;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for infected_infEndermanHead.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInfectedInfendermanhead extends GeoEntityRenderer<EntityInfectedEnderman> {

    public RenderInfectedInfendermanhead(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInfectedInfendermanhead());
    }
}
