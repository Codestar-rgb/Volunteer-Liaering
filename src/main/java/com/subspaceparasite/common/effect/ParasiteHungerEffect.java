package com.subspaceparasite.common.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Parasite Hunger Effect - Accelerated nutrient consumption.
 * Represents parasite draining host nutritional resources.
 * 
 * <h2>Effects by Amplifier:</h2>
 * <ul>
 *   <li>Level 0: Rapid food exhaustion</li>
 *   <li>Level 1: Extreme food exhaustion + weakness</li>
 *   <li>Level 2+: Starvation damage when food depleted</li>
 * </ul>
 * 
 * @author SRP Port Team
 */
public class ParasiteHungerEffect extends BaseSRPEffect {
    
    private static final float EXHAUSTION_MULTIPLIER = 3.0f;
    private static final int DAMAGE_INTERVAL = 60;
    
    public ParasiteHungerEffect() {
        super(MobEffectCategory.HARMFUL, 0x8B4513, 3, true, 20);
    }
    
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnHungerParticles(entity, amplifier);
            return;
        }
        
        // Apply extreme exhaustion (players only have food mechanics)
        if (entity instanceof Player player) {
            float exhaustion = EXHAUSTION_MULTIPLIER * (amplifier + 1) * 0.01f;
            player.causeFoodExhaustion(exhaustion);
            
            // Deal damage when starving
            if (player.getFoodData().getFoodLevel() <= 0) {
                if (entity.tickCount % DAMAGE_INTERVAL == 0) {
                    entity.hurt(entity.damageSources().starve(), 1.0f + amplifier);
                }
            }
        } else {
            // Non-player entities take direct damage representing nutrient drain
            if (entity.tickCount % DAMAGE_INTERVAL == 0) {
                entity.hurt(entity.damageSources().magic(), 0.5f + amplifier * 0.25f);
            }
        }
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
    
    private void spawnHungerParticles(LivingEntity entity, int amplifier) {
        for (int i = 0; i < 2 + amplifier; i++) {
            double offsetX = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            double offsetY = entity.getRandom().nextDouble() * entity.getBbHeight();
            double offsetZ = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.FALLING_HONEY,
                entity.getX() + offsetX,
                entity.getY() + offsetY,
                entity.getZ() + offsetZ,
                0, 0.02, 0
            );
        }
    }
}
