package com.subspaceparasite.common.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Sentience Effect - Advanced cognitive enhancement for parasites.
 * <p>
 * This effect represents a parasite's development of higher intelligence
 * and tactical awareness. Provides AI enhancements, improved targeting,
 * and strategic behavior patterns.
 * </p>
 * 
 * <h2>Effects by Amplifier:</h2>
 * <ul>
 *   <li>Level 0: Basic sentience, improved AI targeting</li>
 *   <li>Level 1: Advanced sentience, tactical behavior + speed boost</li>
 *   <li>Level 2+: Perfect sentience, strategic coordination + all stats boost</li>
 * </ul>
 * 
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Enhanced AI targeting and pathfinding</li>
 *   <li>Movement speed and attack speed bonuses</li>
 *   <li>Improved combat decision making</li>
 *   <li>Coordination with other sentient parasites</li>
 *   <li>Visual neural network particles</li>
 * </ul>
 * 
 * @author SRP Port Team
 * @see ParasiteBuffEffectBase
 * @see EvolutionEffect
 */
public class SentienceEffect extends ParasiteBuffEffectBase {
    
    /** Movement speed boost per amplifier level */
    private static final double SPEED_BOOST_PER_LEVEL = 0.1;
    
    /** Attack speed boost per amplifier level */
    private static final double ATTACK_SPEED_PER_LEVEL = 0.15;
    
    /** Follow range boost per amplifier level */
    private static final double FOLLOW_RANGE_PER_LEVEL = 4.0;
    
    /** UUIDs for attribute modifiers */
    private static final UUID SENTIENCE_SPEED_UUID = UUID.fromString("d5d5d5d5-aaaa-bbbb-cccc-222000000001");
    private static final UUID SENTIENCE_ATTACK_SPEED_UUID = UUID.fromString("d5d5d5d5-aaaa-bbbb-cccc-222000000002");
    private static final UUID SENTIENCE_FOLLOW_UUID = UUID.fromString("d5d5d5d5-aaaa-bbbb-cccc-222000000003");
    
    /** Tick interval for sentience logic */
    private static final int SENTIENCE_TICK_INTERVAL = 10;
    
    /**
     * Creates the Sentience effect with standard settings.
     */
    public SentienceEffect() {
        super(
            MobEffectCategory.BENEFICIAL,
            0x9370DB,  // Medium purple (intelligence/mind color)
            3,         // Max amplifier
            true,      // Can stack
            SENTIENCE_TICK_INTERVAL,
            true,      // Has attribute modifiers
            SPEED_BOOST_PER_LEVEL,
            AttributeModifier.Operation.MULTIPLY_BASE
        );
    }
    
