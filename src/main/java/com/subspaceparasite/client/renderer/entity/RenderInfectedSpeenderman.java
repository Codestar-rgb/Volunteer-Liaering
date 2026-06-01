package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.infected.EntityInfectedEnderman;
import com.subspaceparasite.client.model.entity.ModelInfectedSpeenderman;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for infected_speEnderman.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInfectedSpeenderman extends GeoEntityRenderer<EntityInfectedEnderman> {

    public RenderInfectedSpeenderman(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInfectedSpeenderman());
    }
}
