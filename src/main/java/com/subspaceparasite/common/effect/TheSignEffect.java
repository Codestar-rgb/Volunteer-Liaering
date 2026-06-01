package com.subspaceparasite.common.effect;

import com.subspaceparasite.common.capability.ParasiteCapability;
import com.subspaceparasite.core.ModEffects;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * The Sign Effect - Powerful cleansing and anti-infection effect from original SRP.
 * <p>
 * This is the signature "holy" effect from Scape and Run: Parasites.
 * At high amplifiers, it rapidly cures COTH and grants powerful regeneration.
 * This is the primary counter to the parasite infection system.
 * </p>
 * 
 * <h2>Effects by Amplifier:</h2>
 * <ul>
 *   <li>Level 0-2: Minor infection reduction, slow cleansing</li>
 *   <li>Level 3-5: Moderate infection reduction, Regeneration I</li>
 *   <li>Level 6+: Rapid COTH curing, Regeneration II+, full cleanse</li>
 * </ul>
 * 
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Progressive infection cleansing based on amplifier</li>
 *   <li>Removes COTH effect at amplifier 6+</li>
 *   <li>Grants Regeneration at amplifier 3+</li>
 *   <li>Builds permanent infection resistance</li>
 *   <li>Visual golden purification particles</li>
 * </ul>
 * 
 * @author SRP Port Team
 */
public class TheSignEffect extends BaseSRPEffect {
    
    private static final UUID ARMOR_UUID = UUID.fromString("a0b1c2d3-e4f5-6789-abcd-0123456789ab");
    
    /** Infection reduction per tick at amplifier 0 */
    private static final int BASE_INFECTION_REDUCTION = 1;
    
    /** Additional reduction per amplifier level */
    private static final int REDUCTION_PER_LEVEL = 2;
    
    /** Resistance build rate per tick */
    private static final float RESISTANCE_BUILD_RATE = 0.002f;
    
    /** Maximum resistance from The Sign */
    private static final float MAX_RESISTANCE_FROM_SIGN = 0.95f;
    
    /** Tick interval for cleansing */
    private static final int SIGN_TICK_INTERVAL = 20;
    
    /** Amplifier threshold for Regeneration */
    private static final int REGEN_THRESHOLD = 3;
    
    /** Amplifier threshold for COTH removal */
    private static final int COTH_REMOVAL_THRESHOLD = 6;
    
