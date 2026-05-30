package com.subspaceparasite.client.model.entity;

import com.subspaceparasite.common.entity.base.EntityParasiteBase;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

/**
 * Base GeoModel for all parasite entities using GeckoLib.
 * Dynamically loads geometry and textures based on entity type and phase.
 */
public class ModelParasiteGeo<T extends EntityParasiteBase> extends GeoModel<T> {

    private final String modelName;
    private final ResourceLocation defaultTexture;

    public ModelParasiteGeo(String modelName, String texturePath) {
        this.modelName = modelName;
        this.defaultTexture = new ResourceLocation("subspaceparasite", texturePath);
    }

    @Override
    public ResourceLocation getModelResource(T entity) {
        return new ResourceLocation("subspaceparasite", "geo/" + modelName + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T entity) {
        // Allow entity to override texture based on type/phase
        ResourceLocation customTexture = entity.getCustomTexture();
        if (customTexture != null) {
            return customTexture;
        }
        return defaultTexture;
    }

    @Override
    public ResourceLocation getAnimationResource(T entity) {
        // Use same name for animation file
        return new ResourceLocation("subspaceparasite", "animations/" + modelName + ".animation.json");
    }

    @Override
    public void setCustomAnimations(T entity, int instanceId, AnimationState<T> animationState) {
        super.setCustomAnimations(entity, instanceId, animationState);
        
        // Access bones for custom animation logic if needed
        CoreGeoBone head = getAnimationProcessor().getBone("head");
        CoreGeoBone body = getAnimationProcessor().getBone("body");
        
        if (head != null) {
            // Apply look-at rotation from entity
            float headPitch = entity.getXRot() * ((float) Math.PI / 180F);
            float headYaw = entity.getYRot() * ((float) Math.PI / 180F);
            head.setRotX(headPitch);
            head.setRotY(headYaw);
        }
        
        // Handle special states
        if (entity.isCloaking()) {
            applyCloakingEffect(animationState, entity);
        }
        
        if (entity.isSwelling()) {
            applySwellingEffect(animationState, entity);
        }
    }

    /**
     * Apply cloaking visual effect to model bones.
     */
    protected void applyCloakingEffect(AnimationState<T> animationState, T entity) {
        float cloakLevel = entity.getCloakingLevel();
        // Bones can be scaled or alpha-adjusted based on cloakLevel
        EntityModelData modelData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        modelData.scaleX = 1.0f - (cloakLevel * 0.2f);
        modelData.scaleY = 1.0f - (cloakLevel * 0.2f);
        modelData.scaleZ = 1.0f - (cloakLevel * 0.2f);
    }

    /**
     * Apply swelling visual effect to model bones.
     */
    protected void applySwellingEffect(AnimationState<T> animationState, T entity) {
        float swellAmount = entity.getSwellAmount();
        EntityModelData modelData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        float scale = 1.0f + (swellAmount * 0.5f);
        modelData.scaleX = scale;
        modelData.scaleY = scale;
        modelData.scaleZ = scale;
    }
}
