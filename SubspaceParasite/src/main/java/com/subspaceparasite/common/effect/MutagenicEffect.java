package com.subspaceparasite.common.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Mutagenic Effect - Triggers genetic mutations in host
 * Represents the mutagenic properties of parasite DNA integration
 * 
 * @author SRP Port Team
 */
public class MutagenicEffect extends BaseSRPEffect {
    
    private static final int MUTATION_CHECK_INTERVAL = 200;
    private static final float MUTATION_CHANCE_PER_CHECK = 0.05f;
    
    public MutagenicEffect() {
        super(MobEffectCategory.HARMFUL, 0x9932CC);
    }
    
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnMutagenicParticles(entity);
            return;
        }
        
        // Check for mutation triggers
        if (entity.tickCount % MUTATION_CHECK_INTERVAL == 0) {
            checkMutationTrigger(entity, amplifier);
        }
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
    
    /**
     * Check if a mutation should be triggered
     */
    private void checkMutationTrigger(LivingEntity entity, int amplifier) {
        float chance = MUTATION_CHANCE_PER_CHECK * (amplifier + 1);
        if (entity.getRandom().nextFloat() < chance) {
            onMutationTriggered(entity, amplifier);
        }
    }
    
    /**
     * Called when a mutation is triggered
     */
    private void onMutationTriggered(LivingEntity entity, int amplifier) {
        // Apply random mutation effects
        // This could include: stat changes, new abilities, appearance changes
        if (!entity.level().isClientSide()) {
            // Intensive particle burst
            for (int i = 0; i < 25; i++) {
                spawnMutagenicParticles(entity);
            }
        }
    }
    
    private void spawnMutagenicParticles(LivingEntity entity) {
        for (int i = 0; i < 3; i++) {
            double offsetX = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            double offsetY = entity.getRandom().nextDouble() * entity.getBbHeight();
            double offsetZ = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.ENCHANT,
                entity.getX() + offsetX,
                entity.getY() + offsetY,
                entity.getZ() + offsetZ,
                (entity.getRandom().nextDouble() - 0.5) * 0.5,
                0.05,
                (entity.getRandom().nextDouble() - 0.5) * 0.5
            );
        }
    }
}