    public TheSignEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFD700, 9, true, SIGN_TICK_INTERVAL);
    }
    
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnSignParticles(entity, amplifier);
            return;
        }
        
        // Reduce infection level
        reduceInfection(entity, amplifier);
        
        // Build infection resistance
        buildResistance(entity, amplifier);
        
        // Grant Regeneration at amplifier 3+
        if (amplifier >= REGEN_THRESHOLD) {
            applyRegeneration(entity, amplifier);
        }
        
        // Remove COTH at amplifier 6+
        if (amplifier >= COTH_REMOVAL_THRESHOLD) {
            removeCOTH(entity, amplifier);
        }
        
        // Remove other negative effects at high amplifier
        if (amplifier >= 5) {
            cleanseNegativeEffects(entity, amplifier);
        }
    }
    
    private void reduceInfection(@NotNull LivingEntity entity, int amplifier) {
        entity.getCapability(ParasiteCapability.CAPABILITY).ifPresent(cap -> {
            int reduction = BASE_INFECTION_REDUCTION + (amplifier * REDUCTION_PER_LEVEL);
            cap.reduceInfection(reduction);
        });
    }
    
    private void buildResistance(@NotNull LivingEntity entity, int amplifier) {
        entity.getCapability(ParasiteCapability.CAPABILITY).ifPresent(cap -> {
            float currentResistance = cap.getInfectionResistance();
            if (currentResistance < MAX_RESISTANCE_FROM_SIGN) {
                float buildRate = RESISTANCE_BUILD_RATE * (1.0f + amplifier * 0.3f);
                float newResistance = Math.min(MAX_RESISTANCE_FROM_SIGN, currentResistance + buildRate);
                cap.setInfectionResistance(newResistance);
            }
        });
    }
    
    private void applyRegeneration(@NotNull LivingEntity entity, int amplifier) {
        int regenLevel = Math.min(4, (amplifier - REGEN_THRESHOLD) / 2);
        MobEffectInstance currentRegen = entity.getEffect(MobEffects.REGENERATION);
        if (currentRegen == null || currentRegen.getAmplifier() < regenLevel) {
            entity.addEffect(new MobEffectInstance(
                MobEffects.REGENERATION,
                SIGN_TICK_INTERVAL * 2,
                regenLevel,
                false, false, true
            ));
        }
    }
    
    private void removeCOTH(@NotNull LivingEntity entity, int amplifier) {
        // Remove COTH effect entirely at high amplifier
        if (entity.hasEffect(ModEffects.COTH.get())) {
            entity.removeEffect(ModEffects.COTH.get());
        }
        // Also remove infection stages
        if (entity.hasEffect(ModEffects.INFECTION_II.get())) {
            entity.removeEffect(ModEffects.INFECTION_II.get());
        }
        if (entity.hasEffect(ModEffects.INFECTION_III.get())) {
            entity.removeEffect(ModEffects.INFECTION_III.get());
        }
    }
    
    private void cleanseNegativeEffects(@NotNull LivingEntity entity, int amplifier) {
        // Remove specific parasite debuffs
        if (entity.hasEffect(ModEffects.BLEED.get())) {
            entity.removeEffect(ModEffects.BLEED.get());
        }
        if (entity.hasEffect(ModEffects.VIRULENCE.get())) {
            entity.removeEffect(ModEffects.VIRULENCE.get());
        }
        if (entity.hasEffect(ModEffects.SPORE.get())) {
            entity.removeEffect(ModEffects.SPORE.get());
        }
        if (entity.hasEffect(ModEffects.CORROSION.get())) {
            entity.removeEffect(ModEffects.CORROSION.get());
        }
        
        // At very high amplifier, remove all harmful effects
        if (amplifier >= 8) {
            entity.getActiveEffects().removeIf(effectInstance ->
                effectInstance.getEffect().getCategory() == MobEffectCategory.HARMFUL
            );
        }
    }
    
    @Override
    public void addAttributeModifiers(LivingEntity entity,
                                     AttributeMap attributeMap,
                                     int amplifier) {
        // Grant armor bonus while The Sign is active
        AttributeInstance attr = entity.getAttribute(Attributes.ARMOR);
        if (attr != null) {
            double armorBoost = 2.0 + amplifier * 1.0;
            AttributeModifier armorMod = new AttributeModifier(
                ARMOR_UUID,
                "The Sign Armor Bonus",
                armorBoost,
                AttributeModifier.Operation.ADDITION
            );
            if (!attr.hasModifier(armorMod)) {
                attr.addTransientModifier(armorMod);
            }
        }
    }
    
    @Override
    public void removeAttributeModifiers(LivingEntity entity,
                                        AttributeMap attributeMap,
                                        int amplifier) {
        if (!entity.level().isClientSide()) {
            // Keep partial resistance after The Sign expires
            entity.getCapability(ParasiteCapability.CAPABILITY).ifPresent(cap -> {
                float currentResistance = cap.getInfectionResistance();
                if (currentResistance > 0.3f) {
                    cap.setInfectionResistance(currentResistance * 0.7f);
                }
            });
        }
        
        // Remove armor modifier
        AttributeInstance attr = entity.getAttribute(Attributes.ARMOR);
        if (attr != null) {
            attr.removeModifier(ARMOR_UUID);
        }
    }
    
    private void spawnSignParticles(@NotNull LivingEntity entity, int amplifier) {
        double x = entity.getX() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        double y = entity.getY() + entity.level().random.nextDouble() * entity.getBbHeight();
        double z = entity.getZ() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        
        // Golden purification particles
        entity.level().addParticle(
            net.minecraft.core.particles.ParticleTypes.ENCHANTED_HIT,
            x, y, z,
            0.0, 0.05, 0.0
        );
        
        if (amplifier >= REGEN_THRESHOLD) {
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.TOTEM_OF_UNDYING,
                x, y + 0.3, z,
                (entity.level().random.nextDouble() - 0.5) * 0.05,
                0.06,
                (entity.level().random.nextDouble() - 0.5) * 0.05
            );
        }
        
        if (amplifier >= COTH_REMOVAL_THRESHOLD) {
            // Intense golden aura at COTH-removal threshold
            for (int i = 0; i < 3; i++) {
                double angle = (entity.tickCount + i * 2.5) * 0.08;
                double radius = entity.getBbWidth() / 2.0 + 0.3;
                double px = entity.getX() + Math.cos(angle) * radius;
                double pz = entity.getZ() + Math.sin(angle) * radius;
                
                entity.level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.END_ROD,
                    px, y + 0.5, pz,
                    0.0, 0.03, 0.0
                );
            }
        }
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % SIGN_TICK_INTERVAL == 0;
    }
    
    @Override
    public boolean canApply(@NotNull LivingEntity entity) {
        return entity.isAlive();
    }
}
