package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.infected.EntityInfectedHuman;
import com.subspaceparasite.client.model.entity.ModelInfectedInfhuman;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for infected_infHuman.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInfectedInfhuman extends GeoEntityRenderer<EntityInfectedHuman> {

    public RenderInfectedInfhuman(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInfectedInfhuman());
    }
}
