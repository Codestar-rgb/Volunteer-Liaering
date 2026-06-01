package com.subspaceparasite.client.renderer.entity;

import com.subspaceparasite.common.entity.monster.misc.EntityBoomOrb;
import com.subspaceparasite.client.model.entity.ModelMiscBiomassvenkrol;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * GeckoLib renderer for misc_biomassVenkrol.
 * Generated from MinecraftModelMigrator-Pro-GeckoLib resource mapping.
 */
public class RenderMiscBiomassvenkrol extends GeoEntityRenderer<EntityBoomOrb> {

    public RenderMiscBiomassvenkrol(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelMiscBiomassvenkrol());
    }
}
