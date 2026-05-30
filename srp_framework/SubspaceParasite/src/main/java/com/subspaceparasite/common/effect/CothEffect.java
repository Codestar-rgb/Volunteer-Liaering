package com.subspaceparasite.common.effect;

import com.subspaceparasite.api.parasite.EvoPhase;
import com.subspaceparasite.common.capability.ParasiteCapability;
import com.subspaceparasite.common.capability.ParasiteCapabilityProvider;
import com.subspaceparasite.config.ModConfigSystems;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.CapabilityManager;

/**
 * Call of the Hive (COTH) - The primary infection effect.
 * <p>
 * This effect represents the hive mind's call infecting entities.
 * It deals periodic damage, accumulates infection levels, and can
 * eventually convert the host into a parasite.
 * </p>
 * 
 * <b>Effects by level:</b>
 * <ul>
 *   <li>Level I: Basic WITHER damage, slow infection accumulation</li>
 *   <li>Level II: Increased damage, faster infection, movement slowdown</li>
 *   <li>Level III: Maximum damage, rapid infection, weakness applied</li>
 * </ul>
 * 
 * @author SubspaceParasite Team
 * @since 1.0.0
 */
public class CothEffect extends CustomMobEffect {
    
    /** Base damage per tick at level I */
    private static final float BASE_DAMAGE = 0.5F;
    
    /** Damage multiplier per amplifier level */
    private static final float DAMAGE_PER_LEVEL = 0.25F;
    
    /** Infection points added per tick */
    private static final float INFECTION_PER_TICK = 0.05F;
    
    /** Tick interval for damage application (in ticks) */
    private static final int DAMAGE_INTERVAL = 20; // Once per second
    
    /**
     * Creates the COTH effect.
     */
    public CothEffect() {
        super(MobEffectCategory.HARMFUL, 0x4A0E0E, 2, true);
    }
    
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return;
        
        // Apply damage periodically
        int tickCount = entity.tickCount;
        if (tickCount % DAMAGE_INTERVAL == 0) {
            applyDamage(entity, amplifier);
        }
        
        // Accumulate infection in capability
        accumulateInfection(entity, amplifier);
        
        // Apply additional debuffs based on amplifier
        applyAdditionalDebuffs(entity, amplifier);
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // Check every tick for infection accumulation
        return true;
    }
    
    /**
     * Applies periodic damage to the affected entity.
     * 
     * @param entity the affected entity
     * @param amplifier the effect amplifier (0-2)
     */
    protected void applyDamage(LivingEntity entity, int amplifier) {
        float damage = calculateScaledDamage(BASE_DAMAGE, amplifier, DAMAGE_PER_LEVEL);
        
        // Scale damage based on config
        damage *= (float) ModConfigSystems.getCOTHDamageMultiplier();
        
        // Apply damage (true damage, ignores armor)
        entity.hurt(entity.damageSources().magic(), damage);
    }
    
    /**
     * Accumulates infection points in the entity's capability.
     * 
     * @param entity the affected entity
     * @param amplifier the effect amplifier
     */
    protected void accumulateInfection(LivingEntity entity, int amplifier) {
        entity.getCapability(ParasiteCapabilityProvider.PARASITE_CAPABILITY).ifPresent(cap -> {
            if (cap instanceof ParasiteCapability parasiteCap) {
                // Skip if already immune or fully infected
                if (parasiteCap.isImmune()) return;
                if (parasiteCap.getInfectionLevel() >= 100) return;
                
                // Calculate infection rate based on amplifier
                float infectionRate = INFECTION_PER_TICK * (amplifier + 1);
                
                // Apply resistance reduction
                float resistance = parasiteCap.getInfectionResistance();
                infectionRate *= (1.0F - Math.min(resistance, 0.9F));
                
                // Add infection points
                parasiteCap.addInfection(infectionRate);
                
                // Check for conversion threshold
                if (parasiteCap.getInfectionLevel() >= 100) {
                    onFullInfection(entity, parasiteCap);
                }
            }
        });
    }
    
    /**
     * Applies additional debuffs based on COTH level.
     * 
     * @param entity the affected entity
     * @param amplifier the effect amplifier
     */
    protected void applyAdditionalDebuffs(LivingEntity entity, int amplifier) {
        // Level II+ adds movement slowdown
        if (amplifier >= 1 && entity.tickCount % 40 == 0) {
            // Slowdown handled by vanilla SLOW_EFFECT in InfectionComponent
        }
        
        // Level III adds weakness
        if (amplifier >= 2 && entity.tickCount % 60 == 0) {
            // Weakness handled by vanilla WEAKNESS in InfectionComponent
        }
    }
    
    /**
     * Called when infection reaches 100%.
     * <p>
     * Triggers entity conversion logic through InfectionComponent.
     * </p>
     * 
     * @param entity the fully infected entity
     * @param capability the parasite capability
     */
    protected void onFullInfection(LivingEntity entity, ParasiteCapability capability) {
        // Mark as fully infected
        capability.setInfectionLevel(100);
        
        // Attempt conversion if entity is not already a parasite
        if (!(entity instanceof com.subspaceparasite.common.entity.base.EntityParasiteBase)) {
            // Conversion will be handled by InfectionComponent.checkConversion()
            // This is called from the capability update event
        }
    }
    
    /**
     * Gets the estimated time to full infection in seconds.
     * 
     * @param amplifier the effect amplifier
     * @param resistance the entity's infection resistance
     * @return time in seconds until conversion
     */
    public static float getTimeToFullInfection(int amplifier, float resistance) {
        float effectiveRate = INFECTION_PER_TICK * (amplifier + 1) * (1.0F - Math.min(resistance, 0.9F));
        if (effectiveRate <= 0) return Float.MAX_VALUE;
        
        float ticksToFull = 100.0F / effectiveRate;
        return ticksToFull / 20.0F; // Convert to seconds
    }
}
