package com.subspaceparasite.common.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Decay Effect - Progressive armor degradation
 * Represents the decay of host defenses under parasite influence
 * 
 * @author SRP Port Team
 */
public class DecayEffect extends BaseSRPEffect {
    
    private static final UUID ARMOR_UUID = UUID.fromString("d2e3f4a5-b6c7-8d9e-0f1a-2b3c4d5e6f7a");
    private static final UUID ARMOR_TOUGHNESS_UUID = UUID.fromString("e3f4a5b6-c7d8-9e0f-1a2b-3c4d5e6f7a8b");
    private static final double ARMOR_REDUCTION_PER_LEVEL = 0.15;
    private static final double TOUGHNESS_REDUCTION_PER_LEVEL = 0.20;
    
    public DecayEffect() {
        super(MobEffectCategory.HARMFUL, 0x556B2F);
    }
    
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            return;
        }
        
        // Spawn decay particles periodically
        if (entity.tickCount % 60 == 0) {
            spawnDecayParticles(entity);
        }
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
    
    @Override
    public void addAttributeModifiers(@NotNull LivingEntity entity, 
                                     @NotNull net.minecraft.world.entity.ai.attributes.AttributeMap attributes, 
                                     int amplifier) {
        double armorReduction = ARMOR_REDUCTION_PER_LEVEL * (amplifier + 1);
        double toughnessReduction = TOUGHNESS_REDUCTION_PER_LEVEL * (amplifier + 1);
        
        AttributeModifier armorModifier = new AttributeModifier(
            ARMOR_UUID,
            "Decay Armor Reduction",
            -armorReduction,
            AttributeModifier.Operation.MULTIPLY_TOTAL
        );
        attributes.addAttributeInstance(Attributes.ARMOR, armorModifier);
        
        AttributeModifier toughnessModifier = new AttributeModifier(
            ARMOR_TOUGHNESS_UUID,
            "Decay Armor Toughness Reduction",
            -toughnessReduction,
            AttributeModifier.Operation.MULTIPLY_TOTAL
        );
        attributes.addAttributeInstance(Attributes.ARMOR_TOUGHNESS, toughnessModifier);
    }
    
    @Override
    public void removeAttributeModifiers(@NotNull LivingEntity entity, 
                                        @NotNull net.minecraft.world.entity.ai.attributes.AttributeMap attributes, 
                                        int amplifier) {
        AttributeModifier armorModifier = new AttributeModifier(
            ARMOR_UUID,
            "Decay Armor Reduction",
            -ARMOR_REDUCTION_PER_LEVEL * (amplifier + 1),
            AttributeModifier.Operation.MULTIPLY_TOTAL
        );
        attributes.removeAttributeInstance(Attributes.ARMOR, armorModifier);
        
        AttributeModifier toughnessModifier = new AttributeModifier(
            ARMOR_TOUGHNESS_UUID,
            "Decay Armor Toughness Reduction",
            -TOUGHNESS_REDUCTION_PER_LEVEL * (amplifier + 1),
            AttributeModifier.Operation.MULTIPLY_TOTAL
        );
        attributes.removeAttributeInstance(Attributes.ARMOR_TOUGHNESS, toughnessModifier);
    }
    
    private void spawnDecayParticles(LivingEntity entity) {
        for (int i = 0; i < 8; i++) {
            double offsetX = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            double offsetY = entity.getRandom().nextDouble() * entity.getBbHeight();
            double offsetZ = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.SPORE_BLOSSOM_AIR,
                entity.getX() + offsetX,
                entity.getY() + offsetY,
                entity.getZ() + offsetZ,
                0, 0.02, 0
            );
        }
    }
}
