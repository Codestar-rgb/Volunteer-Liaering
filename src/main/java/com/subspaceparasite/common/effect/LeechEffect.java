package com.subspaceparasite.common.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Leech Effect - Life drain link to parasite entity
 * Represents parasitic life force siphoning from host
 * 
 * @author SRP Port Team
 */
public class LeechEffect extends BaseSRPEffect {
    
    private static final float LEECH_RATE = 0.5f;
    private static final int LEECH_INTERVAL = 20;
    private static final double MAX_LEECH_RANGE = 16.0;
    
    public LeechEffect() {
        super(MobEffectCategory.HARMFUL, 0xA0522D);
    }
    
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnLeechParticles(entity);
            return;
        }
        
        // Find leech source and drain health
        if (entity.tickCount % LEECH_INTERVAL == 0) {
            drainLifeForce(entity, amplifier);
        }
    }
    
    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
    
    /**
     * Drain life force from host to parasite
     */
    private void drainLifeForce(LivingEntity entity, int amplifier) {
        // Find nearby parasite entities that are leeching
        double range = MAX_LEECH_RANGE;
        
        // This would integrate with parasite entity tracking
        // Transfer health from host to parasite
        
        float damage = LEECH_RATE * (amplifier + 1);
        entity.hurt(entity.damageSources().magic(), damage);
    }
    
    /**
     * Create visual link between host and leeching parasite
     */
    private void spawnLeechParticles(LivingEntity entity) {
        for (int i = 0; i < 4; i++) {
            double offsetX = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            double offsetY = entity.getRandom().nextDouble() * entity.getBbHeight();
            double offsetZ = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.REDSTONE,
                entity.getX() + offsetX,
                entity.getY() + offsetY,
                entity.getZ() + offsetZ,
                0.8, 0, 0
            );
        }
    }
}
