package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.inborn.EntityNuuh;
import com.subspaceparasite.client.model.entity.ModelInbornNuuh;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for inborn_nuuh.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderInbornNuuh extends GeoEntityRenderer<EntityNuuh> {

    public RenderInbornNuuh(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelInbornNuuh());
    }
}
