package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.infected.EntityInfectedPolarBear;
import com.subspaceparasite.client.model.entity.ModelInfectedSpebear;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for infected_speBear.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInfectedSpebear extends GeoEntityRenderer<EntityInfectedPolarBear> {

    public RenderInfectedSpebear(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInfectedSpebear());
    }
}
