package com.subspaceparasite.common.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Gestation Effect - Tracks parasitic egg development progress
 * Represents the gestation period of parasite offspring within host
 * 
 * @author SRP Port Team
 */
public class GestationEffect extends BaseSRPEffect {
    
    private static final int SPAWN_CHECK_INTERVAL = 100;
    private static final float GESTATION_PROGRESS_PER_TICK = 0.0005f;
    
    public GestationEffect() {
        super(MobEffectCategory.NEUTRAL, 0xE6B800);
    }
    
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnGestationParticles(entity);
            return;
        }
        
        // Track gestation progress via capability or NBT
        if (entity.tickCount % SPAWN_CHECK_INTERVAL == 0) {
            checkGestationCompletion(entity, amplifier);
        }
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
    
    /**
     * Check if gestation is complete and trigger spawning
     */
    private void checkGestationCompletion(LivingEntity entity, int amplifier) {
        // This would integrate with a gestation tracker system
        // For now, just log progress
        float progress = (float) entity.tickCount / (20.0f * 60.0f * 5.0f); // 5 minutes
        if (progress >= 1.0f) {
            onGestationComplete(entity, amplifier);
        }
    }
    
    /**
     * Called when gestation completes
     */
    private void onGestationComplete(LivingEntity entity, int amplifier) {
        // Trigger egg/alveoli spawning logic
        // This would be expanded with actual spawning code
        if (!entity.level().isClientSide()) {
            // Spawn notification particles
            for (int i = 0; i < 20; i++) {
                spawnGestationParticles(entity);
            }
        }
    }
    
    private void spawnGestationParticles(LivingEntity entity) {
        for (int i = 0; i < 2; i++) {
            double offsetX = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            double offsetY = entity.getRandom().nextDouble() * entity.getBbHeight() * 0.5f;
            double offsetZ = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.ITEM_SNOWBALL,
                entity.getX() + offsetX,
                entity.getY() + offsetY,
                entity.getZ() + offsetZ,
                0, 0.02, 0
            );
        }
    }
}
