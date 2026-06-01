package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.infected.EntityInfectedEnderman;
import com.subspaceparasite.client.model.entity.ModelInfectedInfdragonehead;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for infected_infDragonEHead.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInfectedInfdragonehead extends GeoEntityRenderer<EntityInfectedEnderman> {

    public RenderInfectedInfdragonehead(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInfectedInfdragonehead());
    }
}
