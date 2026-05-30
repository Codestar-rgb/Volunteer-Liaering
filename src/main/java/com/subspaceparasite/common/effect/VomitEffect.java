package com.subspaceparasite.common.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import org.jetbrains.annotations.NotNull;

/**
 * Vomit Effect - Causes nausea and random item drops
 * Represents host rejection of parasite biomass
 * 
 * @author SRP Port Team
 */
public class VomitEffect extends BaseSRPEffect {
    
    private static final int VOMIT_INTERVAL = 80;
    private static final float DROP_CHANCE = 0.3f;
    
    public VomitEffect() {
        super(MobEffectCategory.HARMFUL, 0x6B8E23);
    }
    
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnVomitParticles(entity);
            return;
        }
        
        // Periodic vomiting effect
        if (entity.tickCount % VOMIT_INTERVAL == 0) {
            triggerVomit(entity, amplifier);
        }
    }
    
    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
    
    /**
     * Trigger vomiting event - may drop consumed items
     */
    private void triggerVomit(LivingEntity entity, int amplifier) {
        // Spawn vomit particles
        for (int i = 0; i < 15; i++) {
            spawnVomitParticles(entity);
        }
        
        // Chance to drop recently consumed item
        if (entity.getRandom().nextFloat() < DROP_CHANCE * (amplifier + 1)) {
            dropRandomItem(entity);
        }
    }
    
    /**
     * Drop a random item from entity's inventory or last consumed item
     */
    private void dropRandomItem(LivingEntity entity) {
        // This would integrate with inventory system
        // For now, just spawn particle indication
    }
    
    private void spawnVomitParticles(LivingEntity entity) {
        for (int i = 0; i < 3; i++) {
            double offsetX = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            double offsetY = entity.getRandom().nextDouble() * entity.getBbHeight() * 0.3f;
            double offsetZ = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.COMPOSTER,
                entity.getX() + offsetX,
                entity.getY() + offsetY,
                entity.getZ() + offsetZ,
                (entity.getRandom().nextDouble() - 0.5) * 0.2,
                0.1,
                (entity.getRandom().nextDouble() - 0.5) * 0.2
            );
        }
    }
}