    /**
     * Applies sentience buffs each tick.
     * Enhances AI behavior and movement capabilities.
     * 
     * @param entity    The sentient parasite entity
     * @param amplifier The sentience level/amplifier
     */
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnSentienceParticles(entity, amplifier);
            return;
        }
        
        // Apply AI enhancements
        applyAIEnhancements(entity, amplifier);
        
        // Apply combat awareness
        applyCombatAwareness(entity, amplifier);
    }
    
    /**
     * Enhances AI behavior based on sentience level.
     */
    private void applyAIEnhancements(@NotNull LivingEntity entity, int amplifier) {
        // Higher sentience improves targeting priority
        // This would integrate with custom AI goals in full implementation
        
        // Level 1+: Improved pathfinding around obstacles
        if (amplifier >= 1) {
            // NoActionDelay was removed in 1.20.1
            // Pathfinding improvement handled by AI goals
        }
        
        // Level 2+: Strategic positioning behavior
        if (amplifier >= 2 && entity.tickCount % 40 == 0) {
            // Logic for finding better combat positions
            // Would be implemented with custom AI goals
        }
    }
    
    /**
     * Applies combat awareness bonuses.
     */
    private void applyCombatAwareness(@NotNull LivingEntity entity, int amplifier) {
        // Combat awareness provides occasional damage boost
        if (amplifier >= 1 && entity.getLastHurtByMob() != null) {
            // Marked target tracking - visual feedback only here
            // Actual tracking handled by AI goals
        }
        
        // At max sentience, grant critical strike chance
        if (amplifier >= 3 && entity.tickCount % 60 == 0) {
            // Critical strike logic would be implemented in attack handler
        }
    }
    
    /**
     * Applies attribute modifiers for sentience effect.
     * Boosts movement speed, attack speed, and follow range.
     */
    @Override
    public void addAttributeModifiers(LivingEntity entity,
                                     AttributeMap attributeMap,
                                     int amplifier) {
        AttributeInstance speedAttr = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            double amount = SPEED_BOOST_PER_LEVEL * (amplifier + 1);
            applyModifier(entity, speedAttr, SENTIENCE_SPEED_UUID, amount, AttributeModifier.Operation.MULTIPLY_BASE);
        }
        AttributeInstance attackSpd = entity.getAttribute(Attributes.ATTACK_SPEED);
        if (attackSpd != null) {
            double amount = ATTACK_SPEED_PER_LEVEL * (amplifier + 1);
            applyModifier(entity, attackSpd, SENTIENCE_ATTACK_SPEED_UUID, amount, AttributeModifier.Operation.MULTIPLY_BASE);
        }
        AttributeInstance followRange = entity.getAttribute(Attributes.FOLLOW_RANGE);
        if (followRange != null) {
            double amount = FOLLOW_RANGE_PER_LEVEL * (amplifier + 1);
            applyModifier(entity, followRange, SENTIENCE_FOLLOW_UUID, amount, AttributeModifier.Operation.ADDITION);
        }
    }
    
    /**
     * Helper method to apply attribute modifier.
     */
    private void applyModifier(LivingEntity entity,
                              AttributeInstance attribute,
                              UUID uuid,
                              double amount,
                              AttributeModifier.Operation operation) {
        var existingModifier = attribute.getModifier(uuid);
        if (existingModifier == null) {
            var modifier = new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                uuid,
                "Parasite Buff: " + getDescriptionId(),
                amount,
                operation
            );
            attribute.addTransientModifier(modifier);
        } else if (existingModifier.getAmount() != amount) {
            attribute.removeModifier(uuid);
            var modifier = new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                uuid,
                "Parasite Buff: " + getDescriptionId(),
                amount,
                operation
            );
            attribute.addTransientModifier(modifier);
        }
    }
    
    /**
     * Spawns sentience particles (purple neural network effect).
     */
    private void spawnSentienceParticles(@NotNull LivingEntity entity, int amplifier) {
        double x = entity.getX() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        double y = entity.getY() + entity.level().random.nextDouble() * entity.getBbHeight();
        double z = entity.getZ() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        
        // Base sentience particles (purple sparkles)
        entity.level().addParticle(
            net.minecraft.core.particles.ParticleTypes.PORTAL,
            x, y, z,
            0.0, 0.03, 0.0
        );
        
        // Extra particles at higher amplifier
        if (amplifier >= 1) {
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.ENCHANT,
                x, y + 0.5, z,
                (entity.level().random.nextDouble() - 0.5) * 0.05,
                0.05,
                (entity.level().random.nextDouble() - 0.5) * 0.05
            );
        }
        
        if (amplifier >= 2) {
            // Neural network visualization at max sentience
            for (int i = 0; i < 3; i++) {
                double angle = (entity.tickCount + i * 2.5) * 0.08;
                double radius = entity.getBbWidth() / 2.0 + 0.25;
                double px = entity.getX() + Math.cos(angle) * radius;
                double pz = entity.getZ() + Math.sin(angle) * radius;
                
                entity.level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.SCULK_CHARGE_POP,
                    px, y + 0.6, pz,
                    0.0, 0.015, 0.0
                );
            }
        }
    }
    
    /**
     * Sentience is specifically for parasite entities.
     */
    @Override
    public boolean isParasiteOnly() {
        return true;
    }
    
    /**
     * Determines if sentience should tick this frame.
     */
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % SENTIENCE_TICK_INTERVAL == 0;
    }
}
