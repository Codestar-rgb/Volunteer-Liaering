package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.infected.EntityInfectedSheep;
import com.subspaceparasite.client.model.entity.ModelInfectedInfsheephead;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for infected_infSheepHead.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInfectedInfsheephead extends GeoEntityRenderer<EntityInfectedSheep> {

    public RenderInfectedInfsheephead(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInfectedInfsheephead());
    }
}
