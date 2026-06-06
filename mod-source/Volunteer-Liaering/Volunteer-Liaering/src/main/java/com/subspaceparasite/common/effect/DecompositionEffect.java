package com.subspaceparasite.common.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Decomposition Effect - Rapid hunger exhaustion.
 * Represents the decomposition of host nutrients by parasites.
 * 
 * <h2>Effects by Amplifier:</h2>
 * <ul>
 *   <li>Level 0: Moderate food exhaustion</li>
 *   <li>Level 1: Severe food exhaustion, starvation damage when empty</li>
 *   <li>Level 2+: Extreme exhaustion + direct starvation damage</li>
 * </ul>
 * 
 * @author SRP Port Team
 */
public class DecompositionEffect extends BaseSRPEffect {
    
    private static final float EXHAUSTION_PER_TICK = 0.05f;
    private static final int DAMAGE_INTERVAL = 40;
    
    public DecompositionEffect() {
        super(MobEffectCategory.HARMFUL, 0x3B5323, 3, true, 20);
    }
    
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnDecompositionParticles(entity, amplifier);
            return;
        }
        
        // Apply exhaustion to cause hunger (players only)
        if (entity instanceof Player player) {
            float exhaustion = EXHAUSTION_PER_TICK * (amplifier + 1);
            player.causeFoodExhaustion(exhaustion);
            
            // Deal damage if starving
            if (player.getFoodData().getFoodLevel() <= 0) {
                if (entity.tickCount % DAMAGE_INTERVAL == 0) {
                    entity.hurt(entity.damageSources().starve(), 1.0f + amplifier);
                }
            }
        } else {
            // Non-player entities take direct magic damage representing nutrient loss
            if (entity.tickCount % DAMAGE_INTERVAL == 0) {
                entity.hurt(entity.damageSources().magic(), 0.5f + amplifier * 0.5f);
            }
        }
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
    
    private void spawnDecompositionParticles(LivingEntity entity, int amplifier) {
        for (int i = 0; i < 3 + amplifier; i++) {
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
