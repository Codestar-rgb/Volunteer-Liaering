package com.subspaceparasite.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.subspaceparasite.common.entity.monster.primitive.EntityNogla;
import com.subspaceparasite.client.model.entity.ModelParasiteGeo;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

/**
 * GeckoLib renderer for EntityNogla (Primitive stage parasite with stealth capabilities).
 * Handles rendering with GeckoLib animations, textures, cloaking effects, and ambush visuals.
 */
public class RenderNoglaGeo extends GeoEntityRenderer<EntityNogla> {

    private static final ResourceLocation DEFAULT_TEXTURE =
        new ResourceLocation("subspaceparasite", "primitive/nogla.png");

    public RenderNoglaGeo(EntityRendererProvider.Context context) {
        super(context, new ModelParasiteGeo<>("primitive/nogla", "primitive/nogla.png"));
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public void preRender(PoseStack poseStack, EntityNogla entity, float partialTick,
                         MultiBufferSource bufferSource, VertexConsumer buffer,
                         int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, entity, partialTick, bufferSource, buffer,
                       packedLight, packedOverlay, red, green, blue, alpha);

        // Apply scale modifiers from entity
        float scale = entity.getScaleModifier();
        float animScale = entity.getScaleAnimation(partialTick);
        float totalScale = scale * animScale;

        if (totalScale != 1.0f) {
            poseStack.scale(totalScale, totalScale, totalScale);
        }

        // Apply body animation progress
        float bodyAnim = entity.getBodyAnimationProgress();
        if (bodyAnim != 0.0f) {
            float breathScale = 1.0f + (bodyAnim * 0.1f);
            poseStack.scale(breathScale, breathScale, breathScale);
        }

        // Apply cloaking effect - primary feature of Nogla
        float cloakLevel = entity.getCloakingLevel();
        if (cloakLevel > 0.0f) {
            float cloakScale = 1.0f - (cloakLevel * 0.15f);
            poseStack.scale(cloakScale, cloakScale, cloakScale);

            // Reduce alpha for transparency based on cloak level
            alpha = Math.max(0.2f, 1.0f - cloakLevel);
        }

        // Apply swelling effect
        float swellAmount = entity.getSwellAmount();
        if (swellAmount > 0.0f) {
            float swellScale = 1.0f + (swellAmount * 0.5f);
            poseStack.scale(swellScale, swellScale, swellScale);
        }
    }

    @Override
    public RenderType getRenderType(EntityNogla entity, float partialTick, PoseStack poseStack,
                                   MultiBufferSource bufferSource, VertexConsumer buffer,
                                   int packedLight, ResourceLocation texture) {
        // Handle cloaking transparency
        float cloakLevel = entity.getCloakingLevel();
        if (cloakLevel >= 0.95f) {
            return null; // Fully cloaked, don't render
        }

        if (cloakLevel > 0.5f) {
            return RenderType.entityTranslucent(texture);
        }

        return super.getRenderType(entity, partialTick, poseStack, bufferSource,
                                  buffer, packedLight, texture);
    }

    @Override
    protected float getDeathMaxRotation(EntityNogla entity) {
        return entity.getDeathMaxRotation();
    }

    @Override
    public ResourceLocation getTextureLocation(EntityNogla entity) {
        ResourceLocation customTexture = entity.getCustomTexture();
        if (customTexture != null) {
            return customTexture;
        }

        byte variant = entity.getSkinVariant();
        if (variant > 0) {
            // Variant-specific textures can be added later
        }

        return DEFAULT_TEXTURE;
    }
}
