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
 * Parasitic Weakness Effect - Attack damage reduction from parasite toxins.
 * <p>
 * A stronger variant of vanilla Weakness that is harder to remove and
 * also reduces attack speed.
 * </p>
 * 
 * @author SRP Port Team
 */
public class WeaknessParasiteEffect extends BaseSRPEffect {
    
    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("f2a3b4c5-d6e7-8901-bcde-2345678901fa");
    private static final UUID ATTACK_SPEED_UUID = UUID.fromString("a3b4c5d6-e7f8-9012-cdef-345678901fab");
    private static final double DAMAGE_REDUCTION_PER_LEVEL = 0.15;
    private static final double SPEED_REDUCTION_PER_LEVEL = 0.10;
    
    public WeaknessParasiteEffect() {
        super(MobEffectCategory.HARMFUL, 0x484878, 4, true, 20);
    }
    
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnWeaknessParticles(entity, amplifier);
            return;
        }
        
        // At high amplifiers, also apply vanilla weakness for stacking effect
        if (amplifier >= 2) {
            MobEffectInstance currentWeakness = entity.getEffect(MobEffects.WEAKNESS);
            if (currentWeakness == null || currentWeakness.getAmplifier() < amplifier - 1) {
                entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, amplifier - 1, false, false, true));
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
        AttributeInstance damageAttr = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damageAttr != null) {
            double reduction = -DAMAGE_REDUCTION_PER_LEVEL * (amplifier + 1);
            AttributeModifier modifier = new AttributeModifier(
                ATTACK_DAMAGE_UUID,
                "Parasitic Weakness Damage",
                reduction,
                AttributeModifier.Operation.MULTIPLY_TOTAL
            );
            if (!damageAttr.hasModifier(modifier)) {
                damageAttr.addTransientModifier(modifier);
            }
        }
        AttributeInstance speedAttr = entity.getAttribute(Attributes.ATTACK_SPEED);
        if (speedAttr != null) {
            double reduction = -SPEED_REDUCTION_PER_LEVEL * (amplifier + 1);
            AttributeModifier modifier = new AttributeModifier(
                ATTACK_SPEED_UUID,
                "Parasitic Weakness Speed",
                reduction,
                AttributeModifier.Operation.MULTIPLY_TOTAL
            );
            if (!speedAttr.hasModifier(modifier)) {
                speedAttr.addTransientModifier(modifier);
            }
        }
    }
    
    @Override
    public void removeAttributeModifiers(LivingEntity entity,
                                        AttributeMap attributeMap,
                                        int amplifier) {
        AttributeInstance damageAttr = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damageAttr != null) {
            damageAttr.removeModifier(ATTACK_DAMAGE_UUID);
        }
        AttributeInstance speedAttr = entity.getAttribute(Attributes.ATTACK_SPEED);
        if (speedAttr != null) {
            speedAttr.removeModifier(ATTACK_SPEED_UUID);
        }
    }
    
    private void spawnWeaknessParticles(LivingEntity entity, int amplifier) {
        double x = entity.getX() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        double y = entity.getY() + entity.level().random.nextDouble() * entity.getBbHeight();
        double z = entity.getZ() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        
        entity.level().addParticle(
            net.minecraft.core.particles.ParticleTypes.WITCH,
            x, y, z,
            0.0, 0.02, 0.0
        );
    }
}
