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
 * Evolution Effect - Represents parasite evolutionary progress.
 * <p>
 * This is a core evolution buff that indicates a parasite has successfully
 * evolved to a higher form. Provides stacking bonuses to all attributes
 * and unlocks advanced parasite abilities.
 * </p>
 * 
 * <h2>Effects by Amplifier:</h2>
 * <ul>
 *   <li>Level 0: Basic evolution, minor stat boost (+5%)</li>
 *   <li>Level 1: Advanced evolution, moderate stat boost (+10%)</li>
 *   <li>Level 2+: Perfect evolution, significant stat boost (+15%+)</li>
 * </ul>
 * 
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Multiplicative boost to all combat attributes</li>
 *   <li>Required for unlocking advanced parasite forms</li>
 *   <li>Synergizes with other evolution effects</li>
 *   <li>Persistent across parasite transformations</li>
 *   <li>Visual evolution aura particles</li>
 * </ul>
 * 
 * @author SRP Port Team
 * @see ParasiteBuffEffectBase
 * @see AdaptationEffect
 * @see SentienceEffect
 * @see DerivationEffect
 */
public class EvolutionEffect extends ParasiteBuffEffectBase {
    
    /** Base percentage boost per amplifier level */
    private static final double BASE_BOOST_PERCENT = 0.05;
    
    /** UUIDs for attribute modifiers */
    private static final UUID EVOLUTION_HEALTH_UUID = UUID.fromString("d5d5d5d5-aaaa-bbbb-cccc-555000000001");
    private static final UUID EVOLUTION_DAMAGE_UUID = UUID.fromString("d5d5d5d5-aaaa-bbbb-cccc-555000000002");
    private static final UUID EVOLUTION_SPEED_UUID = UUID.fromString("d5d5d5d5-aaaa-bbbb-cccc-555000000003");
    private static final UUID EVOLUTION_ARMOR_UUID = UUID.fromString("d5d5d5d5-aaaa-bbbb-cccc-555000000004");
    
    /** Tick interval for evolution logic */
    private static final int EVOLUTION_TICK_INTERVAL = 20;
    
    /**
     * Creates the Evolution effect with standard settings.
     */
    public EvolutionEffect() {
        super(
            MobEffectCategory.BENEFICIAL,
            0x3CB371,  // Medium sea green (evolution/growth color)
            3,         // Max amplifier
            true,      // Can stack
            EVOLUTION_TICK_INTERVAL,
            true,      // Has attribute modifiers
            BASE_BOOST_PERCENT,
            AttributeModifier.Operation.MULTIPLY_BASE
        );
    }
    
