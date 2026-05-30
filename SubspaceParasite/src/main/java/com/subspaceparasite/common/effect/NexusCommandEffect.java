package com.subspaceparasite.common.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Nexus Command Effect - Grants colony command authority
 * Represents elevated control within the parasite hierarchy
 * 
 * @author SRP Port Team
 */
public class NexusCommandEffect extends BaseSRPEffect {
    
    private static final int COMMAND_RANGE = 32;
    private static final int MAX_COMMANDS = 3;
    
    public NexusCommandEffect() {
        super(MobEffectCategory.NEUTRAL, 0x1E90FF);
    }
    
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnCommandParticles(entity);
            return;
        }
        
        // Process command queue for linked entities
        if (entity.tickCount % 20 == 0) {
            processCommands(entity, amplifier);
        }
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
    
    /**
     * Process and execute commands to linked entities
     */
    private void processCommands(LivingEntity entity, int amplifier) {
        int maxCommands = Math.min(MAX_COMMANDS, amplifier + 1);
        double range = COMMAND_RANGE * (1 + amplifier * 0.2);
        
        // This would integrate with AI command system
        // Allow issuing orders to hive-linked entities
    }
    
    /**
     * Check if entity has command authority
     */
    public static boolean hasCommandAuthority(LivingEntity entity) {
        return entity.hasEffect(ModEffects.NEXUS_COMMAND.get());
    }
    
    /**
     * Get command level based on effect amplifier
     */
    public static int getCommandLevel(LivingEntity entity) {
        if (entity.hasEffect(ModEffects.NEXUS_COMMAND.get())) {
            return entity.getEffect(ModEffects.NEXUS_COMMAND.get()).getAmplifier() + 1;
        }
        return 0;
    }
    
    private void spawnCommandParticles(LivingEntity entity) {
        for (int i = 0; i < 4; i++) {
            double offsetX = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            double offsetY = entity.getRandom().nextDouble() * entity.getBbHeight();
            double offsetZ = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.END_ROD,
                entity.getX() + offsetX,
                entity.getY() + offsetY,
                entity.getZ() + offsetZ,
                0, 0.02, 0
            );
        }
    }
}
