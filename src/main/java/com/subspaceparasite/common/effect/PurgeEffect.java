package com.subspaceparasite.common.effect;

import com.subspaceparasite.common.capability.ParasiteCapability;
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
 * Purge Effect - Cleanses infection and provides temporary immunity.
 * <p>
 * This is the primary counter-effect to COTH and other infection effects.
 * When applied, it rapidly reduces infection level and grants temporary
 * immunity to further infection attempts.
 * </p>
 * 
 * @author SRP Port Team
 * @see ParasiteCapability
 */
public class PurgeEffect extends BaseSRPEffect {
    
    private static final UUID ARMOR_UUID = UUID.fromString("d4e5f6a7-b8c9-0123-defa-456789012345");
    
    /** Infection reduction per tick at amplifier 0 */
    private static final int BASE_INFECTION_REDUCTION = 1;
    
    /** Additional reduction per amplifier level */
    private static final int REDUCTION_PER_LEVEL = 2;
    
    /** Immunity duration granted after full cleanse (in ticks) */
    private static final int BASE_IMMUNITY_DURATION = 600; // 30 seconds
    
    /** Tick interval for infection reduction */
    private static final int PURGE_TICK_INTERVAL = 20;
    
    public PurgeEffect() {
        super(
            MobEffectCategory.BENEFICIAL,
            0xF0F8FF,  // Alice blue (pure/clean color)
            2,         // Max amplifier
            true,      // Can stack
            PURGE_TICK_INTERVAL
        );
    }
    
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnPurificationParticles(entity, amplifier);
            return;
        }
        
        // Reduce infection level
        reduceInfection(entity, amplifier);
        
        // Cleanse negative parasite effects
        cleanseNegativeEffects(entity, amplifier);
    }
    
    private void reduceInfection(@NotNull LivingEntity entity, int amplifier) {
        entity.getCapability(ParasiteCapability.CAPABILITY).ifPresent(capability -> {
            int reduction = BASE_INFECTION_REDUCTION + (amplifier * REDUCTION_PER_LEVEL);
            capability.reduceInfection(reduction);
            
            // If fully cleansed, grant bonus immunity via cooldown
            if (capability.getInfectionLevel() <= 0) {
                int immunityDuration = BASE_IMMUNITY_DURATION + (amplifier * 300);
                capability.setInfectionCooldown(immunityDuration);
                capability.setImmune(true);
            }
        });
    }
    
    private void cleanseNegativeEffects(@NotNull LivingEntity entity, int amplifier) {
        // Always remove COTH
        entity.removeEffect(MobEffects.WITHER);
        
        if (amplifier >= 1) {
            entity.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            entity.removeEffect(MobEffects.WEAKNESS);
        }
        
        if (amplifier >= 2) {
            // Full cleanse - remove all harmful effects
            entity.getActiveEffects().removeIf(effectInstance -> 
                effectInstance.getEffect().getCategory() == MobEffectCategory.HARMFUL
            );
        }
    }
    
    @Override
    public void addAttributeModifiers(LivingEntity entity,
                                     AttributeMap attributeMap,
                                     int amplifier) {
        // Grant armor bonus while purging
        AttributeInstance attr = entity.getAttribute(Attributes.ARMOR);
        if (attr != null) {
            double armorBoost = 2.0 * (amplifier + 1);
            AttributeModifier armorMod = new AttributeModifier(
                ARMOR_UUID,
                "Purge Armor Bonus",
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
            // Reset immunity after purge expires
            entity.getCapability(ParasiteCapability.CAPABILITY).ifPresent(capability -> {
                // Only reset immunity if it was from purge (check cooldown)
                if (capability.getInfectionCooldown() <= 0) {
                    capability.setImmune(false);
                }
            });
        }
        
        // Remove armor modifier
        AttributeInstance attr = entity.getAttribute(Attributes.ARMOR);
        if (attr != null) {
            attr.removeModifier(ARMOR_UUID);
        }
    }
    
    private void spawnPurificationParticles(@NotNull LivingEntity entity, int amplifier) {
        double x = entity.getX() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        double y = entity.getY() + entity.level().random.nextDouble() * entity.getBbHeight();
        double z = entity.getZ() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        
        entity.level().addParticle(
            net.minecraft.core.particles.ParticleTypes.ENCHANTED_HIT,
            x, y, z,
            0.0, 0.05, 0.0
        );
        
        if (amplifier >= 1) {
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.EFFECT,
                x, y + 0.5, z,
                0.0, 0.08, 0.0
            );
        }
        
        if (amplifier >= 2) {
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.TOTEM_OF_UNDYING,
                x, y + 0.3, z,
                (entity.level().random.nextDouble() - 0.5) * 0.1,
                0.1,
                (entity.level().random.nextDouble() - 0.5) * 0.1
            );
        }
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % PURGE_TICK_INTERVAL == 0;
    }
    
    @Override
    public boolean canApply(@NotNull LivingEntity entity) {
        return entity.isAlive();
    }
}
