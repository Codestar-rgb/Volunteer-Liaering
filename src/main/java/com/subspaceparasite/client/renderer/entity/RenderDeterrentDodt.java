package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.dispatcher.EntityDodT;
import com.subspaceparasite.client.model.entity.ModelDeterrentDodt;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for deterrent_dodT.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderDeterrentDodt extends GeoEntityRenderer<EntityDodT> {

    public RenderDeterrentDodt(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelDeterrentDodt());
    }
}
