package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.abomination.EntityAbominationAmalgam;
import com.subspaceparasite.client.model.entity.ModelAbominationAbobodies;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for abomination_aboBodies.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderAbominationAbobodies extends GeoEntityRenderer<EntityAbominationAmalgam> {

    public RenderAbominationAbobodies(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelAbominationAbobodies());
    }
}
