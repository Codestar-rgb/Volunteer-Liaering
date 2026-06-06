package com.subspaceparasite.common.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Putrefaction Effect - Compound effect of poison and slowness
 * Represents the putrefaction of host tissues
 * 
 * @author SRP Port Team
 */
public class PutrefactionEffect extends BaseSRPEffect {
    
    private static final int POISON_INTERVAL = 20;
    private static final float SLOWNESS_FACTOR = 0.15f;
    
    public PutrefactionEffect() {
        super(MobEffectCategory.HARMFUL, 0x2F4F2F);
    }
    
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            return;
        }
        
        // Apply poison damage periodically
        if (entity.tickCount % POISON_INTERVAL == 0) {
            entity.hurt(entity.damageSources().magic(), 0.5f + (amplifier * 0.25f));
        }
        
        // Apply slowness effect through attribute modifier
        applySlownessEffect(entity, amplifier);
        
        // Spawn putrefaction particles
        if (entity.tickCount % 25 == 0) {
            spawnPutrefactionParticles(entity);
        }
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
    
    /**
     * Applies slowness to the entity using the correct 1.20.1 API.
     */
    private void applySlownessEffect(@NotNull LivingEntity entity, int amplifier) {
        // Apply slowness mob effect instead of using non-existent setSpeedMultiplier
        float slownessLevel = (float)(SLOWNESS_FACTOR * (amplifier + 1) * 10);
        int effectLevel = Math.min(5, (int)slownessLevel);
        
        // Add vanilla Slowness effect if not already present or upgrade existing
        net.minecraft.world.effect.MobEffectInstance currentSlowness = 
            entity.getEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN);
        
        if (currentSlowness == null || currentSlowness.getAmplifier() < effectLevel) {
            entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN,
                POISON_INTERVAL * 2,
                effectLevel,
                false,
                false,
                true
            ));
        }
    }
    
    private void spawnPutrefactionParticles(LivingEntity entity) {
        for (int i = 0; i < 8; i++) {
            double offsetX = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            double offsetY = entity.getRandom().nextDouble() * entity.getBbHeight();
            double offsetZ = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.MYCELIUM,
                entity.getX() + offsetX,
                entity.getY() + offsetY,
                entity.getZ() + offsetZ,
                0, 0.02, 0
            );
        }
    }
}
