package com.subspaceparasite.common.effect;

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
 * Parasitic Slowness Effect - Movement speed reduction from parasite toxins.
 * <p>
 * A stronger variant of vanilla Slowness that is harder to remove and
 * intensifies based on proximity to parasite entities.
 * </p>
 * 
 * @author SRP Port Team
 */
public class SlownessParasiteEffect extends BaseSRPEffect {
    
    private static final UUID SPEED_UUID = UUID.fromString("e1f2a3b4-c5d6-7890-abcd-1234567890ef");
    private static final double SPEED_REDUCTION_PER_LEVEL = 0.15;
    
    public SlownessParasiteEffect() {
        super(MobEffectCategory.HARMFUL, 0x5A5A8A, 4, true, 20);
    }
    
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnSlownessParticles(entity, amplifier);
            return;
        }
        
        // At high amplifiers, also apply mining fatigue effect
        if (amplifier >= 3) {
            MobEffectInstance currentFatigue = entity.getEffect(MobEffects.DIG_SLOWDOWN);
            if (currentFatigue == null || currentFatigue.getAmplifier() < 1) {
                entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 40, 1, false, false, true));
            }
        }
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
    
    @Override
    public void addAttributeModifiers(LivingEntity entity,
                                     AttributeMap attributeMap,
                                     int amplifier) {
        AttributeInstance attr = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null) {
            double reduction = -SPEED_REDUCTION_PER_LEVEL * (amplifier + 1);
            AttributeModifier modifier = new AttributeModifier(
                SPEED_UUID,
                "Parasitic Slowness",
                reduction,
                AttributeModifier.Operation.MULTIPLY_TOTAL
            );
            if (!attr.hasModifier(modifier)) {
                attr.addTransientModifier(modifier);
            }
        }
    }
    
    @Override
    public void removeAttributeModifiers(LivingEntity entity,
                                        AttributeMap attributeMap,
                                        int amplifier) {
        AttributeInstance attr = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null) {
            attr.removeModifier(SPEED_UUID);
        }
    }
    
    private void spawnSlownessParticles(LivingEntity entity, int amplifier) {
        double x = entity.getX() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        double y = entity.getY() + entity.level().random.nextDouble() * entity.getBbHeight();
        double z = entity.getZ() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        
        entity.level().addParticle(
            net.minecraft.core.particles.ParticleTypes.MYCELIUM,
            x, y, z,
            0.0, -0.02, 0.0
        );
    }
}
