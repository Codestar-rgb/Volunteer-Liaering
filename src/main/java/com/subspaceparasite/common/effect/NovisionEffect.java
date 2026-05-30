package com.subspaceparasite.common.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Novision Effect - Complete blindness with auditory compensation
 * Represents total visual deprivation from parasite toxins
 * 
 * @author SRP Port Team
 */
public class NovisionEffect extends BaseSRPEffect {
    
    private static final int AUDIO_PULSE_INTERVAL = 40;
    
    public NovisionEffect() {
        super(MobEffectCategory.HARMFUL, 0x1A1A1A);
    }
    
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnNovisionParticles(entity);
            
            // Client-side visual effects for blindness
            if (entity instanceof Player player) {
                applyBlindnessOverlay(player, amplifier);
            }
            return;
        }
        
        // Server-side: Apply actual blindness effect
        entity.setRemainingFireTicks(0);
        
        // Audio pulse for echolocation hint
        if (entity.tickCount % AUDIO_PULSE_INTERVAL == 0) {
            triggerAudioPulse(entity);
        }
    }
    
    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
    
    /**
     * Apply visual blindness overlay for players
     */
    private void applyBlindnessOverlay(Player player, int amplifier) {
        // This would be expanded with screen overlays
        // For now, just spawn particles to indicate effect
    }
    
    /**
     * Trigger audio pulse for echolocation mechanic
     */
    private void triggerAudioPulse(LivingEntity entity) {
        // Play subtle sound that could be used for echolocation
        // This is a placeholder for actual sound implementation
    }
    
    private void spawnNovisionParticles(LivingEntity entity) {
        for (int i = 0; i < 2; i++) {
            double offsetX = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            double offsetY = entity.getRandom().nextDouble() * entity.getBbHeight();
            double offsetZ = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.ASH,
                entity.getX() + offsetX,
                entity.getY() + offsetY,
                entity.getZ() + offsetZ,
                0, 0.01, 0
            );
        }
    }
}
