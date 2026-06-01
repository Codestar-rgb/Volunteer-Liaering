package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.rooter.EntityLeemB;
import com.subspaceparasite.client.model.entity.ModelDeterrentLeemsiii;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for deterrent_leemSIII.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderDeterrentLeemsiii extends GeoEntityRenderer<EntityLeemB> {

    public RenderDeterrentLeemsiii(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelDeterrentLeemsiii());
    }
}
