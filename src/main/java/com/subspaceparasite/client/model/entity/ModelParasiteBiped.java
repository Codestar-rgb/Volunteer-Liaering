package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.SubspaceParasite;
import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import com.subspaceparasite.common.entity.base.EntityParasiteBase.AnimState;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Biped model for parasite entities extending HumanoidModel.
 * Supports 6 animation states and per-type special move animations.
 */
public class ModelParasiteBiped extends HumanoidModel<EntityParasiteBase> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(SubspaceParasite.MOD_ID, "parasite_biped"),
            "main"
    );

    // Custom parts for parasite-specific features
    private final ModelPart tailPart1;
    private final ModelPart tailPart2;
    private final ModelPart crestPart;

    // Animation state tracking
    private AnimState currentAnimState = AnimState.IDLE;
    private float animProgress = 0.0f;

    public ModelParasiteBiped(ModelPart root) {
        super(root);
        this.tailPart1 = root.getChild("tail_part_1");
        this.tailPart2 = root.getChild("tail_part_2");
        this.crestPart = root.getChild("crest");
    }

    /**
     * Create the body layer definition for the parasite biped model.
     */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition root = mesh.getRoot();

        // Standard humanoid parts are already defined by HumanoidModel.createMesh
        // Add parasite-specific parts

        // Tail segments
        PartDefinition tail1 = root.addOrReplaceChild("tail_part_1",
                CubeListBuilder.create().texOffs(32, 48)
                        .addBox(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, 12.0F, 2.0F, -0.5F, 0.0F, 0.0F));

        PartDefinition tail2 = tail1.addOrReplaceChild("tail_part_2",
                CubeListBuilder.create().texOffs(32, 57)
                        .addBox(-1.0F, 0.0F, 0.0F, 2.0F, 5.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, 5.0F, 1.0F, -0.3F, 0.0F, 0.0F));

        // Crest/horn on head
        PartDefinition head = root.getChild("head");
        head.addOrReplaceChild("crest",
                CubeListBuilder.create().texOffs(0, 48)
                        .addBox(-2.0F, -6.0F, -2.0F, 4.0F, 4.0F, 1.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(EntityParasiteBase entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        // Update animation state from entity
        this.currentAnimState = entity.getCurrentAnimState();
        this.animProgress = entity.getAnimProgress(ageInTicks);

        // Apply per-state animations
        switch (this.currentAnimState) {
            case IDLE:
                applyIdleAnimation(ageInTicks);
                break;
            case ATTACKING:
                applyAttackAnimation(entity, ageInTicks);
                break;
            case SPECIAL_SKILL:
                applySpecialSkillAnimation(entity, ageInTicks);
                break;
            case SWELLING:
                applySwellingAnimation(entity);
                break;
            case CLOAKING:
                applyCloakingAnimation(entity);
                break;
            case MELTING:
                applyMeltingAnimation(entity);
                break;
        }

        // Per-type special move animations
        applyTypeSpecificAnimation(entity, ageInTicks);

        // Animate tail
        animateTail(limbSwing, limbSwingAmount, ageInTicks);
    }

    /**
     * Idle animation - subtle breathing/swaying.
     */
    protected void applyIdleAnimation(float ageInTicks) {
        float breathe = (float) Math.sin(ageInTicks * 0.05f) * 0.02f;
        this.body.xRot += breathe;
        this.head.xRot += breathe * 0.5f;
    }

    /**
     * Attack animation - arm swing with body lean.
     */
    protected void applyAttackAnimation(EntityParasiteBase entity, float ageInTicks) {
        float swing = Mth.sin(this.animProgress * (float) Math.PI);
        this.rightArm.xRot = -2.0f * swing;
        this.body.yRot = Mth.sin(this.animProgress * (float) Math.PI) * 0.3f;
    }

    /**
     * Special skill animation - arms raised, body arched.
     */
    protected void applySpecialSkillAnimation(EntityParasiteBase entity, float ageInTicks) {
        float phase = this.animProgress;
        this.rightArm.xRot = -1.5f + Mth.sin(phase * (float) Math.PI) * 0.5f;
        this.leftArm.xRot = -1.5f + Mth.sin(phase * (float) Math.PI) * 0.5f;
        this.body.xRot = Mth.sin(phase * (float) Math.PI * 0.5f) * 0.3f;
    }

    /**
     * Swelling animation - arms spread out.
     * Note: In MC 1.20.1, ModelPart does not have xScale/yScale/zScale fields.
     * Swelling visual scale is handled via PoseStack.scale() in RenderParasiteBase instead.
     */
    protected void applySwellingAnimation(EntityParasiteBase entity) {
        float swell = entity.getSwellAmount();

        // Arms spread out
        this.rightArm.xRot = swell * -0.8f;
        this.leftArm.xRot = swell * -0.8f;
        this.rightArm.zRot = swell * 0.5f;
        this.leftArm.zRot = swell * -0.5f;
    }

    /**
     * Cloaking animation - body partially dissolves.
     * Note: In MC 1.20.1, ModelPart does not have yScale field.
     * Cloaking visibility is handled via RenderParasiteBase.isBodyVisible() instead.
     */
    protected void applyCloakingAnimation(EntityParasiteBase entity) {
        // Visual cloaking handled in renderer; pose adjustments can be added here
    }

    /**
     * Melting animation - body sinks into the ground.
     * Note: In MC 1.20.1, ModelPart does not have xScale/yScale/zScale fields.
     * Melting visual scale is handled via PoseStack.scale() in RenderParasiteBase instead.
     */
    protected void applyMeltingAnimation(EntityParasiteBase entity) {
        // Visual melting handled in renderer; pose adjustments can be added here
    }

    /**
     * Per-type special move animations.
     */
    protected void applyTypeSpecificAnimation(EntityParasiteBase entity, float ageInTicks) {
        int type = entity.getParasiteTypeOrdinal();
        switch (type) {
            case 2: // Feral - more aggressive stance
                this.body.xRot += 0.2f;
                break;
            case 5: // Pure - upright regal stance
                this.body.xRot -= 0.1f;
                break;
            case 7: // Awakened - hovering
                float hover = (float) Math.sin(ageInTicks * 0.1f) * 0.05f;
                this.body.y += hover;
                break;
            default:
                break;
        }
    }

    /**
     * Animate tail segments.
     */
    protected void animateTail(float limbSwing, float limbSwingAmount, float ageInTicks) {
        float tailSwing = Mth.sin(limbSwing * 0.5f) * limbSwingAmount * 0.5f;
        this.tailPart1.yRot = tailSwing;
        this.tailPart2.yRot = tailSwing * 0.7f + Mth.sin(ageInTicks * 0.1f) * 0.1f;
    }
}
