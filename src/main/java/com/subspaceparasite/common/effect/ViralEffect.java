package com.subspaceparasite.common.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Viral Effect - Inverts healing into damage
 * Represents viral corruption of host biological processes
 * 
 * @author SRP Port Team
 */
public class ViralEffect extends BaseSRPEffect {
    
    private static final float HEAL_INVERT_PERCENT = 0.75f;
    
    public ViralEffect() {
        super(MobEffectCategory.HARMFUL, 0x228B22);
    }
    
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnViralParticles(entity);
            return;
        }
        
        // Periodic damage representing viral replication
        if (entity.tickCount % 40 == 0) {
            float damage = 0.5f + (amplifier * 0.25f);
            entity.hurt(entity.damageSources().magic(), damage);
        }
    }
    
    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
    
    /**
     * Check if this effect should invert incoming healing
     * Called by healing methods to determine if healing should be converted to damage
     */
    public static boolean shouldInvertHeal(LivingEntity entity) {
        return entity.hasEffect(ModEffects.VIRAL.get());
    }
    
    /**
     * Convert healing amount to damage based on viral effect
     */
    public static float convertHealToDamage(float healAmount, LivingEntity entity) {
        if (entity.hasEffect(ModEffects.VIRAL.get())) {
            int amplifier = entity.getEffect(ModEffects.VIRAL.get()).getAmplifier();
            return healAmount * HEAL_INVERT_PERCENT * (amplifier + 1);
        }
        return 0;
    }
    
    private void spawnViralParticles(LivingEntity entity) {
        for (int i = 0; i < 4; i++) {
            double offsetX = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            double offsetY = entity.getRandom().nextDouble() * entity.getBbHeight();
            double offsetZ = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.SNEEZE,
                entity.getX() + offsetX,
                entity.getY() + offsetY,
                entity.getZ() + offsetZ,
                0, 0.03, 0
            );
        }
    }
}
