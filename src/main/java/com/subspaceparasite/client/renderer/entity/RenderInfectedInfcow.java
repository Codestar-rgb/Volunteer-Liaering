package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.infected.EntityInfectedCow;
import com.subspaceparasite.client.model.entity.ModelInfectedInfcow;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for infected_infCow.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInfectedInfcow extends GeoEntityRenderer<EntityInfectedCow> {

    public RenderInfectedInfcow(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInfectedInfcow());
    }
}