    /**
     * Applies evolution buffs each tick.
     * Enhances parasite capabilities based on evolution stage.
     * 
     * @param entity    The evolved parasite entity
     * @param amplifier The evolution stage/amplifier
     */
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            spawnEvolutionParticles(entity, amplifier);
            return;
        }
        
        // Apply evolution-specific logic
        applyEvolutionBonuses(entity, amplifier);
    }
    
    /**
     * Applies evolution-based bonuses to the entity.
     */
    private void applyEvolutionBonuses(@NotNull LivingEntity entity, int amplifier) {
        // Evolution increases maximum health progressively
        float healthBoost = (amplifier + 1) * 2.0f;
        if (entity.getHealth() < entity.getMaxHealth()) {
            entity.heal(healthBoost * 0.1f);
        }
        
        // Higher evolution stages grant regeneration
        if (amplifier >= 2 && entity.tickCount % 40 == 0) {
            entity.heal(1.0f);
        }
    }
    
    /**
     * Applies attribute modifiers for evolution effect.
     * Boosts max health, attack damage, movement speed, and armor.
     */
    @Override
    public void addAttributeModifiers(LivingEntity entity,
                                     AttributeMap attributeMap,
                                     int amplifier) {
        double boost = BASE_BOOST_PERCENT * (amplifier + 1);
        
        AttributeInstance maxHealth = entity.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            applyModifier(maxHealth, EVOLUTION_HEALTH_UUID, "Evolution Health Boost", boost, AttributeModifier.Operation.MULTIPLY_BASE);
        }
        
        AttributeInstance attackDmg = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDmg != null) {
            applyModifier(attackDmg, EVOLUTION_DAMAGE_UUID, "Evolution Damage Boost", boost, AttributeModifier.Operation.MULTIPLY_BASE);
        }
        
        AttributeInstance moveSpeed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (moveSpeed != null) {
            applyModifier(moveSpeed, EVOLUTION_SPEED_UUID, "Evolution Speed Boost", boost, AttributeModifier.Operation.MULTIPLY_BASE);
        }
        
        AttributeInstance armor = entity.getAttribute(Attributes.ARMOR);
        if (armor != null) {
            double armorBoost = 2.0 * (amplifier + 1);
            applyModifier(armor, EVOLUTION_ARMOR_UUID, "Evolution Armor Boost", armorBoost, AttributeModifier.Operation.ADDITION);
        }
    }
    
    /**
     * Removes attribute modifiers for evolution effect.
     */
    @Override
    public void removeAttributeModifiers(LivingEntity entity,
                                        AttributeMap attributeMap,
                                        int amplifier) {
        removeModifier(entity, Attributes.MAX_HEALTH, EVOLUTION_HEALTH_UUID);
        removeModifier(entity, Attributes.ATTACK_DAMAGE, EVOLUTION_DAMAGE_UUID);
        removeModifier(entity, Attributes.MOVEMENT_SPEED, EVOLUTION_SPEED_UUID);
        removeModifier(entity, Attributes.ARMOR, EVOLUTION_ARMOR_UUID);
    }
    
    /**
     * Helper method to apply an attribute modifier.
     */
    private void applyModifier(AttributeInstance attr, UUID uuid, String name, double amount, AttributeModifier.Operation op) {
        var existing = attr.getModifier(uuid);
        if (existing == null) {
            attr.addTransientModifier(new AttributeModifier(uuid, name, amount, op));
        } else if (existing.getAmount() != amount) {
            attr.removeModifier(uuid);
            attr.addTransientModifier(new AttributeModifier(uuid, name, amount, op));
        }
    }
    
    /**
     * Helper method to remove an attribute modifier.
     */
    private void removeModifier(LivingEntity entity, net.minecraft.world.entity.ai.attributes.Attribute attr, UUID uuid) {
        AttributeInstance instance = entity.getAttribute(attr);
        if (instance != null) {
            instance.removeModifier(uuid);
        }
    }
    
    /**
     * Spawns evolution particles (green energy swirls).
     */
    private void spawnEvolutionParticles(@NotNull LivingEntity entity, int amplifier) {
        double x = entity.getX() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        double y = entity.getY() + entity.level().random.nextDouble() * entity.getBbHeight();
        double z = entity.getZ() + (entity.level().random.nextDouble() - 0.5) * entity.getBbWidth();
        
        // Base evolution particles
        entity.level().addParticle(
            net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER,
            x, y, z,
            0.0, 0.03, 0.0
        );
        
        // Extra particles at higher amplifier
        if (amplifier >= 1) {
            entity.level().addParticle(
                net.minecraft.core.particles.ParticleTypes.COMPOSTER,
                x, y + 0.5, z,
                (entity.level().random.nextDouble() - 0.5) * 0.05,
                0.05,
                (entity.level().random.nextDouble() - 0.5) * 0.05
            );
        }
        
        if (amplifier >= 2) {
            // Evolution aura at max stage
            for (int i = 0; i < 4; i++) {
                double angle = (entity.tickCount + i * 1.5) * 0.1;
                double radius = entity.getBbWidth() / 2.0 + 0.3;
                double px = entity.getX() + Math.cos(angle) * radius;
                double pz = entity.getZ() + Math.sin(angle) * radius;
                
                entity.level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.SCULK_SOUL,
                    px, y + 0.3, pz,
                    0.0, 0.02, 0.0
                );
            }
        }
    }
    
    /**
     * Evolution is specifically for parasite entities.
     */
    @Override
    public boolean isParasiteOnly() {
        return true;
    }
    
    /**
     * Determines if evolution should tick this frame.
     */
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % EVOLUTION_TICK_INTERVAL == 0;
    }
}
