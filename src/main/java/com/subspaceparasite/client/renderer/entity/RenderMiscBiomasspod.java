package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.misc.EntityBoomOrb;
import com.subspaceparasite.client.model.entity.ModelMiscBiomasspod;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for misc_biomassPod.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderMiscBiomasspod extends GeoEntityRenderer<EntityBoomOrb> {

    public RenderMiscBiomasspod(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelMiscBiomasspod());
    }
}
