package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.infected.EntityInfectedHuman;
import com.subspaceparasite.client.model.entity.ModelInfectedInfhumanhead;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for infected_infHumanHead.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInfectedInfhumanhead extends GeoEntityRenderer<EntityInfectedHuman> {

    public RenderInfectedInfhumanhead(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInfectedInfhumanhead());
    }
}
