package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.abomination.EntityAbominationHydra;
import com.subspaceparasite.client.model.entity.ModelAbominationAbohead;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for abomination_aboHead.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderAbominationAbohead extends GeoEntityRenderer<EntityAbominationHydra> {

    public RenderAbominationAbohead(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelAbominationAbohead());
    }
}
