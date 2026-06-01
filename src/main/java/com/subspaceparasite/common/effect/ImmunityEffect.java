package com.subspaceparasite.common.effect;

import com.subspaceparasite.common.capability.ParasiteCapability;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Immunity Effect - Complete protection against parasite infection.
 * <p>
 * This is the ultimate defensive effect against SRP parasites. When active,
 * the entity becomes completely immune to all forms of infection, COTH effects,
 * and parasite-related debuffs.
 * </p>
 * 
 * @author SRP Port Team
 * @see ParasiteCapability
 */
public class ImmunityEffect extends BaseSRPEffect {
    
    /** UUID for armor boost modifier while immune */
    private static final UUID ARMOR_UUID = UUID.fromString("b1c2d3e4-f5a6-7890-bcde-f12345678901");
    
    /** Resistance boost per tick at amplifier 0 */
    private static final float BASE_RESISTANCE_BOOST = 0.01f;
    
    /** Additional resistance per amplifier level */
    private static final float RESISTANCE_PER_LEVEL = 0.005f;
    
    /** Maximum resistance cap while effect is active */
    private static final float MAX_RESISTANCE_CAP = 1.0f;
    
    /** Cleansing radius for high-level immunity (in blocks) */
    private static final double CLEANSING_RADIUS = 3.0;
    
    /** Tick interval for resistance application */
    private static final int RESISTANCE_TICK_INTERVAL = 10;
    
    /**
     * Creates the Immunity effect with standard settings.
     */
    public ImmunityEffect() {
        super(
            MobEffectCategory.BENEFICIAL,
            0x00FFFF,  // Cyan color (pure protection)
            2,         // Max amplifier
            true,      // Can stack
            RESISTANCE_TICK_INTERVAL
        );
    }
    
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnImmunityParticles(entity, amplifier);
            return;
        }
        
        // Set complete immunity state
        setCompleteImmunity(entity, amplifier);
        
        // Apply resistance boost over time
        boostResistance(entity, amplifier);
        
        // Cleansing aura at high levels
        if (amplifier >= 2) {
            applyCleansingAura(entity);
        }
    }
    
    private void setCompleteImmunity(@NotNull LivingEntity entity, int amplifier) {
        entity.getCapability(ParasiteCapability.CAPABILITY).ifPresent(capability -> {
            capability.setImmune(true);
            
            // Ensure cooldown is active to block any edge cases
            var effectInstance = entity.getEffect(this);
            if (effectInstance != null) {
                int remainingDuration = effectInstance.getDuration();
                capability.setInfectionCooldown(Math.max(capability.getInfectionCooldown(), remainingDuration));
            }
        });
    }
    
    private void boostResistance(@NotNull LivingEntity entity, int amplifier) {
        entity.getCapability(ParasiteCapability.CAPABILITY).ifPresent(capability -> {
            float currentResistance = capability.getInfectionResistance();
            float boostAmount = BASE_RESISTANCE_BOOST + (amplifier * RESISTANCE_PER_LEVEL);
            float newResistance = Math.min(MAX_RESISTANCE_CAP, currentResistance + boostAmount);
            capability.setInfectionResistance(newResistance);
        });
    }
    
    /**
     * Applies cleansing aura to nearby non-parasite entities at amplifier 2+.
     */
    private void applyCleansingAura(@NotNull LivingEntity entity) {
        var nearbyEntities = entity.level().getEntitiesOfClass(
            LivingEntity.class,
            entity.getBoundingBox().inflate(CLEANSING_RADIUS),
            e -> e != entity && !(e instanceof com.subspaceparasite.common.entity.base.EntityParasiteBase)
        );
        
        for (LivingEntity nearby : nearbyEntities) {
            nearby.getCapability(ParasiteCapability.CAPABILITY).ifPresent(cap -> {
                if (cap.getInfectionLevel() > 0) {
                    cap.reduceInfection(1);
                }
            });
        }
    }
    
    @Override
    public void addAttributeModifiers(LivingEntity entity,
                                     AttributeMap attributeMap,
                                     int amplifier) {
        // Grant armor bonus while immune
        AttributeInstance attr = entity.getAttribute(Attributes.ARMOR);
        if (attr != null) {
            double armorBoost = 4.0 * (amplifier + 1);
            AttributeModifier armorMod = new AttributeModifier(
                ARMOR_UUID,
                "Immunity Armor Bonus",
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
            // Remove immunity flag but keep partial resistance
            entity.getCapability(ParasiteCapability.CAPABILITY).ifPresent(capability -> {
                capability.setImmune(false);
                float currentResistance = capability.getInfectionResistance();
                if (currentResistance > 0.5f) {
                    capability.setInfectionResistance(0.5f);
                }
            });
            
            // Remove armor modifier
            AttributeInstance attr = entity.getAttribute(Attributes.ARMOR);
            if (attr != null) {
                attr.removeModifier(ARMOR_UUID);
            }
        }
    }
    
    private void spawnImmunityParticles(@NotNull LivingEntity entity, int amplifier) {
        double x = entity.getX() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        double y = entity.getY() + entity.level().random.nextDouble() * entity.getBbHeight();
        double z = entity.getZ() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        
        entity.level().addParticle(
            net.minecraft.core.particles.ParticleTypes.END_ROD,
            x, y, z,
            0.0, 0.02, 0.0
        );
        
        if (amplifier >= 1) {
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER,
                x, y + 0.5, z,
                (entity.level().random.nextDouble() - 0.5) * 0.05,
                0.05,
                (entity.level().random.nextDouble() - 0.5) * 0.05
            );
        }
        
        if (amplifier >= 2) {
            for (int i = 0; i < 3; i++) {
                double angle = (entity.tickCount + i * 2) * 0.1;
                double radius = entity.getBbWidth() / 2.0 + 0.2;
                double px = entity.getX() + Math.cos(angle) * radius;
                double pz = entity.getZ() + Math.sin(angle) * radius;
                
                entity.level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.NAUTILUS,
                    px, y + 0.5, pz,
                    0.0, 0.0, 0.0
                );
            }
        }
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % RESISTANCE_TICK_INTERVAL == 0;
    }
    
    @Override
    public boolean canApply(@NotNull LivingEntity entity) {
        return entity.isAlive();
    }
}
