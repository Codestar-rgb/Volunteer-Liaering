package com.subspaceparasite.common.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Decomposition Effect - Rapid hunger exhaustion
 * Represents the decomposition of host nutrients by parasites
 * 
 * @author SRP Port Team
 */
public class DecompositionEffect extends BaseSRPEffect {
    
    private static final float EXHAUSTION_PER_TICK = 0.05f;
    private static final int DAMAGE_INTERVAL = 40;
    
    public DecompositionEffect() {
        super(MobEffectCategory.HARMFUL, 0x3B5323);
    }
    
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            return;
        }
        
        // Apply exhaustion to cause hunger
        float exhaustion = EXHAUSTION_PER_TICK * (amplifier + 1);
        if (entity instanceof Player player) {
            player.causeFoodExhaustion(exhaustion);
        }
        
        // Deal damage if starving
        if (entity.getFoodData() != null && entity.getFoodData().getFoodLevel() <= 0) {
            if (entity.tickCount % DAMAGE_INTERVAL == 0) {
                entity.hurt(entity.damageSources().starve(), 1.0f + amplifier);
            }
        }
        
        // Spawn particles
        if (entity.tickCount % 30 == 0) {
            spawnDecompositionParticles(entity);
        }
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
    
    private void spawnDecompositionParticles(LivingEntity entity) {
        for (int i = 0; i < 6; i++) {
            double offsetX = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            double offsetY = entity.getRandom().nextDouble() * entity.getBbHeight();
            double offsetZ = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.COMPOSTER,
                entity.getX() + offsetX,
                entity.getY() + offsetY,
                entity.getZ() + offsetZ,
                0, 0.03, 0
            );
        }
    }
}
