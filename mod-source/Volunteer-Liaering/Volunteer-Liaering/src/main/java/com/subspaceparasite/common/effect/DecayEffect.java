package com.subspaceparasite.common.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Decay Effect - Progressive armor degradation.
 * Represents the decay of host defenses under parasite influence.
 * 
 * <h2>Effects by Amplifier:</h2>
 * <ul>
 *   <li>Level 0: -15% armor effectiveness, -20% armor toughness</li>
 *   <li>Level 1: -30% armor effectiveness, -40% armor toughness</li>
 *   <li>Level 2+: Severe armor degradation + durability damage</li>
 * </ul>
 * 
 * @author SRP Port Team
 */
public class DecayEffect extends BaseSRPEffect {
    
    private static final UUID ARMOR_UUID = UUID.fromString("d2e3f4a5-b6c7-8d9e-0f1a-2b3c4d5e6f7a");
    private static final UUID ARMOR_TOUGHNESS_UUID = UUID.fromString("e3f4a5b6-c7d8-9e0f-1a2b-3c4d5e6f7a8b");
    private static final double ARMOR_REDUCTION_PER_LEVEL = 0.15;
    private static final double TOUGHNESS_REDUCTION_PER_LEVEL = 0.20;
    
    public DecayEffect() {
        super(MobEffectCategory.HARMFUL, 0x556B2F, 3, true, 40);
    }
    
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnDecayParticles(entity, amplifier);
            return;
        }
        
        // Damage armor durability at higher amplifiers
        if (amplifier >= 1) {
            for (var itemStack : entity.getArmorSlots()) {
                if (!itemStack.isEmpty() && itemStack.isDamageableItem()) {
                    if (entity.level().random.nextFloat() < 0.3f) {
                        itemStack.hurtAndBreak(1 + amplifier, entity, e -> {});
                    }
                }
            }
        }
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 40 == 0;
    }
    
    @Override
    public void addAttributeModifiers(LivingEntity entity,
                                     AttributeMap attributeMap,
                                     int amplifier) {
        double armorReduction = ARMOR_REDUCTION_PER_LEVEL * (amplifier + 1);
        double toughnessReduction = TOUGHNESS_REDUCTION_PER_LEVEL * (amplifier + 1);
        
        AttributeInstance armorAttr = entity.getAttribute(Attributes.ARMOR);
        if (armorAttr != null) {
            AttributeModifier armorModifier = new AttributeModifier(
                ARMOR_UUID,
                "Decay Armor Reduction",
                -armorReduction,
                AttributeModifier.Operation.MULTIPLY_TOTAL
            );
            if (!armorAttr.hasModifier(armorModifier)) {
                armorAttr.addTransientModifier(armorModifier);
            }
        }
        AttributeInstance toughnessAttr = entity.getAttribute(Attributes.ARMOR_TOUGHNESS);
        if (toughnessAttr != null) {
            AttributeModifier toughnessModifier = new AttributeModifier(
                ARMOR_TOUGHNESS_UUID,
                "Decay Armor Toughness Reduction",
                -toughnessReduction,
                AttributeModifier.Operation.MULTIPLY_TOTAL
            );
            if (!toughnessAttr.hasModifier(toughnessModifier)) {
                toughnessAttr.addTransientModifier(toughnessModifier);
            }
        }
    }
    
    @Override
    public void removeAttributeModifiers(LivingEntity entity,
                                        AttributeMap attributeMap,
                                        int amplifier) {
        AttributeInstance armorAttr = entity.getAttribute(Attributes.ARMOR);
        if (armorAttr != null) {
            armorAttr.removeModifier(ARMOR_UUID);
        }
        AttributeInstance toughnessAttr = entity.getAttribute(Attributes.ARMOR_TOUGHNESS);
        if (toughnessAttr != null) {
            toughnessAttr.removeModifier(ARMOR_TOUGHNESS_UUID);
        }
    }
    
    private void spawnDecayParticles(LivingEntity entity, int amplifier) {
        for (int i = 0; i < 4 + amplifier * 2; i++) {
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
