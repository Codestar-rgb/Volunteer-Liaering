package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.infected.EntityInfectedBat;
import com.subspaceparasite.client.model.entity.ModelInfectedInfsquid;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for infected_infSquid.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInfectedInfsquid extends GeoEntityRenderer<EntityInfectedBat> {

    public RenderInfectedInfsquid(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInfectedInfsquid());
    }
}
