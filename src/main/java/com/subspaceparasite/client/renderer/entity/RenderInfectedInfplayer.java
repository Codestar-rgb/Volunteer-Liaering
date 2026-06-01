package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.infected.EntityInfectedHuman;
import com.subspaceparasite.client.model.entity.ModelInfectedInfplayer;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for infected_infPlayer.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInfectedInfplayer extends GeoEntityRenderer<EntityInfectedHuman> {

    public RenderInfectedInfplayer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInfectedInfplayer());
    }
}
