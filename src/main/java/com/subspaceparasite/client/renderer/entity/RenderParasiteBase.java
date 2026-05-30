package com.subspaceparasite.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.client.model.entity.ModelParasiteBiped;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;

import java.util.HashMap;
import java.util.Map;

/**
 * Base renderer for all parasite entities extending MobRenderer.
 * Includes parasite glow, leader aura, infection overlay, and cloaking support.
 */
public class RenderParasiteBase extends MobRenderer<EntityParasiteBase, ModelParasiteBiped> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(SubspaceParasite.MOD_ID, "parasite_biped"),
            "main"
    );

    private static final ResourceLocation BASE_TEXTURE = new ResourceLocation(
            SubspaceParasite.MOD_ID, "textures/entity/monster/parasite_base.png"
    );

    private static final ResourceLocation GLOW_TEXTURE = new ResourceLocation(
            SubspaceParasite.MOD_ID, "textures/entity/layer/parasite_glow.png"
    );

    private static final ResourceLocation LEADER_AURA_TEXTURE = new ResourceLocation(
            SubspaceParasite.MOD_ID, "textures/entity/layer/leader_aura.png"
    );

    private static final ResourceLocation INFECTION_OVERLAY_TEXTURE = new ResourceLocation(
            SubspaceParasite.MOD_ID, "textures/entity/layer/infection_overlay.png"
    );

    // Type-based texture mapping
    private static final Map<Integer, ResourceLocation> TYPE_TEXTURES = new HashMap<>();
    static {
        TYPE_TEXTURES.put(0, new ResourceLocation(SubspaceParasite.MOD_ID, "textures/entity/monster/crude/crude.png"));
        TYPE_TEXTURES.put(1, new ResourceLocation(SubspaceParasite.MOD_ID, "textures/entity/monster/adapted/adapted.png"));
        TYPE_TEXTURES.put(2, new ResourceLocation(SubspaceParasite.MOD_ID, "textures/entity/monster/feral/feral.png"));
        TYPE_TEXTURES.put(3, new ResourceLocation(SubspaceParasite.MOD_ID, "textures/entity/monster/primitive/primitive.png"));
        TYPE_TEXTURES.put(4, new ResourceLocation(SubspaceParasite.MOD_ID, "textures/entity/monster/inborn/inborn.png"));
        TYPE_TEXTURES.put(5, new ResourceLocation(SubspaceParasite.MOD_ID, "textures/entity/monster/pure/pure.png"));
        TYPE_TEXTURES.put(6, new ResourceLocation(SubspaceParasite.MOD_ID, "textures/entity/monster/ancient/ancient.png"));
        TYPE_TEXTURES.put(7, new ResourceLocation(SubspaceParasite.MOD_ID, "textures/entity/monster/awakened/awakened.png"));
    }

    public RenderParasiteBase(EntityRendererProvider.Context context) {
        super(context, new ModelParasiteBiped(context.bakeLayer(ModelParasiteBiped.LAYER_LOCATION)), 0.5F);
        this.addLayer(new ParasiteGlowLayer(this));
        this.addLayer(new LeaderAuraLayer(this));
        this.addLayer(new InfectionOverlayLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(EntityParasiteBase entity) {
        int type = entity.getParasiteTypeOrdinal();
        return TYPE_TEXTURES.getOrDefault(type, BASE_TEXTURE);
    }

    @Override
    protected void scale(EntityParasiteBase entity, PoseStack poseStack, float partialTick) {
        float scale = entity.getScaleModifier();
        float animScale = entity.getScaleAnimation(partialTick);
        float totalScale = scale * animScale;
        poseStack.scale(totalScale, totalScale, totalScale);

        // Cloaking/stealth rendering
        float cloaking = entity.getCloakingLevel();
        if (cloaking > 0.0f) {
            // Reduce visible scale for stealth effect
            float cloakScale = 1.0f - (cloaking * 0.3f);
            poseStack.scale(cloakScale, cloakScale, cloakScale);
        }
    }

    @Override
    protected boolean isBodyVisible(EntityParasiteBase entity) {
        float cloaking = entity.getCloakingLevel();
        if (cloaking >= 0.95f) return false;
        return super.isBodyVisible(entity);
    }

    @Override
    protected float getWhiteOverlayProgress(EntityParasiteBase entity, float partialTick) {
        float flash = entity.getHurtFlashTimer(partialTick);
        return flash > 0.0f ? flash : super.getWhiteOverlayProgress(entity, partialTick);
    }

    // ==================== Inner Layer Classes ====================

    /**
     * Parasite glow layer - renders emissive eyes/body glow.
     */
    public static class ParasiteGlowLayer extends EyesLayer<EntityParasiteBase, ModelParasiteBiped> {

        private static final RenderType GLOW_RENDER_TYPE = RenderType.eyes(GLOW_TEXTURE);

        public ParasiteGlowLayer(RenderParasiteBase renderer) {
            super(renderer);
        }

        @Override
        public RenderType renderType() {
            return GLOW_RENDER_TYPE;
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                           EntityParasiteBase entity, float limbSwing, float limbSwingAmount,
                           float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
            // Only render glow if entity has glow state
            if (!entity.hasGlow()) return;
            super.render(poseStack, buffer, packedLight, entity, limbSwing, limbSwingAmount,
                    partialTick, ageInTicks, netHeadYaw, headPitch);
        }
    }

    /**
     * Leader aura layer - renders a pulsing aura around leader entities.
     */
    public static class LeaderAuraLayer extends RenderLayer<EntityParasiteBase, ModelParasiteBiped> {

        private static final ResourceLocation AURA_TEXTURE = LEADER_AURA_TEXTURE;

        public LeaderAuraLayer(RenderParasiteBase renderer) {
            super(renderer);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                           EntityParasiteBase entity, float limbSwing, float limbSwingAmount,
                           float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
            if (!entity.isLeader()) return;

            float pulse = (float) (0.7f + 0.3f * Math.sin(entity.tickCount * 0.1f));
            int color = ((int)(pulse * 255) << 24) | 0x8844CC;

            poseStack.pushPose();
            float scale = 1.1f + (float) Math.sin(entity.tickCount * 0.05f) * 0.05f;
            poseStack.scale(scale, scale, scale);
            poseStack.translate(0, -0.01, 0);

            RenderType auraType = RenderType.entityTranslucent(AURA_TEXTURE);
            this.getParentModel().renderToBuffer(poseStack, buffer.getBuffer(auraType),
                    packedLight, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, pulse);

            poseStack.popPose();
        }
    }

    /**
     * Infection overlay layer - renders infection spreading texture.
     */
    public static class InfectionOverlayLayer extends RenderLayer<EntityParasiteBase, ModelParasiteBiped> {

        private static final ResourceLocation OVERLAY_TEXTURE = INFECTION_OVERLAY_TEXTURE;

        public InfectionOverlayLayer(RenderParasiteBase renderer) {
            super(renderer);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                           EntityParasiteBase entity, float limbSwing, float limbSwingAmount,
                           float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
            float infectionLevel = entity.getInfectionOverlayLevel();
            if (infectionLevel <= 0.0f) return;

            float alpha = Math.min(infectionLevel, 1.0f);

            RenderType overlayType = RenderType.entityTranslucent(OVERLAY_TEXTURE);
            this.getParentModel().renderToBuffer(poseStack, buffer.getBuffer(overlayType),
                    packedLight, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, alpha);
        }
    }
}
