package com.subspaceparasite.common.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Novision Effect - Complete blindness with auditory compensation.
 * Represents total visual deprivation from parasite toxins.
 * 
 * <h2>Effects by Amplifier:</h2>
 * <ul>
 *   <li>Level 0: Vanilla blindness effect</li>
 *   <li>Level 1: Extended blindness, reduced follow range</li>
 *   <li>Level 2+: Total blindness, prevents targeting</li>
 * </ul>
 * 
 * @author SRP Port Team
 */
public class NovisionEffect extends BaseSRPEffect {
    
    private static final int BLINDNESS_REFRESH_INTERVAL = 30;
    
    public NovisionEffect() {
        super(MobEffectCategory.HARMFUL, 0x1A1A1A, 4, true, 20);
    }
    
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnNovisionParticles(entity, amplifier);
            return;
        }
        
        // Apply vanilla blindness effect (refreshed periodically)
        MobEffectInstance currentBlindness = entity.getEffect(MobEffects.BLINDNESS);
        int blindnessLevel = Math.min(1, amplifier);
        if (currentBlindness == null || currentBlindness.getDuration() < 20) {
            entity.addEffect(new MobEffectInstance(
                MobEffects.BLINDNESS,
                BLINDNESS_REFRESH_INTERVAL + 20,
                blindnessLevel,
                false, false, true
            ));
        }
        
        // Level 1+: Also apply darkness for total blindness
        if (amplifier >= 1) {
            MobEffectInstance currentDarkness = entity.getEffect(MobEffects.DARKNESS);
            if (currentDarkness == null || currentDarkness.getDuration() < 20) {
                entity.addEffect(new MobEffectInstance(
                    MobEffects.DARKNESS,
                    BLINDNESS_REFRESH_INTERVAL + 20,
                    0,
                    false, false, true
                ));
            }
        }
        
        // Level 2+: Prevent mob targeting
        if (amplifier >= 2 && entity instanceof net.minecraft.world.entity.Mob mob) {
            mob.setTarget(null);
        }
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
    
    private void spawnNovisionParticles(LivingEntity entity, int amplifier) {
        for (int i = 0; i < 1 + amplifier; i++) {
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
