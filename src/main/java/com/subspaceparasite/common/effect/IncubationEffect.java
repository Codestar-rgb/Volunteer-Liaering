package com.subspaceparasite.common.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Incubation Effect - Countdown to parasite egg hatching
 * Represents the final incubation phase before offspring emergence
 * 
 * @author SRP Port Team
 */
public class IncubationEffect extends BaseSRPEffect {
    
    private static final int HATCH_CHECK_INTERVAL = 50;
    private static final float INCUBATION_PROGRESS_PER_TICK = 0.001f;
    
    public IncubationEffect() {
        super(MobEffectCategory.NEUTRAL, 0xCCA300);
    }
    
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnIncubationParticles(entity);
            return;
        }
        
        // Track incubation progress
        if (entity.tickCount % HATCH_CHECK_INTERVAL == 0) {
            checkIncubationCompletion(entity, amplifier);
        }
        
        // Increase particle frequency as hatching approaches
        int particleInterval = Math.max(5, 30 - (amplifier * 5));
        if (entity.tickCount % particleInterval == 0) {
            spawnIncubationParticles(entity);
        }
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
    
    /**
     * Check if incubation is complete and trigger hatching
     */
    private void checkIncubationCompletion(LivingEntity entity, int amplifier) {
        float progress = (float) entity.tickCount / (20.0f * 60.0f * 3.0f); // 3 minutes base
        if (progress >= 1.0f) {
            onIncubationComplete(entity, amplifier);
        }
    }
    
    /**
     * Called when incubation completes and eggs hatch
     */
    private void onIncubationComplete(LivingEntity entity, int amplifier) {
        // Trigger hatching logic - spawn parasite offspring
        if (!entity.level().isClientSide()) {
            // Intensive particle effect for hatching
            for (int i = 0; i < 30; i++) {
                spawnIncubationParticles(entity);
            }
            // Actual spawning would be handled here
        }
    }
    
    private void spawnIncubationParticles(LivingEntity entity) {
        for (int i = 0; i < 3; i++) {
            double offsetX = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            double offsetY = entity.getRandom().nextDouble() * entity.getBbHeight();
            double offsetZ = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.FLAME,
                entity.getX() + offsetX,
                entity.getY() + offsetY,
                entity.getZ() + offsetZ,
                0, 0.03, 0
            );
        }
    }
}
