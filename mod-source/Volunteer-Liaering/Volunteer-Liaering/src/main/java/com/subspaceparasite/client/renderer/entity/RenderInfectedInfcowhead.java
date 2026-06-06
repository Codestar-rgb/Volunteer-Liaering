package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.infected.EntityInfectedCow;
import com.subspaceparasite.client.model.entity.ModelInfectedInfcowhead;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for infected_infCowHead.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInfectedInfcowhead extends GeoEntityRenderer<EntityInfectedCow> {

    public RenderInfectedInfcowhead(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInfectedInfcowhead());
    }
}
