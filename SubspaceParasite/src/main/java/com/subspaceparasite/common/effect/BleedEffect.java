package com.subspaceparasite.common.effect;

import com.subspaceparasite.config.ModConfigSystems;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * Bleeding effect - causes continuous damage based on movement.
 * <p>
 * This effect deals more damage when the entity moves or sprints,
 * simulating blood loss from wounds. Standing still reduces damage.
 * </p>
 * 
 * @author SubspaceParasite Team
 * @since 1.0.0
 */
public class BleedEffect extends CustomMobEffect {
    
    /** Base damage per tick when stationary */
    private static final float BASE_DAMAGE_STATIONARY = 0.25F;
    
    /** Damage per tick when moving normally */
    private static final float DAMAGE_MOVING = 0.5F;
    
    /** Damage per tick when sprinting */
    private static final float DAMAGE_SPRINTING = 1.0F;
    
    /** Movement speed reduction per amplifier level */
    private static final float SPEED_REDUCTION_PER_LEVEL = 0.05F;
    
    /** Tick interval for damage application */
    private static final int DAMAGE_INTERVAL = 10; // Twice per second
    
    public BleedEffect() {
        super(MobEffectCategory.HARMFUL, 0x8B0000, 4, true);
    }
    
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return;
        
        int tickCount = entity.tickCount;
        if (tickCount % DAMAGE_INTERVAL == 0) {
            float damage = calculateDamage(entity, amplifier);
            entity.hurt(entity.damageSources().magic(), damage);
            
            // Spawn blood particles on server
            if (!entity.level().isClientSide && tickCount % 40 == 0) {
                spawnBloodParticles(entity);
            }
        }
        
        // Apply movement speed reduction
        applySpeedReduction(entity, amplifier);
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
    
    /**
     * Calculates damage based on entity's movement state.
     * 
     * @param entity the affected entity
     * @param amplifier the effect amplifier
     * @return damage amount
     */
    protected float calculateDamage(LivingEntity entity, int amplifier) {
        float baseDamage;
        
        if (!entity.isMoving()) {
            baseDamage = BASE_DAMAGE_STATIONARY;
        } else if (entity.isSprinting()) {
            baseDamage = DAMAGE_SPRINTING;
        } else {
            baseDamage = DAMAGE_MOVING;
        }
        
        // Scale with amplifier
        float scaledDamage = calculateScaledDamage(baseDamage, amplifier, 0.25F);
        
        // Apply config multiplier
        scaledDamage *= (float) ModConfigSystems.getEffectDamageMultiplier("bleed");
        
        return scaledDamage;
    }
    
    /**
     * Applies movement speed reduction based on amplifier.
     * 
     * @param entity the affected entity
     * @param amplifier the effect amplifier
     */
    protected void applySpeedReduction(LivingEntity entity, int amplifier) {
        float reduction = SPEED_REDUCTION_PER_LEVEL * (amplifier + 1);
        
        // Remove old modifier if exists
        entity.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(
            java.util.UUID.fromString("bleed-speed-reduction-0001")
        );
        
        // Add new modifier
        AttributeModifier modifier = new AttributeModifier(
            java.util.UUID.fromString("bleed-speed-reduction-0001"),
            "Bleed speed reduction",
            -reduction,
            AttributeModifier.Operation.MULTIPLY_TOTAL
        );
        
        entity.getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(modifier);
    }
    
    /**
     * Spawns blood particle effects around the entity.
     * 
     * @param entity the affected entity
     */
    protected void spawnBloodParticles(LivingEntity entity) {
        // Particle spawning will be implemented in client handler
        // Server-side placeholder
    }
    
    /**
     * Checks if the entity is currently moving.
     * 
     * @param entity the entity to check
     * @return true if moving
     */
    protected boolean isEntityMoving(LivingEntity entity) {
        return entity.deltaMovement.horizontalDistanceSqr() > 0.01;
    }
}
