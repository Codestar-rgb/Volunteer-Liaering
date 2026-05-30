package com.subspaceparasite.common.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Assimilation Effect - Tracks entity assimilation progress into hive mind
 * Represents the gradual loss of individuality to parasite collective
 * 
 * @author SRP Port Team
 */
public class AssimilationEffect extends BaseSRPEffect {
    
    private static final float ASSIMILATION_RATE_PER_TICK = 0.0002f;
    private static final int CHECK_INTERVAL = 50;
    
    public AssimilationEffect() {
        super(MobEffectCategory.NEUTRAL, 0x8B0000);
    }
    
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnAssimilationParticles(entity);
            return;
        }
        
        // Track assimilation progress
        if (entity.tickCount % CHECK_INTERVAL == 0) {
            updateAssimilationProgress(entity, amplifier);
        }
    }
    
    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
    
    /**
     * Update assimilation progress tracker
     */
    private void updateAssimilationProgress(LivingEntity entity, int amplifier) {
        float baseRate = ASSIMILATION_RATE_PER_TICK * (amplifier + 1);
        // Progress would be stored in capability or NBT
        // When complete, entity becomes fully controlled by hive mind
    }
    
    /**
     * Called when assimilation is complete
     */
    private void onAssimilationComplete(LivingEntity entity, int amplifier) {
        // Entity becomes permanently allied with parasites
        // This would modify AI goals and faction relationships
    }
    
    private void spawnAssimilationParticles(LivingEntity entity) {
        for (int i = 0; i < 4; i++) {
            double offsetX = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            double offsetY = entity.getRandom().nextDouble() * entity.getBbHeight();
            double offsetZ = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.PORTAL,
                entity.getX() + offsetX,
                entity.getY() + offsetY,
                entity.getZ() + offsetZ,
                0, 0.03, 0
            );
        }
    }
}
