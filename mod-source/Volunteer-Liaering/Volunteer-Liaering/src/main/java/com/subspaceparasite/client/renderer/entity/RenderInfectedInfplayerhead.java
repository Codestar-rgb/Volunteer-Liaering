package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.infected.EntityInfectedHuman;
import com.subspaceparasite.client.model.entity.ModelInfectedInfplayerhead;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for infected_infPlayerHead.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInfectedInfplayerhead extends GeoEntityRenderer<EntityInfectedHuman> {

    public RenderInfectedInfplayerhead(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInfectedInfplayerhead());
    }
}
