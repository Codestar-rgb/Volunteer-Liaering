package com.subspaceparasite.common.effect;

import com.subspaceparasite.common.capability.ParasiteCapability;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Parasitic Wither Effect - Combined wither damage and infection spread.
 * <p>
 * A stronger variant of vanilla Wither that also increases infection level.
 * Parasites are immune to this effect.
 * </p>
 * 
 * @author SRP Port Team
 */
public class WitherParasiteEffect extends BaseSRPEffect {
    
    /** Base damage per interval */
    private static final float BASE_DAMAGE = 1.0f;
    
    /** Damage increase per amplifier */
    private static final float DAMAGE_PER_LEVEL = 0.5f;
    
    /** Infection increase per tick at amplifier 0 */
    private static final float INFECTION_RATE = 0.02f;
    
    public WitherParasiteEffect() {
        super(MobEffectCategory.HARMFUL, 0x2E2E2E, 4, true, 40);
    }
    
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnWitherParticles(entity, amplifier);
            return;
        }
        
        // Skip parasites - they are immune
        if (entity instanceof com.subspaceparasite.common.entity.base.EntityParasiteBase) {
            return;
        }
        
        // Apply wither damage
        float damage = BASE_DAMAGE + (amplifier * DAMAGE_PER_LEVEL);
        entity.hurt(entity.damageSources().wither(), damage);
        
        // Also increase infection level
        entity.getCapability(ParasiteCapability.CAPABILITY).ifPresent(cap -> {
            if (!cap.isImmune()) {
                float infectionAmount = INFECTION_RATE * (1 + amplifier);
                int increaseAmount = Math.max(1, (int)(infectionAmount * 100));
                cap.addInfection(increaseAmount);
            }
        });
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 40 == 0;
    }
    
    private void spawnWitherParticles(LivingEntity entity, int amplifier) {
        for (int i = 0; i < 2 + amplifier; i++) {
            double offsetX = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            double offsetY = entity.getRandom().nextDouble() * entity.getBbHeight();
            double offsetZ = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.SMOKE,
                entity.getX() + offsetX,
                entity.getY() + offsetY,
                entity.getZ() + offsetZ,
                0, 0.02, 0
            );
        }
    }
}
