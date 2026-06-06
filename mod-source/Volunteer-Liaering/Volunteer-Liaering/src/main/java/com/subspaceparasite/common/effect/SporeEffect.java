package com.subspaceparasite.common.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Spore Effect - Creates spore clouds that cause suffocation
 * Represents fungal spore release from infected hosts
 * 
 * @author SRP Port Team
 */
public class SporeEffect extends BaseSRPEffect {
    
    private static final int SPORE_CLOUD_INTERVAL = 60;
    private static final float SPORE_DAMAGE = 0.5f;
    private static final double SPORE_CLOUD_RADIUS = 3.0;
    
    public SporeEffect() {
        super(MobEffectCategory.HARMFUL, 0x9ACD32);
    }
    
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnSporeParticles(entity);
            return;
        }
        
        // Generate spore clouds periodically
        if (entity.tickCount % SPORE_CLOUD_INTERVAL == 0) {
            generateSporeCloud(entity, amplifier);
        }
        
        // Apply suffocation damage when in spore cloud
        if (entity.tickCount % 20 == 0 && shouldTakeSporeDamage(entity)) {
            entity.hurt(entity.damageSources().magic(), SPORE_DAMAGE * (amplifier + 1));
        }
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
    
    /**
     * Generate a spore cloud around the entity
     */
    private void generateSporeCloud(LivingEntity entity, int amplifier) {
        double radius = SPORE_CLOUD_RADIUS * (1 + amplifier * 0.2);
        
        // Create particle cloud
        for (int i = 0; i < 20; i++) {
            double angle = (i / 20.0) * Math.PI * 2;
            double x = entity.getX() + Math.cos(angle) * radius;
            double y = entity.getY() + entity.getBbHeight() * 0.5;
            double z = entity.getZ() + Math.sin(angle) * radius;
            
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.SPORE_BLOSSOM_AIR,
                x, y, z,
                0, 0.01, 0
            );
        }
    }
    
    /**
     * Check if entity should take damage from spores
     */
    private boolean shouldTakeSporeDamage(LivingEntity entity) {
        // Check for nearby spore clouds or other infected entities
        return entity.getRandom().nextFloat() < 0.1f;
    }
    
    private void spawnSporeParticles(LivingEntity entity) {
        for (int i = 0; i < 5; i++) {
            double offsetX = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            double offsetY = entity.getRandom().nextDouble() * entity.getBbHeight();
            double offsetZ = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER,
                entity.getX() + offsetX,
                entity.getY() + offsetY,
                entity.getZ() + offsetZ,
                0, 0.02, 0
            );
        }
    }
}
