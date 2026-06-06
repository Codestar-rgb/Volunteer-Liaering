package com.subspaceparasite.common.effect;

import com.subspaceparasite.core.ModEffects;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Nexus Link Effect - Establishes hive mind connection
 * Represents the psychic link between host and parasite nexus
 * 
 * @author SRP Port Team
 */
public class NexusLinkEffect extends BaseSRPEffect {
    
    private static final int SYNC_INTERVAL = 100;
    private static final double LINK_RANGE = 64.0;
    
    public NexusLinkEffect() {
        super(MobEffectCategory.NEUTRAL, 0x4169E1);
    }
    
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnNexusParticles(entity);
            return;
        }
        
        // Sync with nearby nexus-linked entities
        if (entity.tickCount % SYNC_INTERVAL == 0) {
            syncWithHiveMind(entity, amplifier);
        }
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
    
    /**
     * Synchronize entity with the hive mind network
     */
    private void syncWithHiveMind(LivingEntity entity, int amplifier) {
        // Find other linked entities within range
        // Share awareness, coordinates, and status
        double range = LINK_RANGE * (1 + amplifier * 0.1);
        
        // This would integrate with AI and networking systems
    }
    
    /**
     * Check if entity is connected to hive mind
     */
    public static boolean isConnectedToHive(LivingEntity entity) {
        return entity.hasEffect(ModEffects.NEXUS_LINK.get());
    }
    
    private void spawnNexusParticles(LivingEntity entity) {
        for (int i = 0; i < 3; i++) {
            double offsetX = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            double offsetY = entity.getRandom().nextDouble() * entity.getBbHeight();
            double offsetZ = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.PORTAL,
                entity.getX() + offsetX,
                entity.getY() + offsetY,
                entity.getZ() + offsetZ,
                0, 0.03, 0
            );
        }
    }
}
