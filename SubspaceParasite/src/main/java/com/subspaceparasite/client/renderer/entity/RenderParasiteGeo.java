package com.subspaceparasite.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.client.model.entity.ModelParasiteGeo;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

/**
 * Base GeoRenderer for all parasite entities using GeckoLib.
 * Handles rendering with GeckoLib animations, overlays, and effects.
 */
public class RenderParasiteGeo<T extends EntityParasiteBase> extends GeoEntityRenderer<T> {

    private static final ResourceLocation GLOW_TEXTURE = 
        new ResourceLocation("subspaceparasite", "textures/entity/layer/parasite_glow.png");
    
    private static final ResourceLocation LEADER_AURA_TEXTURE = 
        new ResourceLocation("subspaceparasite", "textures/entity/layer/leader_aura.png");
    
    private static final ResourceLocation INFECTION_OVERLAY_TEXTURE = 
        new ResourceLocation("subspaceparasite", "textures/entity/layer/infection_overlay.png");

    public RenderParasiteGeo(EntityRendererProvider.Context context, ModelParasiteGeo<T> model) {
        super(context, model);
        // Auto-glowing layer for emissive parts
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public void preRender(PoseStack poseStack, T entity, float partialTick, 
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

        // Apply cloaking effect
        float cloakLevel = entity.getCloakingLevel();
        if (cloakLevel > 0.0f) {
            float cloakScale = 1.0f - (cloakLevel * 0.2f);
            poseStack.scale(cloakScale, cloakScale, cloakScale);
            
            // Reduce alpha for transparency
            alpha = Math.max(0.3f, 1.0f - cloakLevel);
        }

        // Apply swelling effect
        float swellAmount = entity.getSwellAmount();
        if (swellAmount > 0.0f) {
            float swellScale = 1.0f + (swellAmount * 0.5f);
            poseStack.scale(swellScale, swellScale, swellScale);
        }
    }

    @Override
    public RenderType getRenderType(T entity, float partialTick, PoseStack poseStack,
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
    protected float getDeathMaxRotation(T entity) {
        return entity.getDeathMaxRotation();
    }

    /**
     * Render leader aura overlay for leader entities.
     */
    protected void renderLeaderAura(PoseStack poseStack, T entity, float partialTick,
                                   MultiBufferSource bufferSource, int packedLight) {
        if (!entity.isLeader()) {
            return;
        }

        poseStack.pushPose();
        
        // Pulsing effect
        float pulse = 0.7f + 0.3f * (float) Math.sin(entity.tickCount * 0.1f);
        float scale = 1.1f + (float) Math.sin(entity.tickCount * 0.05f) * 0.05f;
        
        poseStack.scale(scale, scale, scale);
        poseStack.translate(0.0, -0.01, 0.0);

        VertexConsumer auraConsumer = bufferSource.getBuffer(
            RenderType.entityTranslucent(LEADER_AURA_TEXTURE)
        );

        // Render aura using the same model
        this.getModel().setCustomAnimations(entity, this.getInstanceId(entity), 
                                           this.getCurrentAnimationState());
        this.getModel().renderToBuffer(poseStack, auraConsumer, packedLight, 
                                      packedOverlay, 1.0f, 1.0f, 1.0f, pulse);

        poseStack.popPose();
    }

    /**
     * Render infection overlay for infected entities.
     */
    protected void renderInfectionOverlay(PoseStack poseStack, T entity, float partialTick,
                                         MultiBufferSource bufferSource, int packedLight) {
        float infectionLevel = entity.getInfectionOverlayLevel();
        if (infectionLevel <= 0.0f) {
            return;
        }

        float alpha = Math.min(infectionLevel, 1.0f);

        poseStack.pushPose();
        
        VertexConsumer overlayConsumer = bufferSource.getBuffer(
            RenderType.entityTranslucent(INFECTION_OVERLAY_TEXTURE)
        );

        this.getModel().setCustomAnimations(entity, this.getInstanceId(entity), 
                                           this.getCurrentAnimationState());
        this.getModel().renderToBuffer(poseStack, overlayConsumer, packedLight, 
                                      packedOverlay, 1.0f, 1.0f, 1.0f, alpha);

        poseStack.popPose();
    }

    @Override
    public void postRender(PoseStack poseStack, T entity, float partialTick,
                          MultiBufferSource bufferSource, int packedLight) {
        super.postRender(poseStack, entity, partialTick, bufferSource, packedLight);

        // Render additional layers
        if (entity.isLeader()) {
            renderLeaderAura(poseStack, entity, partialTick, bufferSource, packedLight);
        }
        
        if (entity.getInfectionOverlayLevel() > 0.0f) {
            renderInfectionOverlay(poseStack, entity, partialTick, bufferSource, packedLight);
        }
    }
}
