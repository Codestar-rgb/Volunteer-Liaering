package com.subspaceparasite.common.effect;

import com.subspaceparasite.config.ModConfigSystems;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * Parasite Vitality effect - enhances parasite combat capabilities.
 * <p>
 * A beneficial effect exclusive to parasites that increases health,
 * damage, and regeneration. Stacks with amplifier levels.
 * </p>
 * 
 * @author SubspaceParasite Team
 * @since 1.0.0
 */
public class ParasiteVitalityEffect extends CustomMobEffect {
    
    /** Health boost per amplifier level (in hearts) */
    private static final float HEALTH_BOOST_PER_LEVEL = 2.0F;
    
    /** Damage boost percentage per level */
    private static final float DAMAGE_BOOST_PER_LEVEL = 0.1F;
    
    /** Regeneration amount per tick at level I */
    private static final float REGEN_PER_TICK = 0.1F;
    
    /** Tick interval for regeneration */
    private static final int REGEN_INTERVAL = 20; // Once per second
    
    public ParasiteVitalityEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF6347, 5, true);
    }
    
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return;
        
        // Apply regeneration periodically
        int tickCount = entity.tickCount;
        if (tickCount % REGEN_INTERVAL == 0 && entity.getHealth() < entity.getMaxHealth()) {
            float regenAmount = calculateRegeneration(amplifier);
            entity.heal(regenAmount);
        }
        
        // Update attribute modifiers
        updateAttributeModifiers(entity, amplifier);
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
    
    /**
     * Updates all attribute modifiers for this effect.
     * 
     * @param entity the affected entity
     * @param amplifier the effect amplifier
     */
    protected void updateAttributeModifiers(LivingEntity entity, int amplifier) {
        updateHealthModifier(entity, amplifier);
        updateDamageModifier(entity, amplifier);
    }
    
    /**
     * Updates the health boost modifier.
     * 
     * @param entity the affected entity
     * @param amplifier the effect amplifier
     */
    protected void updateHealthModifier(LivingEntity entity, int amplifier) {
        java.util.UUID modifierId = java.util.UUID.fromString("parasite-vitality-health-0001");
        
        // Remove old modifier
        entity.getAttribute(Attributes.MAX_HEALTH).removeModifier(modifierId);
        
        // Calculate new health boost
        float healthBoost = HEALTH_BOOST_PER_LEVEL * (amplifier + 1);
        
        // Add new modifier
        AttributeModifier modifier = new AttributeModifier(
            modifierId,
            "Parasite vitality health boost",
            healthBoost,
            AttributeModifier.Operation.ADDITION
        );
        
        entity.getAttribute(Attributes.MAX_HEALTH).addTransientModifier(modifier);
        
        // Heal for the difference if health increased
        if (entity.getHealth() < entity.getMaxHealth()) {
            entity.setHealth(Math.min(entity.getHealth() + healthBoost, entity.getMaxHealth()));
        }
    }
    
    /**
     * Updates the damage boost modifier.
     * 
     * @param entity the affected entity
     * @param amplifier the effect amplifier
     */
    protected void updateDamageModifier(LivingEntity entity, int amplifier) {
        java.util.UUID modifierId = java.util.UUID.fromString("parasite-vitality-damage-0001");
        
        // Remove old modifier
        entity.getAttribute(Attributes.ATTACK_DAMAGE).removeModifier(modifierId);
        
        // Calculate damage boost percentage
        float damageBoost = DAMAGE_BOOST_PER_LEVEL * (amplifier + 1);
        
        // Add new modifier
        AttributeModifier modifier = new AttributeModifier(
            modifierId,
            "Parasite vitality damage boost",
            damageBoost,
            AttributeModifier.Operation.MULTIPLY_BASE
        );
        
        entity.getAttribute(Attributes.ATTACK_DAMAGE).addTransientModifier(modifier);
    }
    
    /**
     * Calculates regeneration amount based on amplifier.
     * 
     * @param amplifier the effect amplifier
     * @return regeneration amount in half-hearts
     */
    protected float calculateRegeneration(int amplifier) {
        float baseRegen = REGEN_PER_TICK;
        float scaledRegen = calculateScaledDamage(baseRegen, amplifier, 0.5F);
        
        // Apply config multiplier
        scaledRegen *= (float) ModConfigSystems.getEffectPowerMultiplier("parasite_vitality");
        
        return scaledRegen;
    }
    
    @Override
    public void removeAttributeModifiers(LivingEntity entity, 
                                         net.minecraft.world.entity.ai.attributes.AttributeMap attributes, 
                                         int amplifier) {
        // Clean up all modifiers when effect ends
        entity.getAttribute(Attributes.MAX_HEALTH).removeModifier(
            java.util.UUID.fromString("parasite-vitality-health-0001")
        );
        entity.getAttribute(Attributes.ATTACK_DAMAGE).removeModifier(
            java.util.UUID.fromString("parasite-vitality-damage-0001")
        );
        
        super.removeAttributeModifiers(entity, attributes, amplifier);
    }
}
